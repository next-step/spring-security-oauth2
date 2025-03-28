# 만들면서 배우는 Spring Security 2기 - OAuth 2.0 미션

## 1단계

### 🚀 1-1단계 - OAuth 2.0 Login

- [x] 깃헙 로그인 요청 시, 깃헙 로그인 페이지로 리다이렉트한다.
- [x] 깃헙에서 발급받은 승인 code를 이용하여 서버의 인증 처리를 진행한다.
  - [x] 발급 받은 code로 깃헙 액세스 토큰을 발급한다.
    - [x] 리다이렉트된 `/login/oauth2/code/github` 에서 code값을 추출하여 인증 토큰 생성 (OAuth2LoginFilter 구현)
    - [x] code를 인증 토큰에서 추출하여 액세스 토큰 발급을 요청하는 AuthenticationProvider 구현
  - [x] 발급 받은 깃헙 액세스 토큰으로 깃헙 사용자 리소스 조회를 요청한다.
  - [x] 조회한 사용자 리소스를 이용하여 로그인 후처리 작업을 진행한다.
    - [x] 기존 회원인 경우 세션에 로그인 정보를 저장한 뒤 "/"으로 리다이렉트.
    - [x] 신규 회원인 경우 회원 가입 처리 & 세션에 로그인 정보를 저장한 뒤 "/"으로 리다이렉트.

### 🚀 1-2단계 - 리팩터링 & OAuth 2.0 Resource 연동

- [x] 유저 정보 조회 책임을 `DefaultOAuth2UserService`로 위임
- [x] 구글 로그인 구현 전 사전 리팩토링
  - [x] OAuth2 인증 필터에서 인증 토큰에 provider 정보를 추가
    - [x] Request URI에서 provider 정보 추출
  - [x] OAuth2AuthenticationProvider에서 각 OAuth2 제공자에 맞게 액세스 토큰 취득
    - [x] GitHub Client 구현
  - [x] DefaultOAuth2UserService에서 각 OAuth2 제공자에 맞게 사용자 정보 조회
- [x] 구글 로그인 구현
  - [x] GoogleLoginRedirectFilter
  - [x] GoogleClient
    - [x] 코드를 이용한 액세스 토큰 발급
    - [x] 액세스 토큰을 이용한 사용자 정보 조회
- [x] 실제 UI로 통합 테스트 진행

## [1단계 피드백](https://github.com/next-step/spring-security-oauth2/pull/19#pullrequestreview-2652743249)

- [OAuth2 provider에 대한 직접적인 정보를 프로덕션 코드에 미노출(어떤 플랫폼을 사용할지는 추상화)](https://github.com/next-step/spring-security-oauth2/pull/19#discussion_r1976646965)
  - `provider`가 추가될 때 프로덕션 코드도 변경되어야할지에 대한 고민
- [OAuth2는 프로토콜. 플랫폼별로 동일한 스펙을 가지고 있다. 즉, 플랫폼마다 각각의 구현체를 따로 둘 필요는 없음.](https://github.com/next-step/spring-security-oauth2/pull/19#discussion_r1976648478)

### 피드백 적용

- [x] OAuth2 제공자가 추가되어도 프로덕션 코드는 변경 없도록 환경변수를 추상화하여 관리
- [x] OAuth2AuthorizationRequestRedirectFilter 필터 구현

## 2단계

### 2-1: 리다이렉트 필터

> 주요 클래스
> - OAuth2AuthorizationRequestResolver
> - AuthorizationRequestRepository
> - OAuth2AuthorizationRequest
> - ClientRegistrationRepository

- [x] ClientRegistrationRepository 구현
  - [x] <RegistrationId, ClientRegistration>를 담는 일급 컬렉션 InMemoryClientRegistrationRepository 구현
  - [x] InMemoryClientRegistrationRepository 빈 등록 시 ClientPrlaperties 주입 받아 초기화
- [x] OAuth2AuthorizationRequestResolver 구현
  - [x] OAuth2AuthorizationRequestResolver 빈 등록 시 ClientRegistrationRepository 주입 받아 초기화
- [x] AuthorizationRequestRepository 구현
  - [x] HttpSession에 OAuth2AuthorizationRequest를 저장

### 2-2: OAuth 인증 필터

> 주요 클래스
> - ClientRegistrationRepository
> - OAuth2AuthorizedClientRepository
> - AuthorizationRequestRepository
> - AuthenticationManager
> - HttpSessionSecurityContextRepository
> - Converter와 Converter<OAuth2LoginAuthenticationToken, OAuth2AuthenticationToken>

- [x] OAuth2LoginAuthenticationFilter 구현
  - [x] ClientRegistrationRepository에서 ClientRegistration 조회
  - [x] AuthorizationRequestRepository에서 AuthorizationRequest 조회
  - [x] OAuth2LoginAuthenticationToken 생성 및 AuthenticationManager로 인증 시도
- [x] OAuth2LoginAuthenticationProvider 구현
  - [x] OAuth2AuthorizationCodeAuthenticationToken 생성 
  - [x] OAuth2AuthorizationCodeAuthenticationProvider로 인증 위임.
  - [x] OAuth2UserService를 이용하여 사용자 정보 조회.
    - [x] DefaultOAuth2UserService에서 사용자 정보 조회(Google Client, GitHub Client 삭제)
- [x] OAuth2AuthorizationCodeAuthenticationProvider 구현
  - [x] OAuth2AccessTokenResponseClient를 이용하여 액세스 토큰 발급.
    - [x] OAuth2AccessTokenResponseClient 구현
- [ ] 깨지는 HTTP Session 관련 테스트 수정

## 2단계 피드백

---

# OAuth2 인증 플로우 참고

## OAuth2 인증 리다이렉트 

```mermaid
sequenceDiagram
%% [participants]
    actor u as User Browser
    participant rf as OAuth2AuthorizationRequestRedirectFilter
    participant rr as OAuth2AuthorizationRequestResolver
    participant crr as ClientRegistrationRepository
    participant ar as AuthorizationRequestRepository

%% [start sequnce]
    u ->> rf: GET /oauth2/authorization/{registrationId}
    rf ->> rr: resolve(httpServletRequest)
    rr ->> rr: resolves registrationId from request URI
    rr ->> crr: findByRegistrationId(registrationId)
    crr -->> rr: ClientRegistration
    rr ->> rr: extract Redirect URI from ClientRegistration
    rr -->> rf: OAuth2AuthorizationRequest<br>(contains OAuth2 Redirect URI)
    rf ->> ar: saveAuthorizationRequest(authorizationRequest)
    rf -->> u: returns 302 Found with Redirect URI<br>(sendRedirect(response))
    u ->> u: Redirects to OAuth2 Authorization Page
    u ->> u: User authorizes with their account.
```

## OAuth2LoginAuthenticationFilter 플로우

```mermaid
sequenceDiagram
    title OAuth2LoginAuthenticationFilter Flow

%% [participants]
    actor u as User Browser
    participant af as OAuth2LoginAuthenticationFilter
    participant ar as AuthorizationRequestRepository
    participant crr as ClientRegistrationRepository
    participant am as AuthenticationManager


%% [start sequnce]
    u ->> u: Redirects to APP Server<br>after authorizing OAuth2 Page
    u ->> af: GET /login/oauth2/code/{registrationId}?code={code}

%% get authorizationRequest 
    note over af, ar: Find OAuth2AuthorizationRequest <br>which already saved by OAuth2AuthorizationRequestRedirectFilter
    af ->> ar: removeAuthorizationRequest(req, res)
    ar -->> af: OAuth2AuthorizationRequest

%% get clientRegistration
    af ->> af: extract registrationId from authorizationRequest
    af ->> crr: findByRegistrationId(registartionId)
    crr -->> af: ClientRegistration
    af ->> af: generates authenticationRequest(=OAuth2LoginAuthenticationToken)<br>with clientRegistration, authorizationExchange

%% attempts authentication
    af ->> am: authenticate(authenticationRequest - OAuth2LoginAuthenticationToken)
    am -->> af: Authentication

%% process after successful authentication
    af ->> af: If authentcated, save SecurityContext
```

```mermaid
sequenceDiagram
    participant User
    participant OAuth2LoginAuthenticationFilter
    participant ClientRegistrationRepository
    participant AuthorizationRequestRepository
    participant AuthenticationManager
    User ->> OAuth2LoginAuthenticationFilter: 요청 전송 (인증 코드 포함)
    OAuth2LoginAuthenticationFilter ->> ClientRegistrationRepository: ClientRegistration 조회
    ClientRegistrationRepository -->> OAuth2LoginAuthenticationFilter: ClientRegistration 반환
    OAuth2LoginAuthenticationFilter ->> AuthorizationRequestRepository: AuthorizationRequest 제거 및 조회
    AuthorizationRequestRepository -->> OAuth2LoginAuthenticationFilter: AuthorizationRequest 반환
    OAuth2LoginAuthenticationFilter ->> AuthenticationManager: 인증 시도 (OAuth2LoginAuthenticationToken 전달)
```



### OAuth2LoginAuthenticationProvider 플로우

```mermaid
sequenceDiagram
    participant AuthenticationManager
    participant OAuth2LoginAuthenticationProvider
    participant OAuth2AuthorizationCodeAuthenticationProvider
    participant OAuth2AccessTokenResponseClient
    participant OAuth2UserService (DefaultOAuth2UserService)
    participant ResourceServer
    AuthenticationManager ->> OAuth2LoginAuthenticationProvider: 인증 요청 (OAuth2LoginAuthenticationToken)
    OAuth2LoginAuthenticationProvider ->> OAuth2AuthorizationCodeAuthenticationProvider: 인증 요청 (OAuth2AuthorizationCodeAuthenticationToken)
    OAuth2AuthorizationCodeAuthenticationProvider ->> OAuth2AccessTokenResponseClient: 토큰 요청 (OAuth2AuthorizationCodeGrantRequest)
    OAuth2AccessTokenResponseClient -->> OAuth2AuthorizationCodeAuthenticationProvider: OAuth2AccessTokenResponse 반환
    OAuth2AuthorizationCodeAuthenticationProvider ->> OAuth2UserService (DefaultOAuth2UserService): 사용자 정보 요청 (OAuth2UserRequest)
    OAuth2UserService (DefaultOAuth2UserService) ->> ResourceServer: 사용자 정보 요청 (GET getUserInfoUri)
    ResourceServer -->> OAuth2UserService (DefaultOAuth2UserService): 사용자 정보 응답
    OAuth2UserService (DefaultOAuth2UserService) -->> OAuth2AuthorizationCodeAuthenticationProvider: OAuth2User

```

```mermaid
sequenceDiagram
    title OAuth2LoginAuthenticationProvider authentication Flow

    %% participants
    participant am as AuthenticationManager
    participant lap as OAuth2LoginAuthenticationProvider<br>👉 Delegate autentication to codeAuthenticationProvider
    participant acap as OAuth2AuthorizationCodeAuthenticationProvider<br>👉 Request Exchanging Code To AccessToken
    participant atrc as OAuth2AccessTokenResponseClient<br>(DefaultAuthorizationCodeTokenResponseClient)
    participant us as OAuth2UserService (DefaultOAuth2UserService)

    %% start sequences
    am ->> lap: authenticate(authentication)

    %% OAuth2LoginAuthenticationProvider
    lap ->> lap: Cast authentcation to OAuth2LoginAuthenticationToken
    lap ->> lap: Genenrate 'OAuth2AuthorizationCodeAuthenticationToken'<br>contains clientRegisration & authorizationExchange
    lap ->> acap: authenticate(autentication)

    %% OAuth2AuthorizationCodeAuthenticationProvider
    acap ->> acap: Get OAuth2AuthorizationResponse<br>from authorizationExchange
    acap ->> acap: Generate 'OAuth2AuthorizationCodeGrantRequest'<br>contains clientRegistration, authorizationExchange 
    acap ->> atrc: getTokenResponse(authorizationGrantRequest)

    %% OAuth2AccessTokenResponseClient
    note over atrc, atrc: 🚀 Exchange Authorization Code to AccessToken<br>via call Authorization Server endpoint.
    atrc ->> atrc: Convert authorizationCodeGrantRequest to RequestEntity
    atrc ->> atrc: Exchange request to OAuth2AccessTokenResponse<br>via call Authorization Server endpoint.
    atrc -->> acap: returns tokenResponse    

    acap ->> acap: Generate authenticated OAuth2AuthorizationCodeAuthenticationToken
    acap -->> lap: returns OAuth2AuthorizationCodeAuthenticationToken

    %% request load user
    lap ->> lap: Extract AccessToken from OAuth2AuthorizationCodeAuthenticationToken<br>Generate OAuth2UserRequest with AccessToken
    lap ->> us: loadUser(oauth2UserRequest)

    %% UserService
    note over us, us: 🚀 Exchange AccessToken to UserInfo<br>via call Resource Server
    us ->> us: Validate UserInfoEndPoints Exists.
    us ->> us: Validate UserNameAttributeName Exists.
    us ->> us: Convert userRequest to RequestEntity<br>& Get UserAttributes via call Resource Server

    note over us, us: 👨‍💻 Devleper will implement Member Sign up process<br>using CustomOAuth2UserService if needed.

    us ->> lap: returns DefaultOAuth2SUser

    %% End of OAuth2 authentication
    lap -->> am: Authentication
    note over am, am: OAuth2LoginAuthenticationFilter saves SecurityContext<br><End of OAuth2 Authetication>
```


## OAuth2 인증에서 state값 활용 플로우

```mermaid
sequenceDiagram
    participant User
    participant App
    participant Repository
    participant OAuth Provider
    User ->> App: /oauth2/authorization/google
    App ->> Repository: Generate & Save State
    App ->> OAuth Provider: Redirect with State
    OAuth Provider ->> User: Authentication
    User ->> App: Callback with State
    App ->> Repository: Validate State
    alt Valid State
        Repository ->> App: Success
        App ->> User: Grant Access
    else Invalid State
        Repository ->> App: Failure
        App ->> User: Access Denied
    end
```

```mermaid
sequenceDiagram
    공격자 ->> 서버: 임의의 인증 요청 생성
    공격자 ->> 피해자: 특정 URL 전달 (세션 ID 고정)
    피해자 ->> 서버: URL로 인증 시도
    서버 ->> 공격자: 인증 결과 전송
```
