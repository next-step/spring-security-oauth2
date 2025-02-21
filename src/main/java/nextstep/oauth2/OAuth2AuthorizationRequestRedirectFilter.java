package nextstep.oauth2;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nextstep.oauth2.access.OAuth2RequestExtractor;
import nextstep.oauth2.access.OAuth2RequestMatcher;
import nextstep.oauth2.client.ClientRegistration;
import nextstep.oauth2.client.ClientRegistrationRepository;
import nextstep.oauth2.exception.OAuth2RegistrationNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

public class OAuth2AuthorizationRequestRedirectFilter extends OncePerRequestFilter {
    private final ClientRegistrationRepository clientRegistrationRepository;
    private final OAuth2RequestMatcher requestMatcher;

    public OAuth2AuthorizationRequestRedirectFilter(final ClientRegistrationRepository clientRegistrationRepository, final OAuth2RequestMatcher requestMatcher) {
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

        final String uriString = buildRedirectURI(clientRegistration);
        response.sendRedirect(uriString);
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
