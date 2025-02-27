package nextstep.oauth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

public class OAuth2AuthorizationRequestRedirectFilter extends GenericFilterBean {
    private final AuthorizationRequestResolver authorizationRequestResolver;
    private final AuthorizationRequestRepository authorizationRequestRepository = new AuthorizationRequestRepository();

    public OAuth2AuthorizationRequestRedirectFilter(ClientRegistrationRepository clientRegistrationRepository) {
        this.authorizationRequestResolver = new AuthorizationRequestResolver(clientRegistrationRepository);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //인증요청 해석
        OAuth2AuthorizationRequest authorizationRequest = this.authorizationRequestResolver.resolve(request);

        if (authorizationRequest != null) {
            //리다이렉트
            sendRedirectForAuthorization(request, response, authorizationRequest);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void sendRedirectForAuthorization(HttpServletRequest request
            , HttpServletResponse response
            , OAuth2AuthorizationRequest authorizationRequest) throws IOException {
        //인증요청 저장
        authorizationRequestRepository.saveAuthorizationRequest(request, authorizationRequest);

        //리다이렉트
        String url = authorizationRequest.getAuthorizationRequestUrl();
        response.sendRedirect(url);
    }
}
