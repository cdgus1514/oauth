# 소셜로그인 프로젝트


> Spring Security   
> jpa   
> OAuth2 + JWT   
> google, kakao, naver 로그인 구현

<br>

## 1. OAuth2 로그인 요청처리 흐름

* 사용자가 로그인
* 로그인에 성공하면 Resource Server에 등록해놓은 Redirect URI(`http://{Client주소}/login/aouth2/code/{registrationId}?{code}`) 경로로 요청
* Client는 {code} 값을 이용해서 다시 Resource Server로 Access Token을 요청
* Client는 Access Token으로 다시 Resource Server로 scope에 해당하는 사용자 정보를 요청
* 사용자 정보를 이용해서 회원가입 여부 체크
  * <U>회원</U>이면 JWT로 Access Token과 Refresh Token 반환
  * 회원이 아니면 회원가입
* JWT의 Access Token이 만료된 경우 Refresh Token으로 Resource Server에게 Access Token 재발행 요청


<br>
<br>

## 2. 소셜연동 설정
```properties

# google
spring.security.oauth2.client.registration.google.client-id=
spring.security.oauth2.client.registration.google.client-secret=
spring.security.oauth2.client.registration.google.redirect-uri=http://localhost:8080/login/oauth2/code/google
spring.security.oauth2.client.registration.google.scope=profile,email


# Kakao
spring.security.oauth2.client.registration.kakao.client-id=
spring.security.oauth2.client.registration.kakao.client-secret=
spring.security.oauth2.client.registration..kakao.redirect-uri=http://localhost:8080/login/oauth2/code/kakao
spring.security.oauth2.client.registration.kakao.client-authentication-method=POST
spring.security.oauth2.client.registration.kakao.authorization-grant-type=authorization_code
spring.security.oauth2.client.registration.kakao.scope=profile_nickname, profile_image, account_email
spring.security.oauth2.client.registration.kakao.client-name=Kakao

# Provider-Kakao
spring.security.oauth2.client.provider.kakao.authorization-uri=https://kauth.kakao.com/oauth/authorize
spring.security.oauth2.client.provider.kakao.token-uri=https://kauth.kakao.com/oauth/token
spring.security.oauth2.client.provider.kakao.user-info-uri=https://kapi.kakao.com/v2/user/me
spring.security.oauth2.client.provider.kakao.user-name-attribute=id


# Naver
spring.security.oauth2.client.registration.naver.client-id=
spring.security.oauth2.client.registration.naver.client-secret=
spring.security.oauth2.client.registration.naver.redirect-uri=http://localhost:8080/login/oauth2/code/naver
spring.security.oauth2.client.registration.naver.authorization-grant-type=authorization_code
spring.security.oauth2.client.registration.naver.scope=name, email, profile_image
spring.security.oauth2.client.registration.naver.client-name=Naver

# Provider-Naver
spring.security.oauth2.client.provider.naver.authorization-uri=https://nid.naver.com/oauth2.0/authorize
spring.security.oauth2.client.provider.naver.token-uri=https://nid.naver.com/oauth2.0/token
spring.security.oauth2.client.provider.naver.user-info-uri=https://openapi.naver.com/v1/nid/me
spring.security.oauth2.client.provider.naver.user-name-attribute=response
```