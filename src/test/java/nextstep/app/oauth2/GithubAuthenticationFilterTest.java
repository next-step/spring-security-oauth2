package nextstep.app.oauth2;

import com.fasterxml.jackson.databind.ObjectMapper;
import nextstep.app.domain.Member;
import nextstep.app.domain.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Map;
import java.util.stream.Stream;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureWireMock(port = 8089)
public class GithubAuthenticationFilterTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static Stream<UserStub> userProvider() {
        return Stream.of(
                new UserStub("b", "b_access_token", "b@b.com", "b", "b_avatar_url"),
                new UserStub("c", "c_access_token", "c@c.com", "c", "c_avatar_url")
        );
    }

    @BeforeEach
    void setupMockServer() {
        userProvider().forEach(this::setupUserStub);
    }

    @ParameterizedTest
    @MethodSource("userProvider")
    void authenticationFilterWithState(UserStub user) throws Exception {
        MockHttpSession session = new MockHttpSession();

        String state = extractState(session);
        String requestUri = "/login/oauth2/code/github?code=" + user.code() + "&state=" + state;

        mockMvc.perform(MockMvcRequestBuilders.get(requestUri).session(session))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/"));

        assertMember(user);
    }

    private String extractState(MockHttpSession session) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.get("/oauth2/authorization/github").session(session))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andReturn().getResponse().getHeader(HttpHeaders.LOCATION).split("&state=")[1];
    }

    private void assertMember(UserStub user) {
        Member savedMember = memberRepository.findByEmail(user.email()).orElseThrow();
        assertThat(savedMember.getEmail()).isEqualTo(user.email());
        assertThat(savedMember.getName()).isEqualTo(user.name());
    }

    private void setupUserStub(UserStub user) {
        stubFor(post(urlEqualTo("/login/oauth/access_token"))
                .withRequestBody(containing("code=" + user.code()))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                        .withBody(toJson(Map.of("access_token", user.accessToken(), "token_type", "bearer")))));

        stubFor(get(urlEqualTo("/user"))
                .withHeader("Authorization", equalTo("Bearer " + user.accessToken()))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                        .withBody(toJson(Map.of(
                                "email", user.email(),
                                "name", user.name(),
                                "avatar_url", user.avatarUrl()
                        )))));
    }

    private String toJson(Map<String, String> map) {
        try {
            return OBJECT_MAPPER.writeValueAsString(map);
        } catch (Exception e) {
            throw new RuntimeException("JSON 변환 실패", e);
        }
    }

    record UserStub(String code, String accessToken, String email, String name, String avatarUrl) {}
}