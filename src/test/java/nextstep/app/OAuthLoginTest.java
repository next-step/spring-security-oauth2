package nextstep.app;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class OAuthLoginTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void oauth_authorization() throws Exception {
        ResultActions response = requestOauthAuthorizationWithGithub();
        response.andExpect(
                status().is3xxRedirection()
        );
        response.andExpect(
                redirectedUrl("https://github.com/login/oauth/authorize?client_id=04f8811a8c4aafe9baab&response_type=code&scope=read:user&redirect_uri=http://localhost:8080/login/oauth2/code/github")
        );
    }

    private ResultActions requestOauthAuthorizationWithGithub() throws Exception {
        return mockMvc.perform(get("/oauth2/authorization/github"));
    }
}
