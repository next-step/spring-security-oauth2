package nextstep.oauth2;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nextstep.oauth2.client.ClientRegistration;
import nextstep.oauth2.client.ClientRegistrationRepository;
import nextstep.oauth2.exception.OAuth2RegistrationNotFoundException;
import nextstep.security.access.MvcRequestMatcher;
import nextstep.security.access.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

public class OAuth2AuthorizationRequestRedirectFilter extends OncePerRequestFilter {
    public static final String DEFAULT_AUTHORIZATION_REQUEST_BASE_URI = "/oauth2/authorization/";
    private final ClientRegistrationRepository clientRegistrationRepository;
    private final RequestMatcher requiresAuthenticationRequestMatcher;

    public OAuth2AuthorizationRequestRedirectFilter(final ClientRegistrationRepository clientRegistrationRepository) {
        this.clientRegistrationRepository = clientRegistrationRepository;
        this.requiresAuthenticationRequestMatcher = new MvcRequestMatcher(DEFAULT_AUTHORIZATION_REQUEST_BASE_URI);
    }

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain) throws ServletException, IOException {
        if (!requiresAuthenticationRequestMatcher.matches(request)) {
            doFilter(request, response, filterChain);
            return;
        }

        final String registrationId = extractRegistrationId(request.getRequestURI(), DEFAULT_AUTHORIZATION_REQUEST_BASE_URI);
        if (registrationId == null) {
            doFilter(request, response, filterChain);
            return;
        }

        final ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId(registrationId);
        if (clientRegistration == null) {
            throw new OAuth2RegistrationNotFoundException(registrationId);
        }

        final String uriString = buildRedirectURI(clientRegistration);
        response.sendRedirect(uriString);
    }

    public String extractRegistrationId(String requestUri, String baseUri) {
        if (requestUri.length() <= baseUri.length()) {
            throw new IllegalArgumentException("Invalid request URI: " + requestUri);
        }
        return requestUri.substring(baseUri.length());
    }

    private String buildRedirectURI(final ClientRegistration registration) {
        final UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(registration.getAuthorizationUri());
        uriBuilder.queryParam("response_type", "code");
        uriBuilder.queryParam("client_id", registration.getClientId());
        uriBuilder.queryParam("scope", String.join(" ", registration.getScopes()));
        uriBuilder.queryParam("redirect_uri", registration.getRedirectUri());

        return uriBuilder.toUriString();
    }
}
