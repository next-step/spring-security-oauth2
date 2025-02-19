package nextstep.security.authentication.filter.oauth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nextstep.security.access.MvcRequestMatcher;
import nextstep.security.authentication.OAuth2AuthenticationRequestResolver;
import nextstep.security.authentication.OAuth2ProviderSupportChecker;
import nextstep.security.authentication.UnsupportedOAuth2ProviderException;
import org.springframework.http.HttpMethod;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class OAuth2RedirectAuthenticationFilter extends OncePerRequestFilter {

    private final MvcRequestMatcher requestMatcher;
    private final OAuth2AuthenticationRequestResolver oAuth2AuthenticationRequestResolver;
    private final OAuth2ProviderSupportChecker providerSupportChecker;

    public OAuth2RedirectAuthenticationFilter(OAuth2AuthenticationRequestResolver oAuth2AuthenticationRequestResolver, OAuth2ProviderSupportChecker providerSupportChecker) {
        this.providerSupportChecker = providerSupportChecker;
        this.requestMatcher = new MvcRequestMatcher(HttpMethod.GET, "/oauth2/authorization/{provider}");
        this.oAuth2AuthenticationRequestResolver = oAuth2AuthenticationRequestResolver;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (noRequiresAuthentication(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String oAuth2Type = getOAuth2Type(request.getRequestURI());
        String authorizationUri = oAuth2AuthenticationRequestResolver.resolve(oAuth2Type);

        response.sendRedirect(authorizationUri);
    }

    private boolean noRequiresAuthentication(HttpServletRequest request) {
        try {
            providerSupportChecker.checkRequest(request, requestMatcher);
            return false;
        } catch (UnsupportedOAuth2ProviderException e) {
            return true;
        }
    }

    private String getOAuth2Type(String requestUri) {
        return requestUri.substring(requestUri.lastIndexOf("/") + 1);
    }
}
