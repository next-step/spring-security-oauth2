package nextstep.security.authentication;

import com.fasterxml.jackson.annotation.JsonProperty;
import nextstep.security.access.matcher.RequestMatcher;
import nextstep.security.context.SecurityContext;
import nextstep.security.context.SecurityContextHolder;
import nextstep.security.context.SecurityContextRepository;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class Oauth2LoginAuthenticationFilter extends OncePerRequestFilter {

    private final RequestMatcher requestMatcher;
    private final WebClient webClient;
    private final SecurityContextRepository securityContextRepository;

    public Oauth2LoginAuthenticationFilter(
        RequestMatcher requestMatcher,
        WebClient webClient,
        SecurityContextRepository securityContextRepository
    ) {
        this.requestMatcher = requestMatcher;
        this.webClient = webClient;
        this.securityContextRepository = securityContextRepository;
    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        if (requestMatcher.matches(request)) {
            final String accessToken = request.getParameter("code");

            final AccessResponse accessResponse = webClient.post()
                .uri("https://github.com/login/oauth/access_token")
                .accept(MediaType.APPLICATION_JSON)
                .body(
                    BodyInserters.fromValue(new AccessRequest(accessToken))
                )
                .retrieve()
                .bodyToMono(AccessResponse.class)
                .block();

            final UserResponse userResponse = webClient.get()
                .uri("https://api.github.com/user")
                .header("Authorization", "Bearer " + accessResponse.getAccessToken())
                .retrieve()
                .bodyToMono(UserResponse.class)
                .block();

            final SecurityContext context = SecurityContextHolder.getContext();
            context.setAuthentication(new Oauth2Authentication(userResponse.getId()));
            securityContextRepository.saveContext(context, request, response);
            response.sendRedirect("/members/authentication");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private static class AccessRequest {
        @JsonProperty("client_id")
        private String clientId = "7fc956935c0618c560da";
        @JsonProperty("client_secret")
        private String clientSecret = "a3fd00e8f3146bf81e2c5b2ea328ccb8d330cd45";
        private String code;
        @JsonProperty("redirect_uri")
        private String redirectUri;

        public AccessRequest(String code) {
            this.code = code;
        }

        public String getClientId() {
            return clientId;
        }

        public String getClientSecret() {
            return clientSecret;
        }

        public String getCode() {
            return code;
        }

        public String getRedirectUri() {
            return redirectUri;
        }

        public void setRedirectUri(String redirectUri) {
            this.redirectUri = redirectUri;
        }
    }

    private static class AccessResponse {
        @JsonProperty("access_token")
        private String accessToken;
        private String scope;
        @JsonProperty("token_type")
        private String tokenType;

        public String getAccessToken() {
            return accessToken;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }

        public String getScope() {
            return scope;
        }

        public void setScope(String scope) {
            this.scope = scope;
        }

        public String getTokenType() {
            return tokenType;
        }

        public void setTokenType(String tokenType) {
            this.tokenType = tokenType;
        }
    }

    private static class UserResponse {
        private String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
}
