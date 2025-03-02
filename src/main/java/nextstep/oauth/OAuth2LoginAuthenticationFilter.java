package nextstep.oauth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nextstep.security.authentication.AbstractAuthenticationProcessingFilter;
import nextstep.security.authentication.Authentication;
import nextstep.security.authentication.AuthenticationManager;

public class OAuth2LoginAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    private static final String MATCH_REQUEST_URI_PREFIX = "/login/oauth2/code/";

    private final AuthorizationRequestRepository authorizationRequestRepository = new AuthorizationRequestRepository();
    private final OAuth2AuthorizedClientRepository authorizedClientRepository = new OAuth2AuthorizedClientRepository();
    private final ClientRegistrationRepository clientRegistrationRepository;

    public OAuth2LoginAuthenticationFilter(ClientRegistrationRepository clientRegistrationRepository
            , AuthenticationManager authenticationManager) {
        super(MATCH_REQUEST_URI_PREFIX, authenticationManager);
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {

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
        OAuth2LoginAuthenticationToken loginAuthenticatedToken = (OAuth2LoginAuthenticationToken) getAuthenticationManager().authenticate(loginAuthenticationToken);

        // provider 인증 후 authenticated된 OAuth2AuthenticationToken 객체 가져오기
        OAuth2AuthenticationToken authenticationToken = new OAuth2AuthenticationToken(loginAuthenticatedToken.getPrincipal());

        // authorizedClientRepository 에 저장할 OAuth2AuthorizedClient을 만들고 저장
        OAuth2AuthorizedClient authorizedClient = new OAuth2AuthorizedClient(
                loginAuthenticationToken.getClientRegistration()
                , authenticationToken.getPrincipal().toString()
                , loginAuthenticatedToken.getAccessToken());

        this.authorizedClientRepository.saveAuthorizedClient(authorizedClient, authenticationToken, request, response);

        return authenticationToken;
    }
}
