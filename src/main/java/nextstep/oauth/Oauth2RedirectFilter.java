package nextstep.oauth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

public class Oauth2RedirectFilter extends GenericFilterBean {
    private static final String MATCH_REQUEST_URI_PREFIX = "/oauth2/authorization/";

    private final OAuth2ClientProperties oAuth2ClientProperties;

    public Oauth2RedirectFilter(OAuth2ClientProperties oAuth2ClientProperties) {
        this.oAuth2ClientProperties = oAuth2ClientProperties;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        if (!request.getRequestURI().startsWith(MATCH_REQUEST_URI_PREFIX)) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        String providerKey = request.getRequestURI().replaceFirst(MATCH_REQUEST_URI_PREFIX, "");

        OAuth2ClientProperties.Provider provider = oAuth2ClientProperties.getProvider().get(providerKey);
        OAuth2ClientProperties.Registration registration = oAuth2ClientProperties.getRegistration().get(providerKey);

        String url = UriComponentsBuilder.fromHttpUrl(provider.getAuthorizationUri())
                .queryParam("client_id", registration.getClientId())
                .queryParam("response_type", "code")
                .queryParam("scope", registration.getScope())
                .queryParam("redirect_uri", registration.getRedirectUri())
                .toUriString();

        response.sendRedirect(url);

    }
}
