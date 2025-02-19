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
    String expectedRedirectUri = "https://account.google.com/o/oauth2/v2/auth?" +
        "scope=email%20profile&" +
        "response_type=code&" +
        "redirect_uri=http://localhost:8080/login/oauth2/code/google&" +
        "client_id=349246449409-h7hgk8kms3k8d7tgal8nesbh24h34t0d.apps.googleusercontent.com";

        mockMvc.perform(MockMvcRequestBuilders.get(requestUri))
            .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
            .andExpect(MockMvcResultMatchers.redirectedUrl(expectedRedirectUri));
  }

}