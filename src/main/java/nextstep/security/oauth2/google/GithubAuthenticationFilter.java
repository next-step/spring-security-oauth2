package nextstep.security.oauth2.google;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nextstep.app.domain.Member;
import nextstep.app.domain.MemberRepository;
import nextstep.app.infrastructure.InmemoryMemberRepository;
import nextstep.security.authentication.UsernamePasswordAuthenticationToken;
import nextstep.security.context.HttpSessionSecurityContextRepository;
import nextstep.security.context.SecurityContext;
import nextstep.security.context.SecurityContextHolder;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class GithubAuthenticationFilter extends OncePerRequestFilter {
    public static final String GITHUB_AUTHORIZATION_REDIRECT_URI = "/login/oauth2/code/github";
        public static final String GITHUB_USER_INFO_REQUEST_URI = "http://localhost:8089/user";
    private static final String GITHUB_ACCESS_TOKEN_REQUEST_URI = "http://localhost:8089/login/oauth/access_token";
//    public static final String GITHUB_ACCESS_TOKEN_REQUEST_URI = "https://github.com/login/oauth/access_token";
//    public static final String GITHUB_USER_INFO_REQUEST_URI = "https://api.github.com/user";
    private static final RestClient restClient = RestClient.create();
    private final MemberRepository memberRepository = new InmemoryMemberRepository();
    private final HttpSessionSecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    private static UsernamePasswordAuthenticationToken createSuccessAuthentication(final Member member) {
        return UsernamePasswordAuthenticationToken.authenticated(member.getEmail(), member.getPassword(), member.getRoles());
    }

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain) throws ServletException, IOException {
        if (!request.getRequestURI().equals(GITHUB_AUTHORIZATION_REDIRECT_URI)) {
            doFilter(request, response, filterChain);
            return;
        }

        final String code = request.getParameter("code");
        final Map<String, String> tokenResponse = sendPostRequest(GITHUB_ACCESS_TOKEN_REQUEST_URI, code);

        final String accessToken = tokenResponse.get("access_token");
        final Map<String, String> userInfoResponse = sendGetRequestWithToken(GITHUB_USER_INFO_REQUEST_URI, accessToken);

        final Member member = retrieveMember(userInfoResponse);

        final UsernamePasswordAuthenticationToken authenticationToken = createSuccessAuthentication(member);

        saveAuthentication(request, response, authenticationToken);

        response.sendRedirect("/");
    }

    private void saveAuthentication(final HttpServletRequest request, final HttpServletResponse response, final UsernamePasswordAuthenticationToken authenticationToken) {
        final SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authenticationToken);
        SecurityContextHolder.setContext(context);
        this.securityContextRepository.saveContext(context, request, response);
    }

    private Member retrieveMember(final Map<String, String> userInfoResponse) {
        final String email = userInfoResponse.get("email");
        final String name = userInfoResponse.get("name");
        final String avatarUrl = userInfoResponse.get("avatar_url");

        final Member member = memberRepository.findByEmail(email)
                .orElseGet(() -> memberRepository.save(new Member(email, "", name, avatarUrl, Set.of())));
        return member;
    }

    public Map<String, String> sendGetRequestWithToken(String url, String accessToken) {
        return restClient.get()
                .uri(url)
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .body(Map.class);
    }

    public Map<String, String> sendPostRequest(String url, String code) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", "Ov23liWTGdV9MeJBRLwC");
        body.add("client_secret", "secret");
        body.add("code", code);
        body.add("redirect_uri", GithubLoginRedirectFilter.REDIRECT_URI);

        return restClient.post()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(Map.class);
    }
}
