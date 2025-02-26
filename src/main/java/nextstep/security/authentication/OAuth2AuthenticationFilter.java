package nextstep.security.authentication;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nextstep.security.access.RegexRequestMatcher;
import nextstep.security.context.HttpSessionSecurityContextRepository;
import nextstep.security.context.SecurityContext;
import nextstep.security.context.SecurityContextHolder;
import nextstep.security.userdetails.UserDetailsService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class OAuth2AuthenticationFilter extends OncePerRequestFilter {

    private final RestTemplate restTemplate = new RestTemplate();
    private static final RegexRequestMatcher OAUTH2_AUTHENTICATION_REQUEST_MATCHER = new RegexRequestMatcher(HttpMethod.GET, "/login/oauth2/code/.*");
    private final HttpSessionSecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();
    private final AuthenticationManager authenticationManager;
    private final OAuth2ClientRepository oAuth2ClientRepository;

    public OAuth2AuthenticationFilter(UserDetailsService userDetailsService, OAuth2ClientRepository oAuth2ClientRepository) {
        this.authenticationManager = new ProviderManager(
                List.of(new OAuth2AuthenticationProvider(userDetailsService)));
        this.oAuth2ClientRepository = oAuth2ClientRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if (!OAUTH2_AUTHENTICATION_REQUEST_MATCHER.matches(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String clientRegistrationId = extractRegistrationId(request);
        ClientRegistration clientRegistration = oAuth2ClientRepository.findByRegistrationId(clientRegistrationId);
        if (clientRegistration == null) {
            throw new AuthenticationException();
        }

        String code = request.getParameter("code");
        String token = getAccessToken(clientRegistration, code);
        UserProfile userProfile = getUserProfile(token, clientRegistration);

        UsernamePasswordAuthenticationToken unauthenticated = UsernamePasswordAuthenticationToken.unauthenticated(userProfile.getEmail(), null);
        Authentication authenticated = authenticationManager.authenticate(unauthenticated);
        saveSecurityContext(request, response, authenticated);

        response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
        response.sendRedirect("/");
    }

    private static String extractRegistrationId(HttpServletRequest request) {
        return request.getRequestURI().substring("/login/oauth2/code/".length());
    }

    private String getAccessToken(ClientRegistration clientRegistration, String code) {
        String tokenUri = clientRegistration.getTokenUri();
        MultiValueMap<String, String> paramsForToken = clientRegistration.getParamsForToken(code);
        AccessTokenResponseDTO tokenResponse = restTemplate.postForObject(tokenUri, paramsForToken, AccessTokenResponseDTO.class);
        return tokenResponse.getToken();
    }

    private UserProfile getUserProfile(String token, ClientRegistration clientRegistration) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);
        String userInfoUri = clientRegistration.getUserInfoUri();
        ResponseEntity<UserProfile> userProfileDTOResponseEntity = restTemplate.exchange(userInfoUri, HttpMethod.GET, httpEntity, UserProfile.class);
        return userProfileDTOResponseEntity.getBody();
    }

    private void saveSecurityContext(HttpServletRequest request, HttpServletResponse response, Authentication authenticated) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authenticated);
        SecurityContextHolder.setContext(context);
        securityContextRepository.saveContext(context, request, response);
    }
}
