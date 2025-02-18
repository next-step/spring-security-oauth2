package nextstep.security.oauth2;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nextstep.app.OAuth2ClientProperties;
import nextstep.security.authentication.Authentication;
import nextstep.security.authentication.AuthenticationException;
import nextstep.security.authentication.ProviderManager;
import nextstep.security.context.HttpSessionSecurityContextRepository;
import nextstep.security.context.SecurityContext;
import nextstep.security.userdetails.UserDetailsService;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.PathContainer;
import org.springframework.web.client.RestClient;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class GithubAuthenticationFilter extends GenericFilterBean {
    private static final String REGISTRATION_ID = "registration-id";
    private static final String OAUTH_REQUEST_URI_PATTERN = "/login/oauth2/code/{" + REGISTRATION_ID + "}";
    private static final PathPattern pattern = new PathPatternParser().parse(OAUTH_REQUEST_URI_PATTERN);

    private final HttpSessionSecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();
    private final ProviderManager providerManager;
    private OAuth2ClientProperties properties;


    public GithubAuthenticationFilter(UserDetailsService userDetailsService, OAuth2ClientProperties properties) {
        providerManager = new ProviderManager(
                List.of(new Oauth2AuthenticationProvider(userDetailsService))
        );

        properties = properties;
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

        validCode(httpServletRequest);

        final String providerName = matchInfo.getUriVariables().get(REGISTRATION_ID);
        final Oauth2ClientRegistrationProperties registration = properties.getOauth2Registration(providerName);
        final Oauth2ClientProviderProperties oauth2Provider = properties.getOauth2Provider(providerName);


        String clientId = registration.getClientId();

        Map<String, String> body = RestClient.create()
                .post()
                .uri(oauth2Provider.getTokenUri())
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});

        String accessToken = body.get("access_token");
        String tokenType = body.get("token_type");


        Map<String, String> user = RestClient.create()
                .get()
                .uri(RESOURCE_USER_URI)
                .header(HttpHeaders.AUTHORIZATION, tokenType + accessToken)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });


        Oauth2AuthenticationToken authenticated = Oauth2AuthenticationToken.unauthenticated(user.get("email"), accessToken);
        Authentication authenticate = providerManager.authenticate(authenticated);

        SecurityContext securityContext = new SecurityContext();
        securityContext.setAuthentication(authenticate);
        securityContextRepository.saveContext(securityContext, httpServletRequest, httpServletResponse);

        httpServletResponse.sendRedirect("/");
    }

    private static void validCode(HttpServletRequest httpServletRequest) {
        String code = httpServletRequest.getParameter("code");

        if (code == null) {
            throw new AuthenticationException("Invalid code");
        }
    }

    private static PathPattern.PathMatchInfo getPathMatchInfo(HttpServletRequest httpServletRequest) {
        PathContainer path = PathContainer.parsePath(httpServletRequest.getRequestURI());
        PathPattern.PathMatchInfo matchInfo = pattern.matchAndExtract(path);

        return matchInfo;
    }

}
