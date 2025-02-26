package nextstep.oauth2;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nextstep.oauth2.authentication.OAuth2AccessToken;
import nextstep.oauth2.authentication.OAuth2AuthenticationToken;
import nextstep.oauth2.client.userinfo.OAuth2User;
import nextstep.oauth2.client.userinfo.OAuth2UserRequest;
import nextstep.oauth2.exception.OAuth2RegistrationNotFoundException;
import nextstep.oauth2.http.OAuth2ApiClient;
import nextstep.oauth2.registration.ClientRegistration;
import nextstep.oauth2.registration.ClientRegistrationRepository;
import nextstep.security.access.MvcRequestMatcher;
import nextstep.security.access.RequestMatcher;
import nextstep.security.authentication.AbstractAuthenticationProcessingFilter;
import nextstep.security.authentication.Authentication;
import nextstep.security.authentication.AuthenticationException;
import nextstep.security.authentication.AuthenticationManager;
import nextstep.security.context.HttpSessionSecurityContextRepository;
import nextstep.security.context.SecurityContext;
import nextstep.security.context.SecurityContextHolder;

import java.io.IOException;

public class OAuth2LoginAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    private static final String DEFAULT_FILTER_PROCESSES_URI = "/login/oauth2/code/";
    private final RequestMatcher requestMatcherrequiresAuthenticationRequestMatcher = new MvcRequestMatcher(DEFAULT_FILTER_PROCESSES_URI);
    private final HttpSessionSecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();
    private final ClientRegistrationRepository clientRegistrationRepository;
    private final OAuth2ApiClient apiClient = new OAuth2ApiClient();

    public OAuth2LoginAuthenticationFilter(final ClientRegistrationRepository clientRegistrationRepository, AuthenticationManager authenticationManager) {
        super(authenticationManager);
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    @Override
    protected boolean requiresAuthentication(final HttpServletRequest request, final HttpServletResponse response) {
        return requestMatcherrequiresAuthenticationRequestMatcher.matches(request);
    }

    @Override
    protected Authentication attemptAuthentication(final HttpServletRequest request, final HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        final String registrationId = extractRegistrationId(request.getRequestURI(), DEFAULT_FILTER_PROCESSES_URI);
//        if (registrationId == null) {
//            doFilter(request, response, filterChain);
//            return;
//        }

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

        return authenticationToken;
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

    @Override
    protected void successfulAuthentication(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain, final Authentication authenticationResult) throws AuthenticationException, IOException, ServletException {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authenticationResult);
        SecurityContextHolder.setContext(context);
        this.securityContextRepository.saveContext(context, request, response);

        response.sendRedirect("/");
    }
}
