package nextstep.oauth2.userinfo;

import java.util.Map;
import java.util.Set;

public interface OAuth2User {
    static DefaultOAuth2User of(
            Set<String> authorities,
            Map<String, Object> attributes,
            String userNameAttributeName
    ) {
        return new DefaultOAuth2User(authorities, attributes, userNameAttributeName);
    }

    Set<String> authorities();

    Map<String, Object> attributes();

    String userNameAttributeName();
}
