package nextstep.app;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nextstep.fixture.AuthorizationRequestFixture;
import nextstep.security.oauth2.AuthorizationRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.HashMap;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureWireMock(port = 8089)
class OAuth2LoginAuthenticationFilterTest {

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setupMockServer() throws Exception {
        stubForAccessToken();
        stubForGithubUser();
        stubForGoogleUser();
    }

    @Test
    @DisplayName("github redirect 된 code로 accessToken 정보를 가 session 에 저장한 후 /members/me로 정보를 조회할 수 있다.")
    void githubRedirectAndRequestGithubAccessToken() throws Exception {
        String requestUri = "/login/oauth2/code/github?code=mock_code";
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(AuthorizationRequestRepository.SESSION_NAME,
                AuthorizationRequestFixture.GITHUB.getOAuth2AuthorizationRequest()
                );

        mockMvc.perform(MockMvcRequestBuilders.get(requestUri)
                        .session(session))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/"));

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders
                .get("/members/me")
                .session(session)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
        );

        response.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.email")
                        .value("a@a.com"));

    }

    @Test
    @DisplayName("google redirect 된 code로 accessToken 정보를 가 session 에 저장한 후 /members/me로 정보를 조회할 수 있다.")
    void googleAndRequestGithubAccessToken() throws Exception {
        String requestUri = "/login/oauth2/code/google?code=mock_code";
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(AuthorizationRequestRepository.SESSION_NAME,
                AuthorizationRequestFixture.GOOGLE.getOAuth2AuthorizationRequest()
        );

        mockMvc.perform(MockMvcRequestBuilders.get(requestUri)
                        .session(session))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/"));

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders
                .get("/members/me")
                .session(session)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
        );

        response.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.email")
                        .value("a@a.com"));

    }

    private static void stubForAccessToken() throws JsonProcessingException {
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("access_token", "mock_access_token");
        responseBody.put("token_type", "bearer");
        String jsonResponse = new ObjectMapper().writeValueAsString(responseBody);

        //github용
        stubFor(post(urlEqualTo("/login/oauth/access_token"))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                        .withBody(jsonResponse)));

        //google 용
        stubFor(post(urlEqualTo("/token"))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                        .withBody(jsonResponse)));
    }

    private static void stubForGithubUser() throws JsonProcessingException {
        Map<String, String> userProfile = new HashMap<>();
        userProfile.put("email", "a@a.com");
        userProfile.put("name", "a");
        userProfile.put("avatar_url", "");
        String profileJsonResponse = new ObjectMapper().writeValueAsString(userProfile);

        stubFor(get(urlEqualTo("/user"))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                        .withBody(profileJsonResponse)));

    }

    private static void stubForGoogleUser() throws JsonProcessingException {
        Map<String, String> userProfile = new HashMap<>();
        userProfile.put("email", "a@a.com");
        userProfile.put("name", "a");
        userProfile.put("picture", "picture");
        String profileJsonResponse = new ObjectMapper().writeValueAsString(userProfile);

        stubFor(get(urlEqualTo("/oauth2/v2/userinfo"))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                        .withBody(profileJsonResponse)));
    }
}

