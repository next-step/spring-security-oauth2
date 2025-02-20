package nextstep.security.oauth2;

import nextstep.app.domain.Member;
import nextstep.security.SecurityApplicationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Set;

@AutoConfigureMockMvc
@SecurityApplicationTest
class OAuth2AuthorizationRequestRedirectFilterTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void githubRedirectTest() throws Exception {
        String requestUri = "/oauth2/authorization/github";
        String expectedRedirectUri = "https://github.com/login/oauth/authorize" +
                "?client_id=..." +
                "&response_type=code" +
                "&scope=read:user" +
                "&redirect_uri=http://localhost:8089/login/oauth2/code/github";

        mockMvc.perform(MockMvcRequestBuilders.get(requestUri))
               .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
               .andExpect(MockMvcResultMatchers.redirectedUrl(expectedRedirectUri));
    }

    @Test
    void googleRedirectTest() throws Exception {
        String requestUri = "/oauth2/authorization/google";
        String expectedRedirectUri = "https://accounts.google.com/o/oauth2/auth" +
                "?client_id=..." +
                "&response_type=code" +
                "&scope=https://www.googleapis.com/auth/userinfo.profile https://www.googleapis.com/auth/userinfo.email" +
                "&redirect_uri=http://localhost:8089/login/oauth2/code/google";

        mockMvc.perform(MockMvcRequestBuilders.get(requestUri))
               .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
               .andExpect(MockMvcResultMatchers.redirectedUrl(expectedRedirectUri));
    }
}
