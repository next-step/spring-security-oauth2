package nextstep.security.authentication;

import jakarta.servlet.http.HttpServletRequest;
import nextstep.security.access.RequestMatcher;

import java.util.Map;

public class OAuth2ClientRepository {


    private final Map<String, ClientRegistration> clientRegistrations;

    public OAuth2ClientRepository(Map<String, ClientRegistration> clientRegistrations) {
        this.clientRegistrations = clientRegistrations;
    }

//    public OAuth2ClientRepository(Map<RequestMatcher, ClientRegistration> loginFactory, Map<RequestMatcher, ClientRegistration> factory) {
//        this.loginFactory = loginFactory;
//        this.authenticationFactory = factory;
//    }

    public ClientRegistration findByRegistrationId(String registrationId) {
        return clientRegistrations.get(registrationId);
    }

//    public ClientRegistration getOAuth2ClientForLogin(HttpServletRequest request) {
//        for (RequestMatcher matcher : loginFactory.keySet()) {
//            if (matcher.matches(request)) {
//                return loginFactory.get(matcher);
//            }
//        }
//
//        return null;
//    }
//
//
//    public ClientRegistration getOAuth2Client(HttpServletRequest request) {
//        for (RequestMatcher matcher : authenticationFactory.keySet()) {
//            if (matcher.matches(request)) {
//                return authenticationFactory.get(matcher);
//            }
//        }
//
//        return null;
//    }
}
