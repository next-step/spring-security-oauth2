package nextstep.app;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.net.URI;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureWireMock(port = 8089)
class GithubLoginRedirectFilterTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void redirectTest() throws Exception {
        String requestUri = "/oauth2/authorization/github";

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(requestUri))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andReturn();

        String redirectedUrl = result.getResponse().getRedirectedUrl();

        assertThat(redirectedUrl).isNotNull();
        URI uri = new URI(redirectedUrl);
        Map<String, String> params = Arrays.stream(uri.getQuery().split("&"))
                .map(param -> param.split("="))
                .collect(Collectors.toMap(
                        param -> param[0],
                        param -> param[1]
                ));

        assertThat(params.get("response_type")).isNotNull().isEqualTo("code");
        assertThat(params.get("client_id")).isNotNull().isEqualTo("Ov23lia9fJuRN9SpjM8v");
        assertThat(params.get("scope")).isNotNull().isEqualTo("read:user");
        assertThat(params.get("redirect_uri")).isNotNull().isEqualTo("http://localhost:8080/login/oauth2/code/github");
        assertThat(params).containsKey("state");
        assertThat(params.get("state")).isNotBlank();
    }
}
