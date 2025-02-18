package nextstep.security.oauth2.exchange;

public class OAuth2AuthorizationResponse {
    private String code;
    private String state;

    public OAuth2AuthorizationResponse(String code, String state) {
        this.code = code;
        this.state = state;
    }

    public String getCode() {
        return code;
    }

    public String getState() {
        return state;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String code;
        private String state;

        public Builder code(String code) {
            this.code = code;
            return this;
        }

        public Builder state(String state) {
            this.state = state;
            return this;
        }

        public OAuth2AuthorizationResponse build() {
            return new OAuth2AuthorizationResponse(code, state);
        }
    }
}
