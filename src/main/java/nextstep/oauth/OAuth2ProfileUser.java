package nextstep.oauth;

import java.util.Map;

public interface OAuth2ProfileUser {

    static OAuth2ProfileUser of(String registrationId, Map<String, Object> attributes) {
        if ("google".equals(registrationId)) {
            return new nextstep.oauth.GoogleProfileUser(attributes);
        }
        if ("github".equals(registrationId)) {
            return new nextstep.oauth.GithubProfileUser(attributes);
        }
        throw new IllegalArgumentException("지원하지 않는 registrationId 입니다.");
    }

    String getName();

    String getImageUrl();

    String getEmail();
}
