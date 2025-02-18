package nextstep.security.oauth2.github;

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

public class GoogleAuthenticationFilter extends OncePerRequestFilter {

    public static final String GOOGLE_AUTHORIZATION_REDIRECT_URI = "/login/oauth2/code/google";
    public static final String GOOGLE_USER_INFO_REQUEST_URI = "http://localhost:8089/oauth2/v1/userinfo";
    private static final String GOOGLE_ACCESS_TOKEN_REQUEST_URI = "http://localhost:8089/token";
    //    public static final String GOOGLE_ACCESS_TOKEN_REQUEST_URI = "https://oauth2.googleapis.com/token";
//    public static final String GOOGLE_USER_INFO_REQUEST_URI = "https://www.googleapis.com/oauth2/v1/userinfo";
    private static final RestClient restClient = RestClient.create();
    private final MemberRepository memberRepository = new InmemoryMemberRepository();
    private final HttpSessionSecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    private static UsernamePasswordAuthenticationToken createSuccessAuthentication(final Member member) {
        return UsernamePasswordAuthenticationToken.authenticated(member.getEmail(), member.getPassword(), member.getRoles());
    }

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain) throws ServletException, IOException {
        if (!request.getRequestURI().equals(GOOGLE_AUTHORIZATION_REDIRECT_URI)) {
            doFilter(request, response, filterChain);
            return;
        }

        final String code = request.getParameter("code");
        final Map<String, String> tokenResponse = sendPostRequest(GOOGLE_ACCESS_TOKEN_REQUEST_URI, code);
        System.out.println("tokenResponse = " + tokenResponse);

        final String accessToken = tokenResponse.get("access_token");
        final Map<String, String> userInfoResponse = sendGetRequestWithToken(GOOGLE_USER_INFO_REQUEST_URI, accessToken);
        System.out.println("userInfoResponse = " + userInfoResponse);

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
        final String avatarUrl = userInfoResponse.get("picture");

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
        body.add("client_id", "176481963463-r1n0954hb0g0tmrefbiq920k7014ka0n.apps.googleusercontent.com");
        body.add("client_secret", "secret");
        body.add("code", code);
        body.add("redirect_uri", GoogleLoginRedirectFilter.REDIRECT_URI);
        body.add("grant_type", "authorization_code");

        return restClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(Map.class);
    }
}
