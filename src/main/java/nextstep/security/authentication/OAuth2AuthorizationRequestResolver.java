package nextstep.security.authentication;

import jakarta.servlet.http.HttpServletRequest;

public interface OAuth2AuthorizationRequestResolver {

    ClientRegistration resolve(HttpServletRequest request);
}
