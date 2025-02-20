package nextstep.oauth2.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nextstep.oauth2.endpoint.dto.OAuth2AuthorizationRequest;
import nextstep.oauth2.registration.ClientRegistrationRepository;
import nextstep.oauth2.web.authorizationrequest.HttpSessionOAuth2AuthorizationRequestRepository;
import nextstep.oauth2.web.authorizationrequest.OAuth2AuthorizationRequestRepository;
import nextstep.oauth2.web.authorizationrequest.OAuth2AuthorizationRequestResolver;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class OAuth2AuthorizationRequestRedirectFilter extends OncePerRequestFilter {
    private static final String BASE_URI = "/oauth2/authorization/";

    private final OAuth2AuthorizationRequestResolver authorizationRequestResolver;
    private final OAuth2AuthorizationRequestRepository authorizationRequestRepository;

    private OAuth2AuthorizationRequestRedirectFilter(
            OAuth2AuthorizationRequestResolver authorizationRequestResolver,
            OAuth2AuthorizationRequestRepository oAuth2AuthorizationRequestRepository
    ) {
        this.authorizationRequestResolver = authorizationRequestResolver;
        this.authorizationRequestRepository = oAuth2AuthorizationRequestRepository;
    }

    public OAuth2AuthorizationRequestRedirectFilter(
            ClientRegistrationRepository clientRegistrationRepository
    ) {
        this(
                new OAuth2AuthorizationRequestResolver(clientRegistrationRepository, BASE_URI),
                HttpSessionOAuth2AuthorizationRequestRepository.getInstance()
        );
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final OAuth2AuthorizationRequest authorizationRequest = this.authorizationRequestResolver.resolve(request);
        if (authorizationRequest == null) {
            filterChain.doFilter(request, response);
            return;
        }
        authorizationRequestRepository.saveAuthorizationRequest(authorizationRequest, request, response);
        response.sendRedirect(authorizationRequest.authorizationRequestUri());
    }
}
