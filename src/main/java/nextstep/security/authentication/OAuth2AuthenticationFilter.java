package nextstep.security.authentication;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
    private final HttpSessionSecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();
    private final AuthenticationManager authenticationManager;
    private final OAuth2ClientFactory oAuth2ClientFactory;

    public OAuth2AuthenticationFilter(UserDetailsService userDetailsService, OAuth2ClientFactory oAuth2ClientFactory) {
        this.authenticationManager = new ProviderManager(
                List.of(new OAuth2AuthenticationProvider(userDetailsService)));
        this.oAuth2ClientFactory = oAuth2ClientFactory;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        ClientRegistration clientRegistration = oAuth2ClientFactory.getOAuth2Client(request);
        if (clientRegistration == null) {
            filterChain.doFilter(request, response);
            return;
        }

        String code = request.getParameter("code");
        String token = getAccessToken(clientRegistration, code);
        UserProfileDTO userProfile = getUserProfile(token, clientRegistration);

        UsernamePasswordAuthenticationToken unauthenticated = UsernamePasswordAuthenticationToken.unauthenticated(userProfile.getEmail(), null);
        Authentication authenticated = authenticationManager.authenticate(unauthenticated);
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authenticated);
        SecurityContextHolder.setContext(context);
        securityContextRepository.saveContext(context, request, response);

        response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
        response.sendRedirect("/");
    }

    private UserProfileDTO getUserProfile(String token, ClientRegistration clientRegistration) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);
        String userInfoUri = clientRegistration.getUserInfoUri();
        ResponseEntity<UserProfileDTO> userProfileDTOResponseEntity = restTemplate.exchange(userInfoUri, HttpMethod.GET, httpEntity, UserProfileDTO.class);
        return userProfileDTOResponseEntity.getBody();
    }

    private String getAccessToken(ClientRegistration clientRegistration, String code) {
        String tokenUri = clientRegistration.getTokenUri();
        MultiValueMap<String, String> paramsForToken = clientRegistration.getParamsForToken(code);
        AccessTokenResponseDTO tokenResponse = restTemplate.postForObject(tokenUri, paramsForToken, AccessTokenResponseDTO.class);
        return tokenResponse.getToken();
    }
}
