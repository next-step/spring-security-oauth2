package nextstep.app;

import nextstep.app.testsupport.BaseIntegrationTestSupport;
import nextstep.oauth2.OAuth2ClientProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OAuth2AuthorizationRequestRedirectFilterTest extends BaseIntegrationTestSupport {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    OAuth2ClientProperties oAuth2Properties;

    @Test
    void github_redirectTest() throws Exception {
        String requestUri = "/oauth2/authorization/github";
        String expectedRedirectUri = "http://localhost:8089/login/oauth/authorize" +
                "?client_id=" + oAuth2Properties.getRegistration().get("github").clientId() +
                "&response_type=code" +
                "&scope=read:user" +
                "&redirect_uri=http://localhost:8080/login/oauth2/code/github";

        ResultActions result = mockMvc.perform(get(requestUri))
                .andDo(print());

        result
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(expectedRedirectUri));
    }

    @Test
    void google_redirectTest() throws Exception {
        String requestUri = "/oauth2/authorization/google";
        String expectedRedirectUri = "http://localhost:8089/o/oauth2/v2/auth" +
                "?client_id=" + oAuth2Properties.getRegistration().get("google").clientId() +
                "&response_type=code" +
                "&scope=https://www.googleapis.com/auth/userinfo.profile https://www.googleapis.com/auth/userinfo.email" +
                "&redirect_uri=http://localhost:8080/login/oauth2/code/google";

        ResultActions result = mockMvc.perform(get(requestUri))
                .andDo(print());

        result
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(expectedRedirectUri));
    }
}
