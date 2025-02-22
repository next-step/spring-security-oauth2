package nextstep.security.authentication.oauth;

public interface OAuth2AuthorizationRequest {
    String registrationId();

    String authorizationUri();

    String state();
}
