package nextstep.security.oauth2.exchange;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthorizationRequestRepository {

    OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request);

    void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest,
                                  HttpServletRequest request,
                                  HttpServletResponse response);

    OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request,
                                                          HttpServletResponse response);
}
