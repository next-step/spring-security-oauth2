package nextstep.security.authentication;

public class Oauth2AuthenticationProvider implements AuthenticationProvider {

    private final Oauth2UserService oauth2UserService;

    public Oauth2AuthenticationProvider(Oauth2UserService oauth2UserService) {
        this.oauth2UserService = oauth2UserService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) {
        final OAuth2User oAuth2User = oauth2UserService.loadUser(authentication.getPrincipal().toString());
        return Oauth2Authentication.ofAuthenticated(oAuth2User.getName(), oAuth2User.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return Oauth2Authentication.class.isAssignableFrom(authentication);
    }
}
