package nextstep.oauth2;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nextstep.oauth2.client.ClientRegistration;
import nextstep.oauth2.client.ClientRegistrationRepository;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

public class OAuth2AuthorizationRequestRedirectFilter extends OncePerRequestFilter {
    private static final String OAUTH_BASE_REQUEST_URI = "/oauth2/authorization/";
    private final ClientRegistrationRepository clientRegistrationRepository;

    public OAuth2AuthorizationRequestRedirectFilter(final ClientRegistrationRepository clientRegistrationRepository) {
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

        final String uriString = buildRedirectURI(clientRegistration);
        response.sendRedirect(uriString);
    }

    private boolean isNotStartingWithBaseUrl(final HttpServletRequest request) {
        return !request.getRequestURI().startsWith(OAUTH_BASE_REQUEST_URI);
    }

    private String extractRegistrationId(String requestUri) {
        return requestUri.substring(OAUTH_BASE_REQUEST_URI.length());
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
