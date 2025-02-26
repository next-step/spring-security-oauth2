package nextstep.oauth2.exception;

public class OAuth2UserInfoEndPointNotFoundException extends RuntimeException {
    private static final String USER_INFO_END_POINT_NOT_FOUND_MSG = "User Info End Point not found";

    public OAuth2UserInfoEndPointNotFoundException() {
        super(USER_INFO_END_POINT_NOT_FOUND_MSG);
    }
}
