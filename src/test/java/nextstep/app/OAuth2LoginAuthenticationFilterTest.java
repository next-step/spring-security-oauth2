package nextstep.app;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import nextstep.app.domain.MemberRepository;
import nextstep.app.testsupport.BaseIntegrationTestSupport;
import nextstep.security.context.HttpSessionSecurityContextRepository;
import nextstep.security.context.SecurityContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.HashMap;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureWireMock(port = 8089)
class OAuth2LoginAuthenticationFilterTest extends BaseIntegrationTestSupport {

    @Autowired
    MemberRepository memberRepository;

    @BeforeEach
    void setupMockServer() throws Exception {
        // GitHub
        stubForGitHubAccessToken();
        stubForGitHubUser();

        // Google
        stubForGoogleAccessToken();
        stubForGoogleUser();
    }

    @Test
    void redirectAndRequestGithubAccessToken() throws Exception {
        MockHttpSession session = new MockHttpSession();

        // 세션에 OAuth2AuthorizationRequest 저장
        mockMvc.perform(get("/oauth2/authorization/github").session(session))
                .andDo(print())
                .andExpect(status().is3xxRedirection());

        // Authorization Endpoint 인증 후 인증 코드를 'mock_code'로 발급 받았다고 가정
        ResultActions result = mockMvc.perform(get("/login/oauth2/code/github?code=mock_code").session(session))
                .andDo(print());

        result
                .andExpect(status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/"));

        SecurityContext context = (SecurityContext) session.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
        assertThat(context).isNotNull();
        assertThat(context.getAuthentication()).isNotNull();
        assertThat(context.getAuthentication().isAuthenticated()).isTrue();
    }

    @Test
    void redirectAndRequestGoogleAccessToken() throws Exception {
        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(get("/oauth2/authorization/google").session(session))
                .andDo(print())
                .andExpect(status().is3xxRedirection());

        ResultActions result = mockMvc.perform(get("/login/oauth2/code/google?code=mock_code").session(session))
                .andDo(print());

        result
                .andExpect(status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/")); // 로그인 성공 시 루트로 리다이렉트

        SecurityContext context = (SecurityContext) session.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
        assertThat(context).isNotNull();
        assertThat(context.getAuthentication()).isNotNull();
        assertThat(context.getAuthentication().isAuthenticated()).isTrue();
    }

    private void stubForGitHubAccessToken() throws JsonProcessingException {
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("access_token", "mock_access_token");
        responseBody.put("token_type", "bearer");
        String jsonResponse = new ObjectMapper().writeValueAsString(responseBody);

        stubFor(WireMock.post(urlPathEqualTo("/login/oauth/access_token"))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                        .withBody(jsonResponse)));
    }

    private void stubForGitHubUser() throws JsonProcessingException {
        Map<String, String> userProfile = new HashMap<>();
        userProfile.put("login", "a");
        userProfile.put("id", "999");
        userProfile.put("email", "a@a.com");
        userProfile.put("avatar_url", "");
        String profileJsonResponse = new ObjectMapper().writeValueAsString(userProfile);

        stubFor(WireMock.get(urlPathMatching("/user"))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                        .withBody(profileJsonResponse)));
    }

    private void stubForGoogleAccessToken() throws JsonProcessingException {
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("access_token", "mock_google_access_token");
        responseBody.put("token_type", "Bearer");
        String jsonResponse = new ObjectMapper().writeValueAsString(responseBody);

        stubFor(WireMock.post(urlPathEqualTo("/token"))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                        .withBody(jsonResponse)));
    }

    private void stubForGoogleUser() throws JsonProcessingException {
        Map<String, String> userProfile = new HashMap<>();
        userProfile.put("id", "google-identifier");
        String profileJsonResponse = new ObjectMapper().writeValueAsString(userProfile);

        stubFor(WireMock.get(urlPathMatching("/oauth2/v2/userinfo"))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                        .withBody(profileJsonResponse)));
    }
}
