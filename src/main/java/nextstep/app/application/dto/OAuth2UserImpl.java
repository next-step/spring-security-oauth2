package nextstep.app.application.dto;

import nextstep.security.authentication.oauth.OAuth2User;

import java.util.Map;
import java.util.Set;

public record OAuth2UserImpl(Map<String, Object> attributes, Set<String> authorities) implements OAuth2User {

}
