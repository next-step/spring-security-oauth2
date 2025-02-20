package nextstep.oauth2.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nextstep.oauth2.authentication.token.OAuth2AuthenticationToken;
import nextstep.oauth2.authentication.token.OAuth2LoginAuthenticationToken;
import nextstep.oauth2.endpoint.dto.OAuth2AuthorizationExchange;
import nextstep.oauth2.endpoint.dto.OAuth2AuthorizationRequest;
import nextstep.oauth2.endpoint.dto.OAuth2AuthorizationResponse;
import nextstep.oauth2.exception.OAuth2AuthenticationException;
import nextstep.oauth2.registration.ClientRegistration;
import nextstep.oauth2.registration.ClientRegistrationRepository;
import nextstep.oauth2.web.authorizationrequest.HttpSessionOAuth2AuthorizationRequestRepository;
import nextstep.oauth2.web.authorizationrequest.OAuth2AuthorizationRequestRepository;
import nextstep.oauth2.web.authorizedclient.OAuth2AuthorizedClient;
import nextstep.oauth2.web.authorizedclient.OAuth2AuthorizedClientRepository;
import nextstep.security.authentication.AbstractAuthenticationProcessingFilter;
import nextstep.security.authentication.Authentication;
import nextstep.security.authentication.AuthenticationManager;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OAuth2LoginAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    private static final String BASE_URI = "/login/oauth2/code/";
    private static final OAuth2AuthorizationRequestRepository requestRepository = HttpSessionOAuth2AuthorizationRequestRepository.getInstance();

    private final ClientRegistrationRepository registrationRepository;
    private final OAuth2AuthorizedClientRepository clientRepository;

    public OAuth2LoginAuthenticationFilter(
            ClientRegistrationRepository registrationRepository,
            OAuth2AuthorizedClientRepository clientRepository,
            AuthenticationManager authenticationManager
    ) {
        super(BASE_URI, authenticationManager);
        this.registrationRepository = registrationRepository;
        this.clientRepository = clientRepository;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        final OAuth2LoginAuthenticationToken loginToken = getLoginToken(request);
        final OAuth2AuthorizedClient authorizedClient = new OAuth2AuthorizedClient(
                loginToken.getClientRegistration(),
                loginToken.getPrincipal().toString(),
                loginToken.getAccessToken()
        );
        final OAuth2AuthenticationToken authentication = new OAuth2AuthenticationToken(
                loginToken.getPrincipal(),
                loginToken.getAuthorities()
        );
        clientRepository.saveAuthorizedClient(authorizedClient, authentication, request, response);
        return authentication;
    }

    public OAuth2LoginAuthenticationToken getLoginToken(
            HttpServletRequest request
    ) {
        final MultiValueMap<String, String> params = getParams(request);
        final ClientRegistration clientRegistration = getClientRegistration(request);
        final OAuth2AuthorizationRequest authorizationRequest = getAuthorizationRequest(request);
        final OAuth2AuthorizationResponse authorizationResponse = new OAuth2AuthorizationResponse(
                clientRegistration.redirectUri(),
                params.getFirst(OAuth2ParameterNames.CODE),
                params.getFirst(OAuth2ParameterNames.STATE)
        );
        return (OAuth2LoginAuthenticationToken) getAuthenticationManager()
                .authenticate(new OAuth2LoginAuthenticationToken(
                        clientRegistration,
                        new OAuth2AuthorizationExchange(authorizationRequest, authorizationResponse)
                ));
    }

    public MultiValueMap<String, String> getParams(HttpServletRequest request) {
        final MultiValueMap<String, String> params = request.getParameterMap().entrySet()
                .stream().collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> List.of(entry.getValue()),
                        (oldValue, newValue) -> oldValue,
                        LinkedMultiValueMap::new
                ));
        final boolean isAuthorizationResponseSuccess = StringUtils.hasText(params.getFirst(OAuth2ParameterNames.CODE))
                && StringUtils.hasText(params.getFirst(OAuth2ParameterNames.STATE));
        final boolean isAuthorizationResponseError = StringUtils.hasText(params.getFirst(OAuth2ParameterNames.ERROR))
                && StringUtils.hasText(params.getFirst(OAuth2ParameterNames.STATE));
        final boolean isAuthorizationResponse = isAuthorizationResponseSuccess || isAuthorizationResponseError;
        if (!isAuthorizationResponse) {
            throw new OAuth2AuthenticationException();
        }
        return params;
    }

    public ClientRegistration getClientRegistration(HttpServletRequest request) {
        final String requestUri = request.getRequestURI();
        final String registrationId = requestUri.startsWith(BASE_URI)
                ? requestUri.substring(BASE_URI.length())
                : null;
        final ClientRegistration clientRegistration = registrationRepository.findByRegistrationId(registrationId);
        if (clientRegistration == null) {
            throw new OAuth2AuthenticationException();
        }
        return clientRegistration;
    }

    public OAuth2AuthorizationRequest getAuthorizationRequest(HttpServletRequest request) {
        final OAuth2AuthorizationRequest authorizationRequest = requestRepository.loadAuthorizationRequest(request);
        if (authorizationRequest == null) {
            throw new OAuth2AuthenticationException();
        }
        return authorizationRequest;
    }
}
