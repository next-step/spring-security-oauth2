# spring-security

## 🚀 1-1단계 - OAuth 2.0 Login
    Github을 통한 로그인을 구현한다. 전체 과정을 수행하기 위해 아래 순서로 구현한다.

- [x] Github Application 등록
- [x] 인증 URL 리다이렉트 필터 구현

- [x] GET /oauth2/authorization/github 요청 시 Github의 인증 URL 로 리다이렉트시키는 필터를 구현한다.
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

🚀 1-2단계 - 리팩터링 & OAuth 2.0 Resource 연동
- [x] Google 계정을 사용한 인증, 인가
- [x] 리팩터링
  - [x] Google 인증을 추가하면서 발생한 중복된 코드를 제거하고, 코드 구조를 개선한다.
  - [x] Github과 Google 인증 로직 간의 중복된 코드를 제거하고, 프레임워크의 인증 로직과 애플리케이션의 비즈니스 로직을 분리한다.
  - [x] Github과 Google의 정보를 properties(혹은 yaml)파일로 분리한다.

🚀 2-1단계 - 리다이렉트 필터
- [x] authorizationRequestResolver과 authorizationRequestRepository의 역할에 대해 이해한다.
- [x] ClientRegistrationRepository 코드를 참고하고 구현체인 InMemoryClientRegistrationRepository를 확인한다
  - [x] 다른 구현체를 사용하지 않을 계획이라면 ClientRegistrationRepository에 InMemoryClientRegistrationRepository를 구현해도 무방하다.
- [x] ClientRegistration 코드를 참고하여 구현한다.
  - [ ] ProviderDetails과 UserInfoEndpoint가 내부 클래스로 존재한다.
  - [x] 이들의 쓰임이 이해가 되지 않는다면 없이 먼저 구현한다.
  - [ ] 그 다음 필요성이 느껴지거나 이해가 된다면 그 때 내부 클래스로 구현해서 사용한다.
  - [x] 필요성이 느껴지지 않으면 구현하지 않아도 무방하다.(필요성이 느껴지지 않은 상황에서 구현할 경우 더 혼동이 올 가능성이 높음)
- [x] ClientRegistrationRepository 빈 등록을 한다.
- [x] UserDetailsService와 UserDetails를 떠올리면 이해하기 수월하다.
- [x] OAuth2UserService의 구현체를 클래스로 만들어주어도 좋고, SecurityConfig에서 추상 클래스로 직접 구현해주어도 좋다. (OAuth2User도 마찬가지)
- [x] OAuth2LoginAuthenticationFilter에서 MemberRepository를 직접 사용하고 있다면 OAuth2UserService로 대체한다.


🚀 2-2단계 - Oauth 인증 필터
- [x] doFilter를 기준으로 전체적인 큰 흐름을 잡는다. AbstractAuthenticationProcessingFilter 구조로 추상화해두지 않았다면 아래와 같이 흐름을 먼저 잡고 진행해도 좋다.
