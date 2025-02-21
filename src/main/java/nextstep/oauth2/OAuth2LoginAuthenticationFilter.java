package nextstep.oauth2;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nextstep.app.domain.Member;
import nextstep.app.domain.MemberRepository;
import nextstep.app.infrastructure.InmemoryMemberRepository;
import nextstep.oauth2.client.ClientRegistration;
import nextstep.oauth2.client.ClientRegistrationRepository;
import nextstep.oauth2.exception.OAuth2RegistrationNotFoundException;
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

public class OAuth2LoginAuthenticationFilter extends OncePerRequestFilter {
    private static final String OAUTH_BASE_REQUEST_URI = "/login/oauth2/code/";
    private final RestClient restClient = RestClient.create();
    private final MemberRepository memberRepository = new InmemoryMemberRepository();
    private final HttpSessionSecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();
    private final ClientRegistrationRepository clientRegistrationRepository;

    public OAuth2LoginAuthenticationFilter(final ClientRegistrationRepository clientRegistrationRepository) {
        this.clientRegistrationRepository = clientRegistrationRepository;
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

        final ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId(registrationId);
        if (clientRegistration == null) {
            throw new OAuth2RegistrationNotFoundException(registrationId);
        }

        final String code = request.getParameter("code");
        final Map<String, String> tokenResponse = sendTokenRequest(clientRegistration, code);

        final String accessToken = tokenResponse.get("access_token");
        final Map<String, String> userInfoResponse = sendUserInfoRequestWithToken(clientRegistration, accessToken);

        final Member member = retrieveMember(userInfoResponse);

        final UsernamePasswordAuthenticationToken authenticationToken = createSuccessAuthentication(member);

        saveAuthentication(request, response, authenticationToken);

        response.sendRedirect("/");
    }

    private boolean isNotStartingWithBaseUrl(final HttpServletRequest request) {
        return !request.getRequestURI().startsWith(OAUTH_BASE_REQUEST_URI);
    }

    private String extractRegistrationId(String requestUri) {
        return requestUri.substring(OAUTH_BASE_REQUEST_URI.length());
    }

    public Map<String, String> sendTokenRequest(final ClientRegistration clientRegistration, final String code) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", clientRegistration.getClientId());
        body.add("client_secret", clientRegistration.getClientSecret());
        body.add("code", code);
        body.add("redirect_uri", clientRegistration.getRedirectUri());
        body.add("grant_type", "authorization_code");

        return restClient.post()
                .uri(clientRegistration.getTokenUri())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(Map.class);
    }

    public Map<String, String> sendUserInfoRequestWithToken(ClientRegistration clientRegistration, String accessToken) {
        return restClient.get()
                .uri(clientRegistration.getUserInfoUri())
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .body(Map.class);
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

    private void saveAuthentication(final HttpServletRequest request, final HttpServletResponse response, final UsernamePasswordAuthenticationToken authenticationToken) {
        final SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authenticationToken);
        SecurityContextHolder.setContext(context);
        this.securityContextRepository.saveContext(context, request, response);
    }
}
