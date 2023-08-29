package dh.example.oauth.oauth2;

import dh.example.oauth.entity.AuthProvider;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class OAuth2USerInfoFactory {

    public static OAuth2UserInfo getOAuth2UserInfo(AuthProvider authProvider, Map<String, Object> attributes) {
        System.out.println("OAuth2USerInfoFactory.getOAuth2UserInfo");
        log.info("provider : {}", authProvider);

        switch (authProvider) {
            case GOOGLE: return new GoogleOAuth2User(attributes);
            case KAKAO: return new KakaoAuth2USer(attributes);
            case NAVER: return new NaverAuth2User(attributes);

            default: throw new IllegalArgumentException("Invalid provider type.");
        }
    }
}
