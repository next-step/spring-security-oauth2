package nextstep.security.authentication.filter.oauth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nextstep.security.access.MvcRequestMatcher;
import nextstep.security.authentication.oauth.OAuth2AuthenticationRequestResolver;
import nextstep.security.authentication.oauth.OAuth2AuthorizationRequest;
import nextstep.security.userservice.OAuth2ClientRegistration;
import nextstep.security.userservice.OAuth2UserService;
import org.springframework.http.HttpMethod;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class OAuth2RedirectAuthenticationFilter extends OncePerRequestFilter {

    private final MvcRequestMatcher requestMatcher;
    private final OAuth2AuthenticationRequestResolver oAuth2AuthenticationRequestResolver;
    private final OAuth2UserService auth2UserService;

    public OAuth2RedirectAuthenticationFilter(OAuth2AuthenticationRequestResolver oAuth2AuthenticationRequestResolver, OAuth2UserService auth2UserService) {
        this.auth2UserService = auth2UserService;
        this.requestMatcher = new MvcRequestMatcher(HttpMethod.GET, "/oauth2/authorization/{provider}");
        this.oAuth2AuthenticationRequestResolver = oAuth2AuthenticationRequestResolver;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (noRequiresAuthentication(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String registrationId = getRegistrationId(request.getRequestURI());
        OAuth2ClientRegistration clientRegistration = auth2UserService.loadClientRegistrationByRegistrationId(registrationId);

        OAuth2AuthorizationRequest authorizationRequest = oAuth2AuthenticationRequestResolver.resolve(clientRegistration);

        auth2UserService.saveOAuth2AuthorizationRequest(authorizationRequest);

        response.sendRedirect(authorizationRequest.authorizationUri());
    }

    private boolean noRequiresAuthentication(HttpServletRequest request) {
        return !requestMatcher.matches(request);
    }

    private String getRegistrationId(String requestUri) {
        return requestUri.substring(requestUri.lastIndexOf("/") + 1);
    }
}
