## 미션 3-1 질문

### 시스템에 저장하는 username은 어떻게 정할지?

### OAuth2를 이용한 로그인 시, 임의로 생성된 username으로 로그인이 가능하게 할 것인가?

### authentication 패키지와 provider 패키지 양방향 참조.

---

## 미션 3-2 진행하며 생긴 궁금증 

> PR 코멘트로 따로 질문드릴 예정입니다..!

### Spring Security에서 어떤 필드는 주입 받고, 어떤 필드는 자체적으로 생성하는 이유?

- OAuth2AuthorizationRequestRedirectFilter 에서, 
  - OAuth2AuthorizationRequestResolver는 주입받지 않고, clientRegistrationRepository만 주입받는 이유?
- OAuth2AuthorizationRequestResolver는 OAuth2AuthorizationRequestRedirectFilter만 쓰이고, clientRegistrationRepository는 다른 곳에서도 쓰이기 때문일 것으로 추측.   

### AuthorizationRequestRepository는 왜 필요한가?

- state값 검증과 HttpSession에 OAuth2AuthorizationRequest를 저장하는 역할을 한다.

### OAuth2LoginAuthenticationFilter 

// ... 
