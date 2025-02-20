package nextstep.security.oauth2.authentication;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nextstep.security.authentication.AbstractAuthenticationProcessingFilter;
import nextstep.security.authentication.Authentication;
import nextstep.security.authentication.AuthenticationManager;
import nextstep.security.oauth2.authorizedclient.OAuth2AuthorizedClient;
import nextstep.security.oauth2.authorizedclient.OAuth2AuthorizedClientService;
import nextstep.security.oauth2.client.ClientRegistration;
import nextstep.security.oauth2.client.ClientRegistrationRepository;
import nextstep.security.oauth2.exception.OAuth2AuthenticationException;
import nextstep.security.oauth2.exchange.AuthorizationRequestRepository;
import nextstep.security.oauth2.exchange.HttpSessionOAuth2AuthorizationRequestRepository;
import nextstep.security.oauth2.exchange.OAuth2AuthorizationExchange;
import nextstep.security.oauth2.exchange.OAuth2AuthorizationRequest;
import nextstep.security.oauth2.exchange.OAuth2AuthorizationResponse;
import org.springframework.core.convert.converter.Converter;

import java.io.IOException;
import java.util.Set;

public class OAuth2LoginAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    private final AuthorizationRequestRepository authorizationRequestRepository =
            new HttpSessionOAuth2AuthorizationRequestRepository();

    private final ClientRegistrationRepository clientRegistrationRepository;
    private final AuthenticationManager authenticationManager;
    private final OAuth2AuthorizedClientService authorizedClientRepository;

    private final Converter<OAuth2LoginAuthenticationToken, OAuth2AuthenticationToken> authenticationResultConverter =
            this::createAuthenticationResult;

    public OAuth2LoginAuthenticationFilter(ClientRegistrationRepository clientRegistrationRepository,
                                           AuthenticationManager authenticationManager,
                                           OAuth2AuthorizedClientService authorizedClientRepository) {

        this.clientRegistrationRepository = clientRegistrationRepository;
        this.authenticationManager = authenticationManager;
        this.authorizedClientRepository = authorizedClientRepository;
    }

    @Override
    protected boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {
        return request.getRequestURI().startsWith("/login/oauth2/code/");
    }

    @Override
    protected Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {

        // request에서 parameter를 가져오기
        // session에서 authorizationRequest를 가져오기
        OAuth2AuthorizationRequest authorizationRequest = this.authorizationRequestRepository
                .removeAuthorizationRequest(request, response);

        if (authorizationRequest == null) {
            throw new OAuth2AuthenticationException("authorizationRequest is null");
        }

        // registrationId를 가져오고 clientRegistration을 가져오기
        String registrationId = authorizationRequest.getRegistrationId();
        ClientRegistration clientRegistration = this.clientRegistrationRepository.findByRegistrationId(registrationId);
        if (clientRegistration == null) {
            throw new OAuth2AuthenticationException("client registration is null");
        }

        // code를 포함한 authorization response를 객체로 가져오기
        String code = request.getParameter("code");
        String state = request.getParameter("state");

        OAuth2AuthorizationResponse authorizationResponse = OAuth2AuthorizationResponse.builder()
                                                                                       .code(code)
                                                                                       .state(state)
                                                                                       .build();

        // access token 을 가져오기 위한 request 객체 만들기
        // OAuth2LoginAuthenticationToken 만들기
        OAuth2LoginAuthenticationToken authRequest = new OAuth2LoginAuthenticationToken(clientRegistration,
                                                                                        new OAuth2AuthorizationExchange(
                                                                                                authorizationRequest,
                                                                                                authorizationResponse)
        );

        // provider 인증 후 authenticated된 OAuth2AuthenticationToken 객체 가져오기
        OAuth2LoginAuthenticationToken authResult =
                (OAuth2LoginAuthenticationToken) this.authenticationManager.authenticate(authRequest);

        OAuth2AuthenticationToken oauth2Authentication = authenticationResultConverter.convert(authResult);

        // authorizedClientRepository 에 저장할 OAuth2AuthorizedClient을 만들고 저장
        OAuth2AuthorizedClient oAuth2AuthorizedClient = new OAuth2AuthorizedClient(clientRegistration,
                                                                                   oauth2Authentication.getPrincipal().getName(),
                                                                                   authResult.getAccessToken());

        this.authorizedClientRepository.saveAuthorizedClient(oAuth2AuthorizedClient, oauth2Authentication);
        return oauth2Authentication;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult) throws IOException {

        super.successfulAuthentication(request, response, chain, authResult);
        response.sendRedirect("/");
    }

    private OAuth2AuthenticationToken createAuthenticationResult(OAuth2LoginAuthenticationToken authenticationResult) {
        Set<String> authorities = authenticationResult.getAccessToken().getScopes();
        authorities.addAll(authenticationResult.getAuthorities());
        return OAuth2AuthenticationToken.authenticated(authenticationResult.getPrincipal(),
                                                       authenticationResult.getAuthorities(),
                                                       authenticationResult.getClientRegistration().getRegistrationId());
    }

}
