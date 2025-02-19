package nextstep.app.application;

import jakarta.servlet.http.HttpServletRequest;
import nextstep.security.access.RequestMatcher;
import nextstep.security.authentication.OAuth2AuthenticationRequestStrategy;
import nextstep.security.authentication.OAuth2ProviderSupportChecker;
import nextstep.security.authentication.UnsupportedOAuth2ProviderException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class OAuth2AnnotationProviderSupportChecker implements OAuth2ProviderSupportChecker {

    private final Set<String> supportedProviders;

    public OAuth2AnnotationProviderSupportChecker(List<OAuth2AuthenticationRequestStrategy> strategies) {
        this.supportedProviders = strategies.stream()
                .map(strategy -> strategy.getClass().getAnnotation(OAuth2Provider.class))
                .map(OAuth2Provider::value)
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public void checkRequest(HttpServletRequest request, RequestMatcher requestMatcher) throws UnsupportedOAuth2ProviderException {
        if (!requestMatcher.matches(request)) {
            throw new UnsupportedOAuth2ProviderException("Not an OAuth2 callback URL");
        }

        String path = request.getRequestURI();
        String provider = path.substring(path.lastIndexOf('/') + 1);

        if (!supportedProviders.contains(provider)) {
            throw new UnsupportedOAuth2ProviderException(provider);
        }
    }
}
