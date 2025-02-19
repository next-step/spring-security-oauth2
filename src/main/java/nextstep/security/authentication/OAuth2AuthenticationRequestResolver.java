package nextstep.security.authentication;

public interface OAuth2AuthenticationRequestResolver {
    String resolve(String oAuth2Type);
}
