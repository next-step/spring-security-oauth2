package nextstep.security.oauth2;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nextstep.security.access.PathPatternRequestMatcher;
import nextstep.security.access.RequestMatcher;
import org.springframework.http.HttpMethod;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class OAuth2AuthorizationRequestRedirectFilter extends OncePerRequestFilter {
    private static final String OAUTH_REQUEST_URI_PATTERN = "/oauth2/authorization/*";

    private final OAuth2AuthorizationRequestResolver oAuth2AuthorizationRequestResolver;
    private final RequestMatcher pathPatternRequestMatcher;
    private final AuthorizationRequestRepository authorizationRequestRepository;

    public OAuth2AuthorizationRequestRedirectFilter(OAuth2AuthorizationRequestResolver oAuth2AuthorizationRequestResolver,
                                                    AuthorizationRequestRepository authorizationRequestRepository

    ) {
        this.oAuth2AuthorizationRequestResolver = oAuth2AuthorizationRequestResolver;
        this.authorizationRequestRepository = authorizationRequestRepository;
        this.pathPatternRequestMatcher = new PathPatternRequestMatcher(HttpMethod.GET, OAUTH_REQUEST_URI_PATTERN);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        if (!pathPatternRequestMatcher.matches(request)) {
            chain.doFilter(request, response);
            return;
        }

        final OAuth2AuthorizationRequest oAuth2AuthorizationRequest = oAuth2AuthorizationRequestResolver.resolve(request);

        if (oAuth2AuthorizationRequest == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        this.authorizationRequestRepository.saveAuthorizationRequest(oAuth2AuthorizationRequest, request, response);
        response.sendRedirect(oAuth2AuthorizationRequest.getOauth2AuthorizationRedirectURl());
    }
}
