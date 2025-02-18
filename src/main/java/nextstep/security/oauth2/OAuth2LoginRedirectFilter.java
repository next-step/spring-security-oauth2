package nextstep.security.oauth2;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nextstep.app.OAuth2ClientProperties;
import org.springframework.http.server.PathContainer;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

import java.io.IOException;

public class OAuth2LoginRedirectFilter extends GenericFilterBean {
    private static final String REGISTRATION = "registration";
    private static final String OAUTH_REQUEST_URI_PATTERN = "/oauth2/authorization/{" + REGISTRATION + "}";
    private static final PathPattern pattern = new PathPatternParser().parse(OAUTH_REQUEST_URI_PATTERN);

    private final OAuth2ClientProperties properties;

    public OAuth2LoginRedirectFilter(OAuth2ClientProperties properties) {
        this.properties = properties;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse httpServletResponse = ((HttpServletResponse) response);

        final PathPattern.PathMatchInfo matchInfo = getPathMatchInfo(((HttpServletRequest) request));

        if (matchInfo == null) {
            chain.doFilter(request, httpServletResponse);
        }

        final String providerName = matchInfo.getUriVariables().get(REGISTRATION);
        final Oauth2ClientRegistrationProperties registration = properties.getOauth2Properties(providerName);

        httpServletResponse.sendRedirect(UriComponentsBuilder.fromHttpUrl(registration.getLoginUrl())
                .queryParam("client_id", registration.getClientId())
                .queryParam("response_type", registration.getResponseType())
                .queryParam("scope", registration.getScope())
                .queryParam("redirect_uri", registration.getRedirectUri())
                .build().toUriString()
        );
    }

    private static PathPattern.PathMatchInfo getPathMatchInfo(HttpServletRequest httpServletRequest) {
        PathContainer path = PathContainer.parsePath(httpServletRequest.getRequestURI());
        PathPattern.PathMatchInfo matchInfo = pattern.matchAndExtract(path);

        return matchInfo;
    }

}
