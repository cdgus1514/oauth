package dh.example.oauth.oauth2;

import java.util.Map;

public class KakaoAuth2USer extends OAuth2UserInfo {

    public KakaoAuth2USer(Map<String, Object> attributes) {
        super((Map<String, Object>) attributes.get("kakao_account"));
    }

    @Override
    public String getOAuth2Id() {
//        return this.id.toString();
        return (String) attributes.get("id");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }
}
