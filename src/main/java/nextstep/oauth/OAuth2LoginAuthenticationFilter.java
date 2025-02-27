package nextstep.oauth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nextstep.security.authentication.Authentication;
import nextstep.security.authentication.AuthenticationException;
import nextstep.security.authentication.AuthenticationManager;
import nextstep.security.context.HttpSessionSecurityContextRepository;
import nextstep.security.context.SecurityContext;
import nextstep.security.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

public class OAuth2LoginAuthenticationFilter extends GenericFilterBean {
    private static final String MATCH_REQUEST_URI_PREFIX = "/login/oauth2/code/";

    private final AuthorizationRequestRepository authorizationRequestRepository = new AuthorizationRequestRepository();
    private final HttpSessionSecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();
    private final OAuth2AuthorizedClientRepository authorizedClientRepository = new OAuth2AuthorizedClientRepository();
    private final ClientRegistrationRepository clientRegistrationRepository;
    private final AuthenticationManager authenticationManager;


    public OAuth2LoginAuthenticationFilter(ClientRegistrationRepository clientRegistrationRepository
            , AuthenticationManager authenticationManager) {
        this.clientRegistrationRepository = clientRegistrationRepository;
        this.authenticationManager = authenticationManager;
    }


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        if (!requiresAuthentication(request, response)) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            Authentication authenticationResult = attemptAuthentication(request, response);
            if (authenticationResult == null) {
                return;
            }
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authenticationResult);
            SecurityContextHolder.setContext(context);
            this.securityContextRepository.saveContext(context, request, response);

            response.sendRedirect("/");
        } catch (AuthenticationException ex) {
            SecurityContextHolder.clearContext();
        }
    }

    private boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {
        return request.getRequestURI().startsWith(MATCH_REQUEST_URI_PREFIX);
    }

    private Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {

        // request에서 parameter를 가져오기
        String code = request.getParameter("code");
        // session에서 authorizationRequest를 가져오기
        OAuth2AuthorizationRequest oAuth2AuthorizationRequest = authorizationRequestRepository.loadAuthorizationRequest(request);

        // registrationId를 가져오고 clientRegistration을 가져오기
        String providerKey = request.getRequestURI().replaceFirst(MATCH_REQUEST_URI_PREFIX, "");
        ClientRegistration clientRegistration = clientRegistrationRepository.findByProviderKey(providerKey);

        // code를 포함한 authorization response를 객체로 가져오기
        OAuth2AuthorizationResponse authorizationResponse = new OAuth2AuthorizationResponse(code, clientRegistration.getRedirectUri());

        // access token 을 가져오기 위한 request 객체 만들기
        OAuth2LoginAuthenticationToken loginAuthenticationToken = new OAuth2LoginAuthenticationToken(clientRegistration
                , authorizationResponse);

        // OAuth2LoginAuthenticationToken 만들기
        OAuth2AuthenticationToken authenticatedToken = (OAuth2AuthenticationToken) authenticationManager.authenticate(loginAuthenticationToken);

        // authorizedClientRepository 에 저장할 OAuth2AuthorizedClient을 만들고 저장
        OAuth2AuthorizedClient authorizedClient = new OAuth2AuthorizedClient(
                loginAuthenticationToken.getClientRegistration()
                , authenticatedToken.getPrincipal().toString()
                , loginAuthenticationToken.getAccessToken());

        this.authorizedClientRepository.saveAuthorizedClient(authorizedClient, authenticatedToken, request, response);

        return authenticatedToken;
    }
}
