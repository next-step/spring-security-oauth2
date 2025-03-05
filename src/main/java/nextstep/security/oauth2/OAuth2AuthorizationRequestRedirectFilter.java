package nextstep.security.oauth2;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nextstep.security.access.AntRequestMatcher;
import nextstep.security.access.RequestMatcher;
import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class OAuth2AuthorizationRequestRedirectFilter extends OncePerRequestFilter {

    private final RequestMatcher requestMatcher;
    private final OAuth2AuthorizationRequestResolver requestResolver;

    private final AuthorizationRequestRepository authorizationRequestRepository;

    public OAuth2AuthorizationRequestRedirectFilter(
            final RequestMatcher requestMatcher,
            final OAuth2AuthorizationRequestResolver requestResolver,
            final AuthorizationRequestRepository authorizationRequestRepository) {
        this.requestMatcher = requestMatcher;
        this.requestResolver = requestResolver;
        this.authorizationRequestRepository = authorizationRequestRepository;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        if (!requestMatcher.matches(request)) {
            filterChain.doFilter(request, response);
            return;
        }
        var oAuth2AuthorizationRequest = requestResolver.resolve(request);
        if (oAuth2AuthorizationRequest == null) {
            filterChain.doFilter(request, response);
            return;
        }
        authorizationRequestRepository.save(request, oAuth2AuthorizationRequest);
        sendRedirectForAuthorization(request, response, oAuth2AuthorizationRequest);
    }

    public void sendRedirectForAuthorization(@NonNull HttpServletRequest request,
                                             @NonNull HttpServletResponse response,
                                             @NonNull OAuth2AuthorizationRequest auth2AuthorizationRequest) throws IOException {

        response.sendRedirect(auth2AuthorizationRequest.getRedirectPath());
    }

}
