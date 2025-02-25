package nextstep.security.oauth2.client.web;

import java.util.Map;
import nextstep.security.oauth2.core.OAuth2AuthorizationResponse;
import nextstep.security.oauth2.core.OAuth2ParameterNames;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

public class OAuth2AuthorizationResponseUtils {
    private OAuth2AuthorizationResponseUtils() {
    }

    static MultiValueMap<String, String> toMultiMap(Map<String, String[]> map) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>(map.size());
        map.forEach((key, values) -> {
            for (String value : values) {
                params.add(key, value);
            }
        });
        return params;
    }

    static boolean isAuthorizationResponse(MultiValueMap<String, String> request) {
        return isAuthorizationResponseSuccess(request) || isAuthorizationResponseError(request);
    }

    static boolean isAuthorizationResponseSuccess(MultiValueMap<String, String> request) {
        return StringUtils.hasText(request.getFirst(OAuth2ParameterNames.CODE.getValue()));
    }

    static boolean isAuthorizationResponseError(MultiValueMap<String, String> request) {
        return StringUtils.hasText(request.getFirst(OAuth2ParameterNames.ERROR.getValue()));
    }

    public static OAuth2AuthorizationResponse convert(MultiValueMap<String, String> request, String redirectUri) {
        String code = request.getFirst(OAuth2ParameterNames.CODE.getValue());
        String errorCode = request.getFirst(OAuth2ParameterNames.ERROR.getValue());
        if (StringUtils.hasText(code)) {
            return OAuth2AuthorizationResponse.success(code, redirectUri);
        }
        return OAuth2AuthorizationResponse.error(errorCode, redirectUri);
    }
}
