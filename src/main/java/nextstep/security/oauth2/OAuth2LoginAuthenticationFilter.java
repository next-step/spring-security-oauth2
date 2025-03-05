package nextstep.security.oauth2;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nextstep.security.access.AntRequestMatcher;
import nextstep.security.authentication.*;
import nextstep.security.oauth2.login.OAuth2LoginAuthenticationToken;
import org.springframework.http.HttpMethod;

public class OAuth2LoginAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    private static final String REDIRECT_MATCH_URL = "/login/oauth2/code/*";
    private final AuthorizationRequestRepository authorizationRequestRepository;
    private final AuthenticationManager authenticationManager;


    public OAuth2LoginAuthenticationFilter(final AuthenticationSuccessHandler authenticationSuccessHandler,
                                           final AuthorizationRequestRepository authorizationRequestRepository,
                                           final AuthenticationManager authenticationManager) {
        super(new AntRequestMatcher(HttpMethod.GET, REDIRECT_MATCH_URL), authenticationSuccessHandler, DefaultAuthenticationFailHandler.getInstance());
        this.authorizationRequestRepository = authorizationRequestRepository;
        this.authenticationManager = authenticationManager;
    }

    @Override
    protected Authentication attemptAuthentication(final HttpServletRequest request, final HttpServletResponse response) {
        var code = request.getParameter("code");
        if (code == null) {
            throw new AuthenticationException("인증 코드 정보가 존재하지 않습니다.");
        }

        // session에서 authorizationRequest를 가져오기
        var authorizationRequest = authorizationRequestRepository.get(request);
        if (authorizationRequest == null) {
            throw new AuthenticationException("인증 정보가 존재하지 않습니다.");
        }

        var authenticated = authenticationManager.authenticate(
                OAuth2LoginAuthenticationToken.unauthenticated(
                        code,
                        authorizationRequest
                )
        );

        if (authenticated == null || !authenticated.isAuthenticated()) {
            throw new AuthenticationException("oauth2 인증에 실패했습니다.");
        }

        return authenticated;
    }
}
