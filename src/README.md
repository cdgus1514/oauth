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
