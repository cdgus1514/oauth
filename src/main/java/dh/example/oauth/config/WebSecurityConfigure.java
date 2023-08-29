package dh.example.oauth.config;

import dh.example.oauth.jwt.JwtAuthenticationFilter;
import dh.example.oauth.jwt.JwtTokenProvider;
import dh.example.oauth.oauth2.OAuth2AuthenticationFailureHandler;
import dh.example.oauth.oauth2.OAuth2AuthenticationSuccessHandler;
import dh.example.oauth.repository.CookieAuthorizationRequestRepository;
import dh.example.oauth.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class WebSecurityConfigure {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final JwtTokenProvider jwtTokenProvider;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.cors()
                .and()
                .httpBasic().disable()
                .csrf().disable()
                .formLogin().disable()
                .rememberMe().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        
        // 권한
        httpSecurity.authorizeRequests().anyRequest().permitAll();


        // oauth2 로그인
        httpSecurity.oauth2Login(ouath2Configure -> ouath2Configure.loginPage("/login")
                .successHandler(oAuth2AuthenticationSuccessHandler)
                .userInfoEndpoint()
                .userService(customOAuth2UserService));

        httpSecurity.logout()
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID");
//        httpSecurity.logout().logoutUrl("/logout").invalidateHttpSession(true).deleteCookies();


        httpSecurity.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }
}
