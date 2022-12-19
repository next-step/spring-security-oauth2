package nextstep.security.authentication;

import java.util.Set;

public interface OAuth2User {

    String getName();

    Set<String> getAuthorities();
}
