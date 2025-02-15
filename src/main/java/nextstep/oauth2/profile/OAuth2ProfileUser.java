package nextstep.oauth2.profile;

import nextstep.oauth2.exception.UnsupportedRegistrationIdException;

import java.util.Map;

public interface OAuth2ProfileUser {
    static OAuth2ProfileUser of(String registrationId, Map<String, Object> attributes) {
        if ("google".equals(registrationId)) {
            return GoogleProfileUser.of(attributes);
        }
        if ("github".equals(registrationId)) {
            return GithubProfileUser.of(attributes);
        }
        throw new UnsupportedRegistrationIdException();
    }

    String name();

    String imageUrl();

    String email();
}
