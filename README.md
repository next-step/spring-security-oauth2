# spring-security
## 🚀 1-1단계 - OAuth 2.0 Login

- [x] Github Application 등록
- [x] 인증 URL 리다이렉트 필터 구현
- [x] Github Access Token 획득
- [x] OAuth2 사용자 정보 조회
- [x] 후처리
  - [x] 이후 프로필 정보를 가지고 회원 가입 & 로그인을 구현한다.
  - [x] 기존 멤버 정보가 있는 경우 세션에 로그인 정보를 저장한 뒤 "/"으로 리다이렉트
  - [x] 새로운 멤버인 경우 회원 가입 후 세션에 로그인 정보를 저장한 뒤 "/"으로 리다이렉트

## 🚀 1-2단계 - 리팩터링 & OAuth 2.0 Resource 연동
- [x] Google 계정을 사용한 인증, 인가
- [x] 리팩터링 Google 인증을 추가하면서 발생한 중복된 코드를 제거하고, 코드 구조를 개선한다.
- [x] Github과 Google 인증 로직 간의 중복된 코드를 제거하고, 프레임워크의 인증 로직과 애플리케이션의 비즈니스 로직을 분리한다.
- [x] Github과 Google의 정보를 properties(혹은 yaml)파일로 분리한다.

## 🚀 2-1단계 - 리다이렉트 필터
- [x] OAuth2AuthorizationRequestRedirectFilter 구현

## 🚀 2-2단계 - OAuth 인증 필터
- [x] OAuth2LoginAuthenticationFilter 구현
