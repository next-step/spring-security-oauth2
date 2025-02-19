package nextstep.app.oauth2;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nextstep.app.domain.Member;
import nextstep.app.domain.MemberRepository;
import org.junit.jupiter.api.DisplayName;
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

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static nextstep.oauth2.web.OAuth2ParameterNames.ACCESS_TOKEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureWireMock(port = 8089)
class GoogleAuthenticationFilterTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    static Stream<UserStub> userProvider() {
        return UserStub.list().stream();
    }

    @DisplayName("Google 인증 기능 테스트")
    @ParameterizedTest
    @MethodSource("userProvider")
    void authenticationFilterWithState(UserStub user) throws Exception {
        stub(user);

        final MockHttpSession session = new MockHttpSession();
        final String state = mockMvc.perform(MockMvcRequestBuilders.get("/oauth2/authorization/google").session(session))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andReturn().getResponse().getHeader(HttpHeaders.LOCATION).split("&state=")[1];

        mockMvc.perform(MockMvcRequestBuilders.get(
                        "/login/oauth2/code/google?code=" + user.code + "&state=" + state
                ).session(session)).andDo(print())
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/"));

        final Member savedMember = memberRepository.findByEmail(user.email).get();
        assertThat(savedMember).isNotNull();
        assertThat(savedMember.getEmail()).isEqualTo(user.email);
        assertThat(savedMember.getName()).isEqualTo(user.name);
    }

    private void stub(UserStub user) throws JsonProcessingException {
        stubForAccessToken(user);
        stubForUser(user);
    }

    private void stubForAccessToken(UserStub user) throws JsonProcessingException {
        stubFor(post(
                urlEqualTo("/o/oauth2/token")
        ).willReturn(
                aResponse().withHeader(
                        HttpHeaders.CONTENT_TYPE, "application/json"
                ).withBody(new ObjectMapper().writeValueAsString(Map.of(
                        ACCESS_TOKEN, user.accessToken,
                        "token_type", "bearer"
                )))
        ));
    }

    private void stubForUser(UserStub user) throws JsonProcessingException {
        stubFor(get(urlEqualTo("/oauth2/v1/userinfo")).withHeader(
                "Authorization", equalTo("Bearer " + user.accessToken)
        ).willReturn(
                aResponse().withHeader(
                        HttpHeaders.CONTENT_TYPE, "application/json"
                ).withBody(new ObjectMapper().writeValueAsString(Map.of(
                        "email", user.email,
                        "name", user.name,
                        "picture", user.picture
                )))
        ));
    }

    private record UserStub(
            String code,
            String accessToken,
            String email,
            String name,
            String picture
    ) {
        private static final UserStub USER_B = new UserStub("b", "b_access_token", "b@b.com", "b", "b_avatar_url");
        private static final UserStub USER_C = new UserStub("c", "c_access_token", "c@c.com", "c", "c_avatar_url");

        private static List<UserStub> list() {
            return List.of(USER_B, USER_C);
        }
    }
}
