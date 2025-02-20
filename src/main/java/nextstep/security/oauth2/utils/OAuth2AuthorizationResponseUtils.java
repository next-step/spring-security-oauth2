package nextstep.security.oauth2.utils;

import nextstep.security.oauth2.exception.OAuth2AuthenticationException;
import nextstep.security.oauth2.exchange.OAuth2AuthorizationResponse;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import java.util.Map;

public final class OAuth2AuthorizationResponseUtils {
    private OAuth2AuthorizationResponseUtils() {
    }

    public static boolean isAuthorizationResponse(MultiValueMap<String, String> request) {
        return isAuthorizationResponseSuccess(request) || isAuthorizationResponseError(request);
    }

    public static boolean isAuthorizationResponseSuccess(MultiValueMap<String, String> request) {
        return StringUtils.hasText(request.getFirst(OAuth2ParameterNames.CODE))
                && StringUtils.hasText(request.getFirst(OAuth2ParameterNames.STATE));
    }

    public static boolean isAuthorizationResponseError(MultiValueMap<String, String> request) {
        return StringUtils.hasText(request.getFirst(OAuth2ParameterNames.ERROR))
                && StringUtils.hasText(request.getFirst(OAuth2ParameterNames.STATE));
    }

    public static MultiValueMap<String, String> toMultiMap(Map<String, String[]> map) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>(map.size());
        map.forEach((key, values) -> {
            for (String value : values) {
                params.add(key, value);
            }
        });
        return params;
    }

    public static OAuth2AuthorizationResponse convert(MultiValueMap<String, String> params) {
        String code = params.getFirst(OAuth2ParameterNames.CODE);
        String state = params.getFirst(OAuth2ParameterNames.STATE);
        if (StringUtils.hasText(code)) {
            return OAuth2AuthorizationResponse.builder().code(code).state(state).build();
        }

        throw new OAuth2AuthenticationException("OAuth2AuthorizationRespone code is null");
    }
}
