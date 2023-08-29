package dh.example.oauth.service;

import dh.example.oauth.entity.AuthProvider;
import dh.example.oauth.entity.Role;
import dh.example.oauth.entity.User;
import dh.example.oauth.oauth2.OAuth2USerInfoFactory;
import dh.example.oauth.oauth2.OAuth2UserInfo;
import dh.example.oauth.oauth2.UserPrincipal;
import dh.example.oauth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.*;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;


@RequiredArgsConstructor
@Service
@Slf4j
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;


    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println("CustomOAuth2UserService.loadUser");
        OAuth2UserService oAuth2UserService = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = oAuth2UserService.loadUser(userRequest);

        return processOAuth2User(userRequest, oAuth2User);

    }

    protected OAuth2User processOAuth2User(OAuth2UserRequest request, OAuth2User oAuth2User) {
        System.out.println("CustomOAuth2UserService.processOAuth2User");
        AuthProvider authProvider = AuthProvider.valueOf(request.getClientRegistration().getRegistrationId().toUpperCase());
        OAuth2UserInfo oAuth2UserInfo = OAuth2USerInfoFactory.getOAuth2UserInfo(authProvider, oAuth2User.getAttributes());


        if(!StringUtils.hasText(oAuth2UserInfo.getEmail())) {
            throw new RuntimeException("Email not found");
        }

        User user = userRepository.findByEmail(oAuth2UserInfo.getEmail()).orElse(null);

        if(user != null) {
            if(!user.getAuthProvider().equals(authProvider)) {
                throw new RuntimeException("This email already exists.");
            }

            user = updateUser(user, oAuth2UserInfo);
        }else{
            user = registUser(authProvider, oAuth2UserInfo);
        }

        userRepository.save(user);

        return UserPrincipal.create(user, oAuth2UserInfo.getAttributes());
    }


    private User registUser(AuthProvider authProvider, OAuth2UserInfo userInfo) {
        System.out.println("CustomOAuth2UserService.registUser");
        User user = User.builder()
                .email(userInfo.getEmail())
                .name(userInfo.getName())
                .oauth2Id(userInfo.getOAuth2Id())
                .authProvider(authProvider)
                .role(Role.ROLE_USER)
                .build();

        return userRepository.save(user);
    }

    private User updateUser(User user, OAuth2UserInfo userInfo) {
        return userRepository.save(user.update(userInfo));
    }
}
