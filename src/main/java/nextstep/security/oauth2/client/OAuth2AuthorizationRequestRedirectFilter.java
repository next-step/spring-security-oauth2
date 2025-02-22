package nextstep.security.oauth2.client;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import nextstep.security.oauth2.client.registration.ClientRegistrationRepository;
import nextstep.security.oauth2.core.OAuth2AuthenticationException;
import nextstep.security.oauth2.core.OAuth2AuthorizationRequest;
import org.springframework.util.Assert;
import org.springframework.web.filter.OncePerRequestFilter;

public class OAuth2AuthorizationRequestRedirectFilter extends OncePerRequestFilter {
    public static final String DEFAULT_AUTHORIZATION_REQUEST_BASE_URI = "/oauth2/authorization/";
    private final OAuth2AuthorizationRequestResolver authorizationRequestResolver;
    private final AuthorizationRequestRepository authorizationRequestRepository = new HttpSessionOAuth2AuthorizationRequestRepository();

    public OAuth2AuthorizationRequestRedirectFilter(ClientRegistrationRepository clientRegistrationRepository) {
        this(clientRegistrationRepository, DEFAULT_AUTHORIZATION_REQUEST_BASE_URI);
    }

    public OAuth2AuthorizationRequestRedirectFilter(ClientRegistrationRepository clientRegistrationRepository,
                                                    String authorizationRequestBaseUri) {
        this.authorizationRequestResolver = new DefaultOAuth2AuthorizationRequestResolver(clientRegistrationRepository,
                authorizationRequestBaseUri);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            OAuth2AuthorizationRequest authorizationRequest = this.authorizationRequestResolver.resolve(request);
            if (authorizationRequest != null) {
                sendRedirectForAuthorization(request, response, authorizationRequest);
                return;
            }
        } catch (Exception e) {
            throw new OAuth2AuthenticationException();
        }
        filterChain.doFilter(request, response);
    }

    private void sendRedirectForAuthorization(HttpServletRequest request, HttpServletResponse response,
                                              OAuth2AuthorizationRequest authorizationRequest) throws IOException {
        this.authorizationRequestRepository.saveAuthorizationRequest(authorizationRequest, request, response);
        response.sendRedirect(authorizationRequest.authorizationRequestUri());
    }
}
