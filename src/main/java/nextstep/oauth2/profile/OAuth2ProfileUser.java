package nextstep.oauth2.profile;

import nextstep.oauth2.exception.OAuth2RegistrationNotSupportedException;

import java.util.Map;

public interface OAuth2ProfileUser {

    static OAuth2ProfileUser of(String registrationId, Map<String, Object> attributes) {
        if ("google".equals(registrationId)) {
            return new GoogleProfileUser(attributes);
        }
        if ("github".equals(registrationId)) {
            return new GithubProfileUser(attributes);
        }
        throw new OAuth2RegistrationNotSupportedException(registrationId);
    }

    String getName();

    String getImageUrl();

    String getEmail();
}
