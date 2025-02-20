package nextstep.security.oauth2;

import nextstep.security.SecurityApplicationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@AutoConfigureMockMvc
@SecurityApplicationTest
class OAuth2AuthorizationRequestRedirectFilterTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void githubRedirectTest() throws Exception {
        String requestUri = "/oauth2/authorization/github";

        mockMvc.perform(MockMvcRequestBuilders.get(requestUri))
               .andExpect(MockMvcResultMatchers.status().is3xxRedirection());
    }

    @Test
    void googleRedirectTest() throws Exception {
        String requestUri = "/oauth2/authorization/google";

        mockMvc.perform(MockMvcRequestBuilders.get(requestUri))
               .andExpect(MockMvcResultMatchers.status().is3xxRedirection());
    }
}
