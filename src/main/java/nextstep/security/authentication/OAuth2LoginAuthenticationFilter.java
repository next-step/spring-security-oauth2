package nextstep.security.authentication;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nextstep.security.access.RegexRequestMatcher;
import nextstep.security.context.HttpSessionSecurityContextRepository;
import nextstep.security.context.SecurityContext;
import nextstep.security.context.SecurityContextHolder;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class OAuth2LoginAuthenticationFilter extends OncePerRequestFilter {

    private static final RegexRequestMatcher OAUTH2_AUTHENTICATION_REQUEST_MATCHER = new RegexRequestMatcher(HttpMethod.GET, "/login/oauth2/code/.*");
    private final HttpSessionSecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();
    private final ClientRegistrationRepository clientRegistrationRepository;
    private final AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository = new HttpSessionOAuth2AuthorizationRequestRepository();
    private final AuthenticationManager authenticationManager;
    private final OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository;

    private final Converter<OAuth2LoginAuthenticationToken, OAuth2AuthenticationToken> authenticationResultConverter = OAuth2LoginAuthenticationFilter.this::createAuthenticationResult;

    public OAuth2LoginAuthenticationFilter(OAuth2UserService userService, ClientRegistrationRepository clientRegistrationRepository, OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository) {
        this.authenticationManager = new ProviderManager(List.of(new OAuth2AuthenticationProvider(userService)));
        this.clientRegistrationRepository = clientRegistrationRepository;
        this.oAuth2AuthorizedClientRepository = oAuth2AuthorizedClientRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if (!OAUTH2_AUTHENTICATION_REQUEST_MATCHER.matches(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            Authentication authenticationResult = attemptAuthentication(request, response);
            if (authenticationResult == null) {
                return;
            }

            successfulAuthentication(request, response, authenticationResult);
        } catch (AuthenticationException e) {
            SecurityContextHolder.clearContext();
        }
    }

    private void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, Authentication authenticationResult) throws IOException {
        saveSecurityContext(request, response, authenticationResult);
        response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
        response.sendRedirect("/");
    }

    private Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        String code = request.getParameter("code");
        OAuth2AuthorizationRequest oAuth2AuthorizationRequest = authorizationRequestRepository.loadAuthorizationRequest(request);
        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId(oAuth2AuthorizationRequest.getClientId());

        OAuth2LoginAuthenticationToken authenticationRequest = new OAuth2LoginAuthenticationToken(clientRegistration, code);
        OAuth2LoginAuthenticationToken authenticationResult = (OAuth2LoginAuthenticationToken) authenticationManager.authenticate(authenticationRequest);

        OAuth2AuthenticationToken oAuth2Authentication = authenticationResultConverter.convert(authenticationResult);

        OAuth2AuthorizedClient oAuth2AuthorizedClient = new OAuth2AuthorizedClient(authenticationResult.getClientRegistration(), oAuth2Authentication.getPrincipal().toString(), authenticationResult.getAccessToken());
        oAuth2AuthorizedClientRepository.saveAuthorizedClient(oAuth2AuthorizedClient, oAuth2Authentication);
        return oAuth2Authentication;
    }

    private void saveSecurityContext(HttpServletRequest request, HttpServletResponse response, Authentication authenticated) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authenticated);
        SecurityContextHolder.setContext(context);
        securityContextRepository.saveContext(context, request, response);
    }

    private OAuth2AuthenticationToken createAuthenticationResult(OAuth2LoginAuthenticationToken authenticationResult) {
        return new OAuth2AuthenticationToken(authenticationResult.getPrincipal());
    }
}
