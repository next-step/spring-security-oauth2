package nextstep.security.authentication.filter.oauth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nextstep.security.access.MvcRequestMatcher;
import nextstep.security.authentication.*;
import nextstep.security.context.HttpSessionSecurityContextRepository;
import nextstep.security.context.SecurityContextHolder;
import nextstep.security.userdetails.UserDetails;
import nextstep.security.userdetails.UserDetailsService;
import org.springframework.http.HttpMethod;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class OAuth2LoginAuthenticationFilter extends OncePerRequestFilter {

    private final MvcRequestMatcher requestMatcher;
    private final OAuth2TokenRequester auth2TokenRequester;
    private final OAuth2EmailResolver oAuth2EmailResolver;
    private final UserDetailsService userDetailsService;
    private final OAuth2ProviderSupportChecker providerSupportChecker;
    private final HttpSessionSecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    public OAuth2LoginAuthenticationFilter(OAuth2TokenRequester auth2TokenRequester, OAuth2EmailResolver oAuth2EmailResolver, UserDetailsService userDetailsService, OAuth2ProviderSupportChecker providerSupportChecker) {
        this.providerSupportChecker = providerSupportChecker;
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

        String oAuth2Type = getOAuth2Type(request.getRequestURI());
        String code = getCodeFromParam(request);

        TokenResponse tokenResponse = auth2TokenRequester.request(oAuth2Type, code);
        if (tokenResponse == null || tokenResponse.getAccessToken() == null) {
            throw new AuthenticationException();
        }

        String username = oAuth2EmailResolver.resolve(oAuth2Type, tokenResponse);
        UserDetails userDetails = getUserOrCreateIfNotExists(username);

        registerAuthenticationContext(request, userDetails);

        response.sendRedirect("/");
    }

    private boolean noRequiresAuthentication(HttpServletRequest request) {
        try {
            providerSupportChecker.checkRequest(request, requestMatcher);
            return false;
        } catch (UnsupportedOAuth2ProviderException e) {
            return true;
        }
    }

    private String getOAuth2Type(String requestUri) {
        return requestUri.substring(requestUri.lastIndexOf("/") + 1);
    }

    private String getCodeFromParam(HttpServletRequest request) {
        String code = request.getParameter("code");
        if (StringUtils.hasText(code)) {
            return code;
        }

        throw new AuthenticationException("Invalid code");
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
