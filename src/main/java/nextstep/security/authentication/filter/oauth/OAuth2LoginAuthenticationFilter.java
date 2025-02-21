package nextstep.security.authentication.filter.oauth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nextstep.security.access.MvcRequestMatcher;
import nextstep.security.authentication.Authentication;
import nextstep.security.authentication.AuthenticationException;
import nextstep.security.authentication.oauth.*;
import nextstep.security.context.HttpSessionSecurityContextRepository;
import nextstep.security.context.SecurityContextHolder;
import nextstep.security.userservice.OAuth2UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.NoSuchElementException;

public class OAuth2LoginAuthenticationFilter extends OncePerRequestFilter {

    private static final String CODE = "code";
    private static final String STATE = "state";
    private static final Logger log = LoggerFactory.getLogger(OAuth2LoginAuthenticationFilter.class);

    private final MvcRequestMatcher requestMatcher;
    private final OAuth2TokenRequester auth2TokenRequester;
    private final OAuth2EmailResolver oAuth2EmailResolver;
    private final HttpSessionSecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();
    private final OAuth2UserService auth2UserService;

    public OAuth2LoginAuthenticationFilter(
            OAuth2TokenRequester auth2TokenRequester,
            OAuth2EmailResolver oAuth2EmailResolver,
            OAuth2UserService auth2UserService
    ) {
        this.auth2UserService = auth2UserService;
        this.requestMatcher = new MvcRequestMatcher(HttpMethod.GET, "/login/oauth2/code/{provider}");
        this.auth2TokenRequester = auth2TokenRequester;
        this.oAuth2EmailResolver = oAuth2EmailResolver;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (noRequiresAuthentication(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            Authentication authentication = attemptAuthentication(request);

            successfulAuthentication(request, response, authentication);

        } catch (AuthenticationException | NoSuchElementException e) {
            unsuccessfulAuthentication(response, e);
        }
    }

    private boolean noRequiresAuthentication(HttpServletRequest request) {
        return !requestMatcher.matches(request);
    }

    private Authentication attemptAuthentication(HttpServletRequest request) {
        final String state = getParameterValueByName(request, STATE);
        final OAuth2AuthorizationRequest authorizationRequest = auth2UserService.consumeOAuth2AuthorizationRequest(state);
        if (authorizationRequest == null) {
            throw new AuthenticationException();
        }

        final String code = getParameterValueByName(request, CODE);
        final String registrationId = authorizationRequest.registrationId();

        final var tokenResponse = auth2TokenRequester.request(registrationId, code);
        if (tokenResponse == null || tokenResponse.getAccessToken() == null) {
            throw new AuthenticationException();
        }

        final String username = oAuth2EmailResolver.resolve(registrationId, tokenResponse);

        final OAuth2User auth2User = auth2UserService.loadUserBy(username);

        return OAuth2AuthenticationToken.authenticated(auth2User);
    }

    private String getParameterValueByName(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        if (StringUtils.hasText(value)) {
            return value;
        }

        throw new AuthenticationException("Invalid parameter value : '" + name + "'");
    }

    private void successfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {
        SecurityContextHolder.getContext().setAuthentication(authentication);
        securityContextRepository.saveContext(SecurityContextHolder.getContext(), request);
        response.sendRedirect("/");
    }

    private void unsuccessfulAuthentication(
            HttpServletResponse response,
            RuntimeException e
    ) throws IOException {
        SecurityContextHolder.clearContext();
        log.error(e.getMessage());
        log.error("[stack trace] : ", e);
        response.sendRedirect("/");
    }
}
