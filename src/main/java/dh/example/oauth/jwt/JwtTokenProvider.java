package dh.example.oauth.jwt;

import dh.example.oauth.config.ExpireTime;
import dh.example.oauth.dto.UserResponseDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JwtTokenProvider {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String AUTHORITES_KEY = "auth";
    private static final String BEARER_TYPE = "Bearer";
    private static final String TYPE_ACCESS = "access";
    private static final String TYPE_REFRESH = "refresh";

    private final Key key;

    public JwtTokenProvider(@Value("${oauth.jwt.secret}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }
    
    // AccessToken과 RefreshToken 생성
    public UserResponseDto.TokenInfo generateToken(Authentication authentication) {
        return generateToken(authentication.getName(), authentication.getAuthorities());
    }

    private UserResponseDto.TokenInfo generateToken(String name, Collection<? extends GrantedAuthority> authorities) {
        System.out.println("JwtTokenProvider.generateToken");

        // 권한조회
        authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));

        Date now = new Date();
        
        
        // AccessToken 생성
        String accessToken = Jwts.builder()
                .setSubject(name)
                .claim(AUTHORITES_KEY, authorities)
                .claim("type", TYPE_ACCESS)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + ExpireTime.ACCESS_TOKEN_EXPIRE_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
        
        // RefreshToken 생성
        String refreshToken = Jwts.builder()
                .claim("type", TYPE_REFRESH)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + ExpireTime.REFRESH_TOKEN_EXPIRE_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        log.info("accessToken : {}", accessToken);
        log.info("refreshToken : {}", refreshToken);

        return UserResponseDto.TokenInfo.builder()
                .grantType(BEARER_TYPE)
                .accessToken(accessToken)
                .accessTokenExpirationTime(ExpireTime.ACCESS_TOKEN_EXPIRE_TIME)
                .refreshToken(refreshToken)
                .regreshTokenExpirationTime(ExpireTime.REFRESH_TOKEN_EXPIRE_TIME)
                .build();
    }

    public Authentication getAuthentication(String accessToken) {
        System.out.println("JwtTokenProvider.getAuthentication");

        // 복호화
        Claims claims = parseClaim(accessToken);

        if(claims.get(AUTHORITES_KEY) == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        // 정보조회
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
        
        // 객체 생성해서 반환
        UserDetails principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }



    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        }catch (SecurityException | MalformedJwtException e) {
            log.error("Invalid Token", e);
        }catch (ExpiredJwtException e) {
            log.error("Expired Token", e);
        }catch (UnsupportedJwtException e) {
            log.error("Unsupported Token", e);
        }catch (IllegalArgumentException e) {
            log.error("JWT claims is empty", e);
        }

        return false;
    }


    private Claims parseClaim(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        }catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_TYPE)) {
            return bearerToken.substring(7);
        }

        return null;
    }
}
