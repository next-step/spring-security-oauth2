package nextstep.security.oauth2user;

import java.util.Set;

public interface OAuth2User {

    String getName();

    Set<String> getAuthorities();
}
