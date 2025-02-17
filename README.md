# spring-security

## 🚀 1-1단계 - OAuth 2.0 Login
    Github을 통한 로그인을 구현한다. 전체 과정을 수행하기 위해 아래 순서로 구현한다.

- [x] Github Application 등록
- [x] 인증 URL 리다이렉트 필터 구현

- [x] GET /oauth2/authorization/github 요청 시 Github의 인증 URL로 리다이렉트시키는 필터를 구현한다.
  - [x] LoginRedirectFilterTest의 github 테스트를 통과할 수 있어야 합니다.
- [x] Github Access Token 획득
  - [x] 리다이렉트된 URL로 이동하여 사용자가 Github 로그인을 마치면 승인 코드가 전달된다. 
  - [x] 이 코드를 사용해 Github Access Token을 얻어야 한다.

- [x] GET /login/oauth2/code/github 요청 시 승인 코드를 받아 Access Token을 요청하는 필터를 작성한다.
- [x] OAuth2 사용자 정보 조회
  - [x] Access Token을 획득한 후에는 Github API를 통해 사용자 정보를 가져와야 한다.
- [x] GET https://api.github.com/user 요청을 통해 사용자 정보를 가져오는 로직을 작성한다. 
- [x] 후처리
  - [x] 이후 프로필 정보를 가지고 회원 가입 & 로그인을 구현한다.
  - [x] 기존 멤버 정보가 있는 경우 세션에 로그인 정보를 저장한 뒤 "/"으로 리다이렉트
  - [x] 새로운 멤버인 경우 회원 가입 후 세션에 로그인 정보를 저장한 뒤 "/"으로 리다이렉트
