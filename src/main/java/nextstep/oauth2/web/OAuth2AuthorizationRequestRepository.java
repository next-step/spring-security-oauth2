package nextstep.oauth2.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nextstep.oauth2.endpoint.dto.OAuth2AuthorizationRequest;

public interface OAuth2AuthorizationRequestRepository {
    OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request);

    void saveAuthorizationRequest(
            OAuth2AuthorizationRequest authorizationRequest,
            HttpServletRequest request,
            HttpServletResponse response
    );

    OAuth2AuthorizationRequest removeAuthorizationRequest(
            HttpServletRequest request,
            HttpServletResponse response
    );
}
