package nextstep.app;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.stream.Stream;

@SpringBootTest
@AutoConfigureMockMvc
class Oauth2RedirectFilterTest {

    @Autowired
    private MockMvc mockMvc;

    @ParameterizedTest
    @MethodSource("REQUEST_URI_AND_REDIRECT_URI")
    @DisplayName("OAUTH 인증서버에 맞게 리다이렉트 시켜준다")
    void redirectTest(String requestUri, String expectedRedirectUri) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(requestUri))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl(expectedRedirectUri));
    }


    private static Stream<Arguments> REQUEST_URI_AND_REDIRECT_URI() throws Exception {
        return Stream.of(
                Arguments.of("/oauth2/authorization/github", "https://github.com/login/oauth/authorize" +
                        "?client_id=Ov23liTBhugSIcf8VX1v" +
                        "&response_type=code" +
                        "&scope=read:user" +
                        "&redirect_uri=http://localhost:8080/login/oauth2/code/github"),
                Arguments.of("/oauth2/authorization/google", "https://accounts.google.com/o/oauth2/v2/auth" +
                        "?client_id=Ov23liTBhugSIcf8VX1v" +
                        "&response_type=code" +
                        "&scope=https%3A//www.googleapis.com/auth/drive.metadata.readonly%20https%3A//www.googleapis.com/auth/calendar.readonly" +
                        "&redirect_uri=http://localhost:8080/login/oauth2/code/google")
        );
    }
}

