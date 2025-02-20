package nextstep.app;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
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

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureWireMock(port = 8089)
class GoogleAuthenticationFilterTest {

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setupMockServer() throws Exception {
        stubForAccessToken();
        stubForUser();
    }

    @Test
    void redirectAndRequestGoogleAccessToken() throws Exception {
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
        responseBody.put("token_type", "bearer");
        String jsonResponse = new ObjectMapper().writeValueAsString(responseBody);

        stubFor(post(urlEqualTo("/oauth2.googleapis.com/token"))
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

        stubFor(get(urlEqualTo("/oauth2/v1/userinfo"))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                        .withBody(profileJsonResponse)));
    }
}

