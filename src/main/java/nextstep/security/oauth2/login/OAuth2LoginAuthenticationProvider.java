package nextstep.security.oauth2.login;

import jakarta.annotation.Nullable;
import nextstep.security.authentication.Authentication;
import nextstep.security.authentication.AuthenticationException;
import nextstep.security.authentication.AuthenticationProvider;
import nextstep.security.oauth2.ClientRegistration;
import nextstep.security.oauth2.userdetails.OAuth2UserDetailsService;
import nextstep.security.oauth2.userdetails.OAuth2UserDetailsServiceResolver;

import java.util.List;

public class OAuth2LoginAuthenticationProvider implements AuthenticationProvider {
    private final AuthenticationProvider authenticationProvider = new OAuth2AuthorizationCodeAuthenticationProvider();
    private final OAuth2UserDetailsServiceResolver oauth2UserDetailsServiceResolver;

    public OAuth2LoginAuthenticationProvider(final List<OAuth2UserDetailsService> userDetailsServices) {
        this.oauth2UserDetailsServiceResolver = new OAuth2UserDetailsServiceResolver(userDetailsServices);
    }

    @Nullable
    @Override
    public Authentication authenticate(final Authentication authentication) {
        OAuth2LoginAuthenticationToken token = (OAuth2LoginAuthenticationToken) authentication;
        var registration = token.getRegistration();
        var oAuth2AuthorizationCodeAuthenticationToken = authenticationProvider.authenticate(OAuth2AuthorizationCodeAuthenticationToken.unauthenticated(token.getPrincipal(), //code가 들어있음
                registration));
        return createOAuth2LoginAuthenticationTokenResponse(registration, oAuth2AuthorizationCodeAuthenticationToken);
    }

    @Override
    public boolean supports(final Class<?> authentication) {
        return authentication == OAuth2LoginAuthenticationToken.class;
    }

    private OAuth2LoginAuthenticationToken createOAuth2LoginAuthenticationTokenResponse(final ClientRegistration registration, final Authentication authentication) {
        if (!authentication.isAuthenticated()) {
            throw new AuthenticationException("code 정보로부터 accessToken 을 받아오는데 실패했습니다.");
        }
        if (!authenticationProvider.supports(authentication.getClass())) {
            return null;
        }
        var oauth2DetailsService = oauth2UserDetailsServiceResolver.getService(registration.getRegistrationId());
        try {
            var userDetails = oauth2DetailsService.loadUserByAccessToken(((OAuth2AuthorizationCodeAuthenticationToken) authentication).getAccessToken());
            return OAuth2LoginAuthenticationToken.authenticated(userDetails);
        } catch (Exception ex) {
            throw new AuthenticationException("accessToken 으로 부터 userInfo를 받아오는데 실패했습니다.");
        }
    }

}
