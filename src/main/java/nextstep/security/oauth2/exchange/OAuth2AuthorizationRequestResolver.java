package nextstep.security.oauth2.exchange;

import jakarta.servlet.http.HttpServletRequest;

public interface OAuth2AuthorizationRequestResolver {

    OAuth2AuthorizationRequest resolve(HttpServletRequest request);
}
