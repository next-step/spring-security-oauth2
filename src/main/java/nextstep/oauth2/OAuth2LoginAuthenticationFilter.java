package nextstep.oauth2;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nextstep.app.domain.Member;
import nextstep.app.domain.MemberRepository;
import nextstep.app.infrastructure.InmemoryMemberRepository;
import nextstep.oauth2.access.OAuth2RequestExtractor;
import nextstep.oauth2.access.OAuth2RequestMatcher;
import nextstep.oauth2.client.ClientRegistration;
import nextstep.oauth2.client.ClientRegistrationRepository;
import nextstep.oauth2.exception.OAuth2RegistrationNotFoundException;
import nextstep.oauth2.http.OAuth2ApiClient;
import nextstep.oauth2.http.OAuth2User;
import nextstep.security.authentication.UsernamePasswordAuthenticationToken;
import nextstep.security.context.HttpSessionSecurityContextRepository;
import nextstep.security.context.SecurityContext;
import nextstep.security.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class OAuth2LoginAuthenticationFilter extends OncePerRequestFilter {
    private final MemberRepository memberRepository = new InmemoryMemberRepository();
    private final HttpSessionSecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();
    private final ClientRegistrationRepository clientRegistrationRepository;
    private final OAuth2ApiClient apiClient = new OAuth2ApiClient();
    private final OAuth2RequestMatcher requestMatcher;

    public OAuth2LoginAuthenticationFilter(final ClientRegistrationRepository clientRegistrationRepository, final OAuth2RequestMatcher requestMatcher) {
        this.clientRegistrationRepository = clientRegistrationRepository;
        this.requestMatcher = requestMatcher;
    }

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain) throws ServletException, IOException {
        if (!requestMatcher.matches(request)) {
            doFilter(request, response, filterChain);
            return;
        }

        final String registrationId = OAuth2RequestExtractor.extractRegistrationId(request.getRequestURI(), requestMatcher.getBaseRequestUri());
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

        final OAuth2User oAuth2User  = apiClient.sendUserInfoRequestWithToken(clientRegistration, token);
        final Member member = retrieveMember(oAuth2User);

        final UsernamePasswordAuthenticationToken authenticationToken = createSuccessAuthentication(member);

        saveAuthentication(request, response, authenticationToken);

        response.sendRedirect("/");
    }

    private Member retrieveMember(final OAuth2User oAuth2User) {
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
