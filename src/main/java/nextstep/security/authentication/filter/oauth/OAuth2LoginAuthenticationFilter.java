package nextstep.security.authentication.filter.oauth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nextstep.security.access.MvcRequestMatcher;
import nextstep.security.authentication.AuthenticationException;
import nextstep.security.authentication.oauth.OAuth2AuthenticationToken;
import nextstep.security.authentication.oauth.OAuth2AuthorizationRequest;
import nextstep.security.authentication.oauth.OAuth2EmailResolver;
import nextstep.security.authentication.oauth.OAuth2TokenRequester;
import nextstep.security.context.HttpSessionSecurityContextRepository;
import nextstep.security.context.SecurityContextHolder;
import nextstep.security.userservice.OAuth2UserService;
import nextstep.security.userservice.UserDetails;
import nextstep.security.userservice.UserDetailsService;
import org.springframework.http.HttpMethod;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class OAuth2LoginAuthenticationFilter extends OncePerRequestFilter {

    private static final String CODE = "code";
    private static final String STATE = "state";

    private final MvcRequestMatcher requestMatcher;
    private final OAuth2TokenRequester auth2TokenRequester;
    private final OAuth2EmailResolver oAuth2EmailResolver;
    private final UserDetailsService userDetailsService;
    private final HttpSessionSecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();
    private final OAuth2UserService auth2UserService;

    public OAuth2LoginAuthenticationFilter(
            OAuth2TokenRequester auth2TokenRequester,
            OAuth2EmailResolver oAuth2EmailResolver,
            UserDetailsService userDetailsService,
            OAuth2UserService auth2UserService
    ) {
        this.auth2UserService = auth2UserService;
        this.requestMatcher = new MvcRequestMatcher(HttpMethod.GET, "/login/oauth2/code/{provider}");
        this.auth2TokenRequester = auth2TokenRequester;
        this.oAuth2EmailResolver = oAuth2EmailResolver;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (noRequiresAuthentication(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        final String code = getParameterValueByName(request, CODE);
        final String state = getParameterValueByName(request, STATE);

        final OAuth2AuthorizationRequest authorizationRequest = auth2UserService.consumeOAuth2AuthorizationRequest(state);
        if (authorizationRequest == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        final String registrationId = authorizationRequest.registrationId();

        final var tokenResponse = auth2TokenRequester.request(registrationId, code);
        if (tokenResponse == null || tokenResponse.getAccessToken() == null) {
            throw new AuthenticationException();
        }

        String username = oAuth2EmailResolver.resolve(registrationId, tokenResponse);
        UserDetails userDetails = getUserOrCreateIfNotExists(username);

        registerAuthenticationContext(request, userDetails);
        response.sendRedirect("/");
    }

    private boolean noRequiresAuthentication(HttpServletRequest request) {
        return !requestMatcher.matches(request);
    }

    private String getParameterValueByName(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        if (StringUtils.hasText(value)) {
            return value;
        }

        throw new AuthenticationException("Invalid parameter value : '" + name + "'");
    }

    private UserDetails getUserOrCreateIfNotExists(String username) {
        UserDetails userDetails;
        try {
            userDetails = userDetailsService.loadUserByUsername(username);
        } catch (AuthenticationException ex) {
            userDetails = userDetailsService.addNewMemberByOAuth2(username, username);
        }
        return userDetails;
    }

    private void registerAuthenticationContext(HttpServletRequest request, UserDetails userDetails) {
        OAuth2AuthenticationToken authenticated = OAuth2AuthenticationToken.authenticated(userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticated);
        securityContextRepository.saveContext(SecurityContextHolder.getContext(), request);
    }
}
