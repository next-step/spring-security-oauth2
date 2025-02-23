package nextstep.security.oauth2;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nextstep.security.access.PathPatternRequestMatcher;
import nextstep.security.authentication.Authentication;
import nextstep.security.authentication.ProviderManager;
import nextstep.security.context.HttpSessionSecurityContextRepository;
import nextstep.security.context.SecurityContext;
import nextstep.security.oauth2.registration.ClientRegistration;
import nextstep.security.oauth2.registration.ClientRegistrationRepository;
import nextstep.security.oauth2.user.OAuth2UserService;
import org.springframework.http.HttpMethod;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.List;

public class OAuth2AuthenticationFilter extends GenericFilterBean {
    private static final String OAUTH_REQUEST_URI_PATTERN = "/login/oauth2/code/{registration-id}";
    private static final String REGISTRATION_ID = "registration-id";

    private final PathPatternRequestMatcher requestMatcher;
    private final HttpSessionSecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();
    private final ProviderManager providerManager;
    private final ClientRegistrationRepository clientRegistrationRepository;

    public OAuth2AuthenticationFilter(OAuth2UserService oAuth2UserService, ClientRegistrationRepository clientRegistrationRepository) {
        this.providerManager = new ProviderManager(
                List.of(new OAuth2AuthenticationProvider(oAuth2UserService))
        );

        this.clientRegistrationRepository = clientRegistrationRepository;
        this.requestMatcher = new PathPatternRequestMatcher(HttpMethod.GET, OAUTH_REQUEST_URI_PATTERN);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest httpServletRequest =  ((HttpServletRequest) request);
        final HttpServletResponse httpServletResponse = ((HttpServletResponse) response);

        if (!requestMatcher.matches(httpServletRequest)) {
            chain.doFilter(request, response);
            return;
        }

        final String registrationId = requestMatcher.getPathVariable(httpServletRequest, REGISTRATION_ID);
        final ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId(registrationId);

        if (clientRegistration == null) {
            httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        final OAuth2ProviderClient oAuth2ProviderClient = new OAuth2ProviderClient(clientRegistration);

        final OAuth2AccessToken oauth2AccessToken = oAuth2ProviderClient.accessTokenRequest(httpServletRequest.getParameter("code"));
        final OAuth2UserInfo userInfo = oAuth2ProviderClient.getUserInfo(oauth2AccessToken);

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
}
