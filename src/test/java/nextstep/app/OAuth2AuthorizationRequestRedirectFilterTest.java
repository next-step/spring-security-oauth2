package nextstep.app;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class OAuth2AuthorizationRequestRedirectFilterTest {

    @Autowired
    private MockMvc mockMvc;

    @DisplayName("/oauth2/authorization/github 로 들어왔을 때 https://github.com/login/oauth/authorize 로 리다이렉트된다.")
    @Test
    void redirectGithubTest() throws Exception {
        String requestUri = "/oauth2/authorization/github";
        String expectedRedirectUri = "https://github.com/login/oauth/authorize" +
                "?client_id=client-id-test" +
                "&response_type=code" +
                "&scope=read:user" +
                "&redirect_uri=http://localhost:8080/login/oauth2/code/github";

        mockMvc.perform(MockMvcRequestBuilders.get(requestUri))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl(expectedRedirectUri));
    }

    @DisplayName("/oauth2/authorization/google 로 들어왔을 때 https://accounts.google.com/o/oauth2/v2/auth 로 리다이렉트된다.")
    @Test
    void redirectGoogleTest() throws Exception {
        String requestUri = "/oauth2/authorization/google";
        String expectedRedirectUri = "https://accounts.google.com/o/oauth2/v2/auth" +
                "?client_id=client-id-test" +
                "&response_type=code" +
                "&scope=https://www.googleapis.com/auth/userinfo.email%20https://www.googleapis.com/auth/userinfo.profile" +
                "&redirect_uri=http://localhost:8080/login/oauth2/code/google";

        mockMvc.perform(MockMvcRequestBuilders.get(requestUri))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl(expectedRedirectUri));
    }


    @DisplayName("/oauth2/authorization/test 로 들어왔을 때 404를 반환한다.")
    @Test
    void notFoundTest() throws Exception {
        String requestUri = "/oauth2/authorization/test";
        mockMvc.perform(MockMvcRequestBuilders.get(requestUri))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

}
