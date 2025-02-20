package nextstep.security.authentication.oauth;

import jakarta.servlet.http.HttpServletRequest;
import nextstep.security.authentication.AuthenticationException;

public interface OAuth2AuthenticationRequestResolver {
    OAuth2AuthorizationRequest resolve(HttpServletRequest request) throws AuthenticationException;
}
