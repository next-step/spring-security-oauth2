package nextstep.security.oauth2.user;

import java.util.Set;

public interface Oauth2User {
    String getEmail();
    Set<String> getAuthorities();
}
