package nextstep.security.oauth2;

import nextstep.security.oauth2.github.GithubUser;
import nextstep.security.access.matcher.MvcRequestMatcher;
import nextstep.security.authentication.Authentication;
import nextstep.security.context.SecurityContext;
import nextstep.security.context.SecurityContextHolder;
import nextstep.security.context.SecurityContextRepository;
import nextstep.security.savedRequest.RequestCache;
import nextstep.security.savedRequest.SavedRequest;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public class OAuth2AccessTokenLoginFilter extends OncePerRequestFilter {

    private final SecurityContextRepository securityContextRepository;

    private final RequestCache requestCache;
    private static final MvcRequestMatcher DEFAULT_REQUEST_MATCHER = new MvcRequestMatcher(HttpMethod.GET,
            "/login/oauth2/code/github");

    public OAuth2AccessTokenLoginFilter(SecurityContextRepository securityContextRepository, RequestCache requestCache) {
        this.securityContextRepository = securityContextRepository;
        this.requestCache = requestCache;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        if (!DEFAULT_REQUEST_MATCHER.matches(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String code = request.getParameter("code");

        AccessToken tokenResponse = getAccessToken(code);

        GithubUser userResponse = getGithubUser(tokenResponse);

        Authentication authentication = OAuth2Authentication.ofRequest(userResponse.getLoginId());
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(authentication);

        securityContextRepository.saveContext(context, request, response);

        Optional<SavedRequest> savedRequest = requestCache.getRequest(request, response);
        if (savedRequest.isPresent()) {
            response.sendRedirect(savedRequest.get().getRedirectUrl());
        }
    }

    private static AccessToken getAccessToken(String code) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", "04f8811a8c4aafe9baab");
        params.add("client_secret", "c465a904598dc994cf5f5ea9a20cdfc23bed1e74");
        params.add("code", code);
        WebClient webClient = WebClient.create("https://github.com");

        return webClient.post()
                .uri("/login/oauth/access_token")
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromFormData(params))
                .retrieve()
                .bodyToMono(AccessToken.class)
                .block();
    }

    private static GithubUser getGithubUser(AccessToken tokenResponse) {
        WebClient webClient2 = WebClient.create("https://api.github.com");

        return webClient2.get()
                .uri("/user")
                .header(HttpHeaders.AUTHORIZATION, tokenResponse.getTokenType() + " " + tokenResponse.getAccessToken())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(GithubUser.class)
                .block();
    }
}
