package nextstep.security.oauth2.core.user;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import nextstep.app.domain.Member;

public class DefaultOAuth2User implements OAuth2User {

    private final Member member;
    private final Map<String, Object> attributes;
    private final String userNameAttributeName;

    // 생성자
    public DefaultOAuth2User(Member member, Map<String, Object> attributes, String userNameAttributeName) {
        this.member = member;
        this.attributes = attributes;
        this.userNameAttributeName = userNameAttributeName;
    }

    @Override
    public String getUserNameAttributeName() {
        return userNameAttributeName;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Set<String> getAuthorities() {
        return member.getRoles();
    }
}
