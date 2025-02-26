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
import nextstep.security.oauth2.user.OAuth2User;
import nextstep.security.oauth2.user.OAuth2UserRequest;
import nextstep.security.oauth2.user.OAuth2UserService;
import org.springframework.http.HttpMethod;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.List;

public class OAuth2LoginAuthenticationFilter extends GenericFilterBean {
    private static final String OAUTH_REQUEST_URI_PATTERN = "/login/oauth2/code/{registration-id}";

    private final PathPatternRequestMatcher requestMatcher;
    private final HttpSessionSecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();
    private final ProviderManager providerManager;
    private final ClientRegistrationRepository clientRegistrationRepository;
    private final AuthorizationRequestRepository authorizationRequestRepository;
    private final OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository;

    public OAuth2LoginAuthenticationFilter(OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService,
                                           ClientRegistrationRepository clientRegistrationRepository,
                                           OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository) {
        this.providerManager = new ProviderManager(
                List.of(new OAuth2AuthenticationProvider(oAuth2UserService))
        );

        this.clientRegistrationRepository = clientRegistrationRepository;
        this.requestMatcher = new PathPatternRequestMatcher(HttpMethod.GET, OAUTH_REQUEST_URI_PATTERN);
        this.authorizationRequestRepository = new AuthorizationRequestRepository();
        this.oAuth2AuthorizedClientRepository = oAuth2AuthorizedClientRepository;
    }


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest httpServletRequest =  ((HttpServletRequest) request);
        final HttpServletResponse httpServletResponse = ((HttpServletResponse) response);

        if (!requestMatcher.matches(httpServletRequest)) {
            chain.doFilter(request, response);
            return;
        }

        final Authentication authentication = attemptAuthentication(httpServletRequest, httpServletResponse);

        storeContext(authentication, httpServletRequest, httpServletResponse);

        httpServletResponse.sendRedirect("/");
    }


    private Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        // session에서 authorizationRequest를 가져오기
        OAuth2AuthorizationRequest oAuth2AuthorizationRequest = authorizationRequestRepository.removeAuthorizationRequest(request, response);

        // registrationId를 가져오고 clientRegistration을 가져오기
        final ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId(requestMatcher.getPathVariable(request, "registration-id"));

        if (clientRegistration == null) {
            throw new OAuth2AuthenticationException();
        }

        // code를 포함한 authorization response를 객체로 가져오기
        // access token 을 가져오기 위한 request 객체 만들기
        OAuth2AccessTokenRequest oAuth2AccessTokenRequest = OAuth2AccessTokenRequest.of(clientRegistration, request.getParameter("code"));
        final OAuth2ProviderClient oAuth2ProviderClient = new OAuth2ProviderClient(clientRegistration);
        final OAuth2AccessToken oauth2AccessToken = oAuth2ProviderClient.accessTokenRequest(oAuth2AccessTokenRequest);

        // OAuth2LoginAuthenticationToken 만들기
        OAuth2LoginAuthenticationToken oAuth2LoginAuthenticationToken = OAuth2LoginAuthenticationToken.unauthenticated(clientRegistration, oauth2AccessToken);

        // provider 인증 후 authenticated된 OAuth2AuthenticationToken 객체 가져오기
        Authentication authenticated = providerManager.authenticate(oAuth2LoginAuthenticationToken);

        // authorizedClientRepository 에 저장할 OAuth2AuthorizedClient을 만들고 저장
        OAuth2AuthorizedClient oAuth2AuthorizedClient = new OAuth2AuthorizedClient(clientRegistration, authenticated.getPrincipal().toString(), oauth2AccessToken);
        this.oAuth2AuthorizedClientRepository.saveAuthorizedClient(oAuth2AuthorizedClient, authenticated, request, response);

        return authenticated;
    }

    private void storeContext(Authentication authenticate, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        SecurityContext securityContext = new SecurityContext(authenticate);
        securityContextRepository.saveContext(securityContext, httpServletRequest, httpServletResponse);
    }
}
