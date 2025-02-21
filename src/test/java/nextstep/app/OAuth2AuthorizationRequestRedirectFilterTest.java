package nextstep.app;

import nextstep.oauth2.OAuth2ClientProperties;
import nextstep.oauth2.OAuth2ClientProperties.Provider;
import nextstep.oauth2.OAuth2ClientProperties.Registration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class OAuth2AuthorizationRequestRedirectFilterTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OAuth2ClientProperties oAuth2ClientProperties;

    @Test
    @DisplayName("Github OAuth2 인증 요청이 올바르게 리다이렉트되는지 검증")
    void redirectGithubTest() throws Exception {
        assertRedirect("github");
    }

    @Test
    @DisplayName("Google OAuth2 인증 요청이 올바르게 리다이렉트되는지 검증")
    void redirectGoogleTest() throws Exception {
        assertRedirect("google");
    }

    private void assertRedirect(String registrationId) throws Exception {
        String requestUri = "/oauth2/authorization/" + registrationId;
        final Registration registration = oAuth2ClientProperties.getRegistration().get(registrationId);
        final Provider provider = oAuth2ClientProperties.getProvider().get(registrationId);

        String expectedRedirectUri = provider.getAuthorizationUri() +
                "?response_type=code" +
                "&client_id=" + registration.getClientId() +
                "&scope=" + String.join("%20", registration.getScope()) +
                "&redirect_uri=" + registration.getRedirectUri();

        mockMvc.perform(MockMvcRequestBuilders.get(requestUri))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl(expectedRedirectUri));
    }
}
