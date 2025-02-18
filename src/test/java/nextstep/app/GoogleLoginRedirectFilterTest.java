package nextstep.app;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
class GoogleLoginRedirectFilterTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void redirectTest() throws Exception {
        String requestUri = "/oauth2/authorization/google";
        String expectedRedirectUri = "https://accounts.google.com/o/oauth2/v2/auth" +
                "?response_type=code" +
                "&client_id=176481963463-r1n0954hb0g0tmrefbiq920k7014ka0n.apps.googleusercontent.com" +
                "&scope=email%20profile" +
                "&redirect_uri=http://localhost:8080/login/oauth2/code/google";

        mockMvc.perform(MockMvcRequestBuilders.get(requestUri))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl(expectedRedirectUri));
    }
}