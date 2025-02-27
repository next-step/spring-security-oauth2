package nextstep.security.authentication;

import jakarta.servlet.http.HttpServletRequest;
import nextstep.security.access.RequestMatcher;

public class DefaultOAuth2AuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {

    private final RequestMatcher requestMatcher;
    private final OAuth2ClientRepository oAuth2ClientRepository;

    public DefaultOAuth2AuthorizationRequestResolver(RequestMatcher requestMatcher, OAuth2ClientRepository oAuth2ClientRepository) {
        this.requestMatcher = requestMatcher;
        this.oAuth2ClientRepository = oAuth2ClientRepository;
    }

    @Override
    public ClientRegistration resolve(HttpServletRequest request) {

        String registrationId = "";
        if (requestMatcher.matches(request)) {
            registrationId = request.getRequestURI().substring("/oauth2/authorization/".length());
        } else {
            return null;
        }

        ClientRegistration clientRegistration = oAuth2ClientRepository.findByRegistrationId(registrationId);
        if (clientRegistration == null) {
            throw new InvalidClientRegistrationIdException();
        }

        return clientRegistration;



    }
}
