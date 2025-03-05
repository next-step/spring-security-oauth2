package nextstep.app.oauth2;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nextstep.app.application.MemberService;
import nextstep.app.payload.Oauth2MemberSaveDto;
import nextstep.security.authentication.Authentication;
import nextstep.security.context.HttpSessionSecurityContextRepository;
import nextstep.security.context.SecurityContext;
import nextstep.security.context.SecurityContextHolder;
import nextstep.security.authentication.AuthenticationSuccessHandler;
import nextstep.security.oauth2.login.OAuth2LoginAuthenticationToken;

import java.io.IOException;

public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final HttpSessionSecurityContextRepository securityContextRepository;
    private final MemberService memberService;

    public OAuth2AuthenticationSuccessHandler(final HttpSessionSecurityContextRepository securityContextRepository,
                                              final MemberService memberService) {
        this.securityContextRepository = securityContextRepository;
        this.memberService = memberService;
    }

    @Override
    public void onSuccess(final HttpServletRequest request, final HttpServletResponse response, final Authentication authentication) throws IOException {
        if (authentication instanceof OAuth2LoginAuthenticationToken authToken) {
            var otherDetails = authToken.getOAuth2UserDetails().getOthers();
            memberService.saveOauth2Member(new Oauth2MemberSaveDto(
                    authToken.getPrincipal(),
                    otherDetails.get("name"),
                    otherDetails.get("avatar_url")
            ));
        } else {
            throw new IllegalArgumentException("유효한 토큰타입이 아닙니다.");
        }

        SecurityContext context = new SecurityContext(authentication);
        SecurityContextHolder.setContext(context);
        securityContextRepository.saveContext(context, request, response);
        response.sendRedirect("/");
    }
}
