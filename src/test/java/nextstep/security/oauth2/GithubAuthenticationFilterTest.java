package nextstep.security.oauth2;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import nextstep.app.domain.Member;
import nextstep.app.domain.MemberRepository;
import nextstep.security.SecurityApplicationTest;
import nextstep.security.authentication.Authentication;
import nextstep.security.context.HttpSessionSecurityContextRepository;
import nextstep.security.context.SecurityContext;
import nextstep.security.oauth2.client.ClientRegistration;
import nextstep.security.oauth2.client.ClientRegistrations;
import nextstep.security.oauth2.exchange.HttpSessionOAuth2AuthorizationRequestRepository;
import nextstep.security.oauth2.exchange.OAuth2AuthorizationRequest;
import nextstep.security.oauth2.user.OAuth2User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@SecurityApplicationTest
@AutoConfigureMockMvc
@AutoConfigureWireMock(port = 8089)
class GithubAuthenticationFilterTest {

    private static final String DEFAULT_AUTHORIZATION_REQUEST_ATTR_NAME = HttpSessionOAuth2AuthorizationRequestRepository.class
            .getName() + ".AUTHORIZATION_REQUEST";

    private final Member TEST_USER_MEMBER = new Member("a@a.com", "password", "a", "", Set.of("USER"));

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ClientRegistrations clientRegistrations;

    @BeforeEach
    void beforeEach() throws Exception {
        memberRepository.save(TEST_USER_MEMBER);
        stubForAccessToken();
        stubForUser();
    }

    @DisplayName("AuthorizeRequest가 없으면 예외가 발생한다. ")
    @Test
    void expectExceptionWhenNoAuthorizeRequestIsNull() throws Exception {
        String requestUri = "/login/oauth2/code/github?code=mock_code";

        mockMvc.perform(MockMvcRequestBuilders.get(requestUri))
               .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @DisplayName("Client Registration을 찾을 수 없으면 예외가 발생한다. ")
    @Test
    void expectExceptionWhenNoClientRegistration() throws Exception {
        OAuth2AuthorizationRequest authorizationRequest = OAuth2AuthorizationRequest.builder()
                                                                                    .registrationId("invalid")
                                                                                    .build();

        MockHttpSession mockSession = new MockHttpSession();
        mockSession.setAttribute(DEFAULT_AUTHORIZATION_REQUEST_ATTR_NAME, authorizationRequest);
        String requestUri = "/login/oauth2/code/github?code=mock_code";

        mockMvc.perform(MockMvcRequestBuilders.get(requestUri)
                                              .session(mockSession))
               .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @DisplayName("Github 로그인 후 인증이 완료되면 인증된 사용자 정보를 반환한다. ")
    @Test
    void loginAndAuthorizeWithGithub() throws Exception {
        ClientRegistration github = clientRegistrations.getMatchingRegistration("github");
        String state = "mock_state";
        String authorizationRequestUri = UriComponentsBuilder.fromHttpUrl(github.getAuthorizationUri())
                                                             .queryParam("client_id", github.getClientId())
                                                             .queryParam("response_type", "code")
                                                             .queryParam("scope", github.getScopes())
                                                             .queryParam("redirect_uri", github.getRedirectUri())
                                                             .queryParam("state", state)
                                                             .build().toUriString();

        OAuth2AuthorizationRequest authorizationRequest = OAuth2AuthorizationRequest.builder()
                                                                                    .registrationId(github.getRegistrationId())
                                                                                    .clientId(github.getClientId())
                                                                                    .redirectUri(github.getRedirectUri())
                                                                                    .scopes(github.getScopes())
                                                                                    .state(state)
                                                                                    .authorizationRequestUri(authorizationRequestUri)
                                                                                    .build();

        MockHttpSession mockSession = new MockHttpSession();
        mockSession.setAttribute(DEFAULT_AUTHORIZATION_REQUEST_ATTR_NAME, authorizationRequest);
        String requestUri = "/login/oauth2/code/github?code=mock_code&state=" + state;

        // 인증 성공
        mockMvc.perform(MockMvcRequestBuilders.get(requestUri)
                                              .session(mockSession))
               .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
               .andExpect(MockMvcResultMatchers.redirectedUrl("/"))
               .andExpect(request -> {
                   HttpSession session = request.getRequest().getSession();
                   assert session != null;
                   SecurityContext context = (SecurityContext) session.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
                   assertThat(context).isNotNull();

                   Authentication authentication = context.getAuthentication();
                   assertThat(authentication.isAuthenticated()).isTrue();

                   OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
                   assertThat(oAuth2User.getEmail()).isEqualTo("a@a.com");
                   assertThat(oAuth2User.getName()).isEqualTo("a");
               });

        // 사용자 정보 조회 가능
        mockMvc.perform(MockMvcRequestBuilders.get("/members/me")
                                              .session(mockSession))
               .andExpect(MockMvcResultMatchers.status().isOk())
               .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("a@a.com"));

        // 어드민 정보 조회 불가능
        mockMvc.perform(MockMvcRequestBuilders.get("/search")
                                              .session(mockSession))
               .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    private static void stubForAccessToken() throws JsonProcessingException {
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("access_token", "mock_access_token");
        responseBody.put("token_type", "bearer");
        String jsonResponse = new ObjectMapper().writeValueAsString(responseBody);

        stubFor(post(urlPathEqualTo("/login/oauth/access_token"))
                        .willReturn(aResponse()
                                            .withHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                                            .withBody(jsonResponse)));
    }

    private static void stubForUser() throws JsonProcessingException {
        Map<String, String> userProfile = new HashMap<>();
        userProfile.put("email", "a@a.com");
        userProfile.put("name", "a");
        userProfile.put("avatar_url", "");
        String profileJsonResponse = new ObjectMapper().writeValueAsString(userProfile);

        stubFor(get(urlPathEqualTo("/user"))
                        .willReturn(aResponse()
                                            .withHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                                            .withBody(profileJsonResponse)));
    }
}
