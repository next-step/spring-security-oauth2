package nextstep.security.authentication;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthorizationRequestRepository<T> {

    T loadAuthorizationRequest(HttpServletRequest request);
    void saveAuthorizationRequest(T authorizationRequest, HttpServletRequest request, HttpServletResponse response);
    T removeAuthorizationRequest(HttpServletRequest request, HttpServletResponse response);
}
