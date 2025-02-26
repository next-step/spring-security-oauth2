package nextstep.oauth2.userinfo;

import java.util.Map;
import java.util.Set;

public class DefaultOAuth2User implements OAuth2User {

    private final Map<String, Object> attributes;
    private final String nameAttributeKey;

    public DefaultOAuth2User(Map<String, Object> attributes, String nameAttributeKey) {
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
    }

    @Override
    public Set<String> authorities() {
        return Set.of();
    }

    @Override
    public Map<String, Object> attributes() {
        return attributes;
    }

    @Override
    public String nameAttributeKey() {
        return nameAttributeKey;
    }
}
