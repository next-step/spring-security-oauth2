package nextstep.security.authentication.oauth;

import java.util.Map;
import java.util.Set;

public interface OAuth2User {
    Map<String, Object> attributes();
    Set<String> authorities();
    default String getUserNameAttributeName() {
        return "username";
    }
}
