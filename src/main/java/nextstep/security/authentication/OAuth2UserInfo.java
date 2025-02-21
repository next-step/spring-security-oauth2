package nextstep.security.authentication;

public interface OAuth2UserInfo {
    String getId();

    String getName();

    String getEmail();

    String getPictureUrl();
}
