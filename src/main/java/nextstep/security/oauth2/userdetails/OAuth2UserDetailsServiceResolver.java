package nextstep.security.oauth2.userdetails;

import jakarta.annotation.Nonnull;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OAuth2UserDetailsServiceResolver {
    private final Map<String, OAuth2UserDetailsService> userDetailsServiceMap;

    public OAuth2UserDetailsServiceResolver(final List<OAuth2UserDetailsService> userDetailsServices) {
        this.userDetailsServiceMap = userDetailsServices.stream()
                .collect(Collectors.toMap(OAuth2UserDetailsService::getRegistrationId, it -> it));

    }

    @Nonnull
    public OAuth2UserDetailsService getService(String vendor) {
        OAuth2UserDetailsService oAuth2UserDetailsService = userDetailsServiceMap.get(vendor);
        if (oAuth2UserDetailsService == null) {
            throw new IllegalArgumentException("oauth2 벤더사를 찾을 수 없습니다.");
        }
        return oAuth2UserDetailsService;
    }

    ;
}
