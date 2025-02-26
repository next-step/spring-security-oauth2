package nextstep.oauth2.userinfo;

import java.util.Map;
import java.util.Set;

public interface OAuth2User {
    Set<String> authorities();

    Map<String, Object> attributes();

    String nameAttributeKey();
}
