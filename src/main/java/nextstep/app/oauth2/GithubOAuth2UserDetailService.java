package nextstep.app.oauth2;

import nextstep.security.oauth2.userdetails.AbstractOAuth2UserDetailsService;
import nextstep.security.oauth2.userdetails.OAuth2UserDetails;
import nextstep.security.properties.ClientRegistrationRepository;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
public class GithubOAuth2UserDetailService extends AbstractOAuth2UserDetailsService {

    public GithubOAuth2UserDetailService(final ClientRegistrationRepository clientRegistrationRepository) {
        super(clientRegistrationRepository);
    }

    @Override
    protected OAuth2UserDetails createOauth2UserDetails(final Map<String, Object> userResponse) {
        var email = (String) userResponse.get("email");
        var name = (String) userResponse.get("name");
        var avatarUrl = (String) userResponse.get("avatar_url");

        return new OAuth2UserDetailsImpl(email, Map.of("name", name,
                "avatarUrl", avatarUrl),
                Set.of("USER"));
    }

    @Override
    public String getRegistrationId() {
        return "github";
    }
}
