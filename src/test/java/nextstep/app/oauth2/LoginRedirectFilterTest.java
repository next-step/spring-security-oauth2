package nextstep.app.oauth2;

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
class LoginRedirectFilterTest {

    @Autowired
    private MockMvc mockMvc;

    @DisplayName("GitHub OAuth2 로그인 리다이렉트에 관한 테스트")
    @Test
    void github() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/oauth2/authorization/github"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection());
    }

    @DisplayName("Google OAuth2 로그인 리다이렉트에 관한 테스트")
    @Test
    void google() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/oauth2/authorization/google"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection());
    }
}
