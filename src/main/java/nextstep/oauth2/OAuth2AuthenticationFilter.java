package nextstep.oauth2;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nextstep.app.domain.Member;
import nextstep.app.domain.MemberRepository;
import nextstep.app.infrastructure.InmemoryMemberRepository;
import nextstep.oauth2.OAuth2ClientProperties.Provider;
import nextstep.oauth2.OAuth2ClientProperties.Registration;
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

public class OAuth2AuthenticationFilter extends OncePerRequestFilter {
    private static final String OAUTH_BASE_REQUEST_URI = "/login/oauth2/code/";
    private final OAuth2ClientProperties oAuth2ClientProperties;
    private final RestClient restClient = RestClient.create();
    private final MemberRepository memberRepository = new InmemoryMemberRepository();
    private final HttpSessionSecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    public OAuth2AuthenticationFilter(final OAuth2ClientProperties oAuth2ClientProperties) {
        this.oAuth2ClientProperties = oAuth2ClientProperties;
    }

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain) throws ServletException, IOException {
        if (isNotStartingWithBaseUrl(request)) {
            doFilter(request, response, filterChain);
            return;
        }

        final String registrationId = extractRegistrationId(request.getRequestURI());
        if (registrationId == null) {
            doFilter(request, response, filterChain);
            return;
        }

        final Registration registration = oAuth2ClientProperties.findRegistration(registrationId);
        final Provider provider = oAuth2ClientProperties.findProvider(registrationId);

        final String code = request.getParameter("code");
        final Map<String, String> tokenResponse = sendPostRequest(registration, provider, code);

        final String accessToken = tokenResponse.get("access_token");
        final Map<String, String> userInfoResponse = sendGetRequestWithToken(provider, accessToken);

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

    private UsernamePasswordAuthenticationToken createSuccessAuthentication(final Member member) {
        return UsernamePasswordAuthenticationToken.authenticated(member.getEmail(), member.getPassword(), member.getRoles());
    }

    public Map<String, String> sendGetRequestWithToken(Provider provider, String accessToken) {
        return restClient.get()
                .uri(provider.getUserInfoUri())
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .body(Map.class);
    }

    public Map<String, String> sendPostRequest(Registration registration, Provider provider, String code) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", registration.getClientId());
        body.add("client_secret", registration.getClientSecret());
        body.add("code", code);
        body.add("redirect_uri", registration.getRedirectUri());
        body.add("grant_type", "authorization_code");

        return restClient.post()
                .uri(provider.getTokenUri())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(Map.class);
    }

    private boolean isNotStartingWithBaseUrl(final HttpServletRequest request) {
        return !request.getRequestURI().startsWith(OAUTH_BASE_REQUEST_URI);
    }

    public String extractRegistrationId(String requestUri) {
        return requestUri.substring(OAUTH_BASE_REQUEST_URI.length());
    }
}
