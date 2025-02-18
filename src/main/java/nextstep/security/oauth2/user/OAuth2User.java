package nextstep.security.oauth2.user;

import java.util.Set;

public interface OAuth2User {
    String getEmail();

    String getName();

    String getImageUrl();

    Set<String> getRoles();
}
