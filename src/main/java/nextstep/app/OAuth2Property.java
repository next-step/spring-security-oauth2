package nextstep.app;

import nextstep.security.authentication.OAuth2Client;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "oauth2")
public class OAuth2Property {

    private Map<String, OAuth2Client> clients;

    public Map<String, OAuth2Client> getClients() {
        return clients;
    }

    public void setClients(Map<String, OAuth2Client> clients) {
        this.clients = clients;
    }

    public OAuth2Client getClient(String key) {
        return clients.get(key);
    }
}
