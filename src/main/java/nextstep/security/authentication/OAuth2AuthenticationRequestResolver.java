package nextstep.security.authentication;

public interface OAuth2AuthenticationRequestResolver {
    String RESPONSE_TYPE = "code";
    String resolve();
}
