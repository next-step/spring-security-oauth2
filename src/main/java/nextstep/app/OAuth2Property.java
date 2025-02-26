package nextstep.app;

import nextstep.security.authentication.ClientRegistration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "oauth2")
public class OAuth2Property {

    private Map<String, ClientRegistration> clients;

    public Map<String, ClientRegistration> getClients() {
        return clients;
    }

    public void setClients(Map<String, ClientRegistration> clients) {
        this.clients = clients;
    }

    public ClientRegistration getClient(String key) {
        return clients.get(key);
    }
}
