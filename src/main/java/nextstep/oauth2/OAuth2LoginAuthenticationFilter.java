package nextstep.oauth2;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nextstep.oauth2.authentication.OAuth2AccessToken;
import nextstep.oauth2.authentication.OAuth2AuthenticationToken;
import nextstep.oauth2.client.userinfo.OAuth2User;
import nextstep.oauth2.client.userinfo.OAuth2UserRequest;
import nextstep.oauth2.client.userinfo.OAuth2UserService;
import nextstep.oauth2.exception.OAuth2RegistrationNotFoundException;
import nextstep.oauth2.http.OAuth2ApiClient;
import nextstep.oauth2.registration.ClientRegistration;
import nextstep.oauth2.registration.ClientRegistrationRepository;
import nextstep.security.access.MvcRequestMatcher;
import nextstep.security.access.RequestMatcher;
import nextstep.security.authentication.Authentication;
import nextstep.security.context.HttpSessionSecurityContextRepository;
import nextstep.security.context.SecurityContext;
import nextstep.security.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class OAuth2LoginAuthenticationFilter extends OncePerRequestFilter {
    private static final String DEFAULT_FILTER_PROCESSES_URI = "/login/oauth2/code/";
    private final HttpSessionSecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();
    private final ClientRegistrationRepository clientRegistrationRepository;
    private final OAuth2ApiClient apiClient = new OAuth2ApiClient();
    private final RequestMatcher requestMatcherrequiresAuthenticationRequestMatcher;
    private final OAuth2UserService oAuth2UserService;

    public OAuth2LoginAuthenticationFilter(final ClientRegistrationRepository clientRegistrationRepository, final OAuth2UserService oAuth2UserService) {
        this.clientRegistrationRepository = clientRegistrationRepository;
        this.oAuth2UserService = oAuth2UserService;
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

        final OAuth2AccessToken oAuth2AccessToken = new OAuth2AccessToken(token);
        final OAuth2UserRequest oAuth2UserRequest = new OAuth2UserRequest(clientRegistration, oAuth2AccessToken);
        final OAuth2User oAuth2User = oAuth2UserService.loadUser(oAuth2UserRequest);

        final OAuth2AuthenticationToken authenticationToken = createSuccessAuthentication(oAuth2User);

        successHandler(request, response, authenticationToken);

        response.sendRedirect("/");
    }

    public String extractRegistrationId(String requestUri, String baseUri) {
        if (requestUri.length() <= baseUri.length()) {
            throw new IllegalArgumentException("Invalid request URI: " + requestUri);
        }
        return requestUri.substring(baseUri.length());
    }

    private OAuth2AuthenticationToken createSuccessAuthentication(final OAuth2User oAuth2User) {
        return new OAuth2AuthenticationToken(oAuth2User, oAuth2User.authorities(), true);
    }

    private void successHandler(final HttpServletRequest request, final HttpServletResponse response, final Authentication authentication) {
        final SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        this.securityContextRepository.saveContext(context, request, response);
    }
}
