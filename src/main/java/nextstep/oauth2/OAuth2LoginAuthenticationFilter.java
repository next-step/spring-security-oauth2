package nextstep.oauth2;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nextstep.app.domain.Member;
import nextstep.app.domain.MemberRepository;
import nextstep.app.infrastructure.InmemoryMemberRepository;
import nextstep.oauth2.registration.ClientRegistration;
import nextstep.oauth2.registration.ClientRegistrationRepository;
import nextstep.oauth2.exception.OAuth2RegistrationNotFoundException;
import nextstep.oauth2.http.OAuth2ApiClient;
import nextstep.oauth2.http.OAuth2UserResponse;
import nextstep.security.access.MvcRequestMatcher;
import nextstep.security.access.RequestMatcher;
import nextstep.security.authentication.UsernamePasswordAuthenticationToken;
import nextstep.security.context.HttpSessionSecurityContextRepository;
import nextstep.security.context.SecurityContext;
import nextstep.security.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

public class OAuth2LoginAuthenticationFilter extends OncePerRequestFilter {
    private static final String DEFAULT_FILTER_PROCESSES_URI = "/login/oauth2/code/";
    private final MemberRepository memberRepository = new InmemoryMemberRepository();
    private final HttpSessionSecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();
    private final ClientRegistrationRepository clientRegistrationRepository;
    private final OAuth2ApiClient apiClient = new OAuth2ApiClient();
    private final RequestMatcher requestMatcherrequiresAuthenticationRequestMatcher;

    public OAuth2LoginAuthenticationFilter(final ClientRegistrationRepository clientRegistrationRepository) {
        this.clientRegistrationRepository = clientRegistrationRepository;
        this.requestMatcherrequiresAuthenticationRequestMatcher = new MvcRequestMatcher(DEFAULT_FILTER_PROCESSES_URI);
    }

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain) throws ServletException, IOException {
        if (!requestMatcherrequiresAuthenticationRequestMatcher.matches(request)) {
            doFilter(request, response, filterChain);
            return;
        }

        final String registrationId = extractRegistrationId(request.getRequestURI(), DEFAULT_FILTER_PROCESSES_URI);
        if (registrationId == null) {
            doFilter(request, response, filterChain);
            return;
        }

        final ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId(registrationId);
        if (clientRegistration == null) {
            throw new OAuth2RegistrationNotFoundException(registrationId);
        }

        final String code = request.getParameter("code");
        final String token = apiClient.sendTokenRequest(clientRegistration, code);

        final OAuth2UserResponse oAuth2User = apiClient.sendUserInfoRequestWithToken(clientRegistration, token);
        final Member member = retrieveMember(oAuth2User);

        final UsernamePasswordAuthenticationToken authenticationToken = createSuccessAuthentication(member);

        saveAuthentication(request, response, authenticationToken);

        response.sendRedirect("/");
    }

    public String extractRegistrationId(String requestUri, String baseUri) {
        if (requestUri.length() <= baseUri.length()) {
            throw new IllegalArgumentException("Invalid request URI: " + requestUri);
        }
        return requestUri.substring(baseUri.length());
    }

    private Member retrieveMember(final OAuth2UserResponse oAuth2User) {
        final String email = oAuth2User.getEmail();
        final String name = oAuth2User.getName();
        final String avatarUrl = oAuth2User.getPicture();

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
