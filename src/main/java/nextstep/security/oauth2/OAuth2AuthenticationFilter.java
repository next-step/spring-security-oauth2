package nextstep.security.oauth2;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nextstep.app.OAuth2ClientProperties;
import nextstep.security.authentication.Authentication;
import nextstep.security.authentication.ProviderManager;
import nextstep.security.context.HttpSessionSecurityContextRepository;
import nextstep.security.context.SecurityContext;
import nextstep.security.userdetails.UserDetailsService;
import org.springframework.http.server.PathContainer;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

import java.io.IOException;
import java.util.List;

public class OAuth2AuthenticationFilter extends GenericFilterBean {
    private static final String REGISTRATION_ID = "registration-id";
    private static final String OAUTH_REQUEST_URI_PATTERN = "/login/oauth2/code/{" + REGISTRATION_ID + "}";
    private static final PathPattern pattern = new PathPatternParser().parse(OAUTH_REQUEST_URI_PATTERN);

    private final HttpSessionSecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();
    private final ProviderManager providerManager;
    private final OAuth2ClientProperties properties;

    public OAuth2AuthenticationFilter(UserDetailsService userDetailsService, OAuth2ClientProperties properties) {
        this.providerManager = new ProviderManager(
                List.of(new OAuth2AuthenticationProvider(userDetailsService))
        );
        this.properties = properties;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest httpServletRequest =  ((HttpServletRequest) request);
        final HttpServletResponse httpServletResponse = ((HttpServletResponse) response);

        final PathPattern.PathMatchInfo matchInfo = getPathMatchInfo(((HttpServletRequest) request));

        if (matchInfo == null) {
            chain.doFilter(request, response);
            return;
        }

        final OAuth2ProviderClient oauth2ProviderClient = getProviderClient(matchInfo);

        final OAuth2AccessToken oauth2AccessToken = oauth2ProviderClient.accessTokenRequest(httpServletRequest.getParameter("code"));

        final OAuth2UserInfo userInfo = oauth2ProviderClient.getUserInfo(oauth2AccessToken);

        storeContext(userInfo, oauth2AccessToken, httpServletRequest, httpServletResponse);

        httpServletResponse.sendRedirect("/");
    }

    private void storeContext(OAuth2UserInfo userInfo, OAuth2AccessToken oauth2AccessToken, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        OAuth2AuthenticationToken authenticated = OAuth2AuthenticationToken.unauthenticated(userInfo.getEmail(), oauth2AccessToken);
        Authentication authenticate = providerManager.authenticate(authenticated);
        SecurityContext securityContext = new SecurityContext();
        securityContext.setAuthentication(authenticate);
        securityContextRepository.saveContext(securityContext, httpServletRequest, httpServletResponse);
    }

    private OAuth2ProviderClient getProviderClient(PathPattern.PathMatchInfo matchInfo) {
        final String providerName = matchInfo.getUriVariables().get(REGISTRATION_ID);

        final OAuth2ClientRegistrationProperties registration = properties.getOauth2Registration(providerName);

        final OAuth2ClientProviderProperties oauth2Provider = properties.getOauth2Provider(providerName);

        return new OAuth2ProviderClient(registration, oauth2Provider);
    }


    private static PathPattern.PathMatchInfo getPathMatchInfo(HttpServletRequest httpServletRequest) {
        PathContainer path = PathContainer.parsePath(httpServletRequest.getRequestURI());
        PathPattern.PathMatchInfo matchInfo = pattern.matchAndExtract(path);

        return matchInfo;
    }

}
