package nextstep.oauth2;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

import static nextstep.oauth2.OAuth2ClientProperties.Provider;
import static nextstep.oauth2.OAuth2ClientProperties.Registration;

public class OAuth2AuthorizationRequestRedirectFilter extends OncePerRequestFilter {
    public static final String OAUTH_BASE_REQUEST_URI = "/oauth2/authorization/";
    private final OAuth2ClientProperties oAuth2ClientProperties;

    public OAuth2AuthorizationRequestRedirectFilter(final OAuth2ClientProperties oAuth2ClientProperties) {
        this.oAuth2ClientProperties = oAuth2ClientProperties;
    }

    public String extractRegistrationId(String requestUri) {
        return requestUri.substring(OAUTH_BASE_REQUEST_URI.length());
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

        final String uriString = buildRedirectURI(registration, provider);
        response.sendRedirect(uriString);
    }

    private String buildRedirectURI(final Registration registration, final Provider provider) {
        final UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(provider.getAuthorizationUri());
        uriBuilder.queryParam("response_type", "code");
        uriBuilder.queryParam("client_id", registration.getClientId());
        uriBuilder.queryParam("scope", String.join(" ", registration.getScope()));
        uriBuilder.queryParam("redirect_uri", registration.getRedirectUri());

        return uriBuilder.toUriString();
    }

    private boolean isNotStartingWithBaseUrl(final HttpServletRequest request) {
        return !request.getRequestURI().startsWith(OAUTH_BASE_REQUEST_URI);
    }
}
