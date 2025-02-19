package nextstep.app;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import nextstep.security.context.HttpSessionSecurityContextRepository;
import nextstep.security.context.SecurityContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.HashMap;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureWireMock(port = 8089)
@ActiveProfiles("test")
class GoogleAuthenticationFilterTest {

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setupMockServer() throws Exception {
        stubForAccessToken();
        stubForUser();
    }

    @Test
    void redirectAndRequestGithubAccessToken() throws Exception {
        String requestUri = "/login/oauth2/code/google?code=mock_code";

        mockMvc.perform(MockMvcRequestBuilders.get(requestUri))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/"))
                .andExpect(request -> {
                    HttpSession session = request.getRequest().getSession();
                    assert session != null;
                    SecurityContext context = (SecurityContext) session.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
                    assertThat(context).isNotNull();
                    assertThat(context.getAuthentication()).isNotNull();
                    assertThat(context.getAuthentication().isAuthenticated()).isTrue();
                    assertThat(context.getAuthentication().getPrincipal()).isEqualTo("a@a.com");
                });
    }

    private static void stubForAccessToken() throws JsonProcessingException {
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("access_token", "mock_access_token");
        responseBody.put("expires_in", "3920");
        responseBody.put("refresh_token", "mock_refresh_token");
        responseBody.put("scope", "email profile");
        responseBody.put("token_type", "Bearer");
        responseBody.put("id_token", "mock_id_token");
        String jsonResponse = new ObjectMapper().writeValueAsString(responseBody);

        stubFor(post(urlEqualTo("/token"))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                        .withBody(jsonResponse)));
    }

    private static void stubForUser() throws JsonProcessingException {
        Map<String, String> userProfile = new HashMap<>();
        userProfile.put("email", "a@a.com");
        userProfile.put("name", "a");
        userProfile.put("picture", "");
        String profileJsonResponse = new ObjectMapper().writeValueAsString(userProfile);

        stubFor(get(urlEqualTo("/oauth2/v2/userinfo"))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                        .withBody(profileJsonResponse)));
    }
}

