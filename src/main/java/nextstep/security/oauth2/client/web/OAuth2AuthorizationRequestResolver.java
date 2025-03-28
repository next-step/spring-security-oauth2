package nextstep.security.oauth2.client.web;

import jakarta.servlet.http.HttpServletRequest;
import nextstep.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

public interface OAuth2AuthorizationRequestResolver {

    OAuth2AuthorizationRequest resolve(HttpServletRequest request);
}
