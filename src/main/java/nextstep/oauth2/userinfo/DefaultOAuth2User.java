package nextstep.oauth2.userinfo;

import java.util.Map;
import java.util.Set;

public record DefaultOAuth2User(
        Set<String> authorities,
        Map<String, Object> attributes,
        String userNameAttributeName
) implements OAuth2User {}
