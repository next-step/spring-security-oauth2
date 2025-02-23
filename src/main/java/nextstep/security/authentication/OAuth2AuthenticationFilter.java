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

        OAuth2Client oAuth2Client = oAuth2ClientFactory.getOAuth2Client(request);
        if (oAuth2Client == null) {
            filterChain.doFilter(request, response);
            return;
        }

        String code = request.getParameter("code");
        String token = getAccessToken(oAuth2Client, code);
        UserProfileDTO userProfile = getUserProfile(token, oAuth2Client);

        UsernamePasswordAuthenticationToken unauthenticated = UsernamePasswordAuthenticationToken.unauthenticated(userProfile.getEmail(), null);
        Authentication authenticated = authenticationManager.authenticate(unauthenticated);
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authenticated);
        SecurityContextHolder.setContext(context);
        securityContextRepository.saveContext(context, request, response);

        response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
        response.sendRedirect("/");
    }

    private UserProfileDTO getUserProfile(String token, OAuth2Client oAuth2Client) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);
        String userInfoUri = oAuth2Client.getUserInfoUri();
        ResponseEntity<UserProfileDTO> userProfileDTOResponseEntity = restTemplate.exchange(userInfoUri, HttpMethod.GET, httpEntity, UserProfileDTO.class);
        return userProfileDTOResponseEntity.getBody();
    }

    private String getAccessToken(OAuth2Client oAuth2Client, String code) {
        String tokenUri = oAuth2Client.getTokenUri();
        MultiValueMap<String, String> paramsForToken = oAuth2Client.getParamsForToken(code);
        AccessTokenResponseDTO tokenResponse = restTemplate.postForObject(tokenUri, paramsForToken, AccessTokenResponseDTO.class);
        return tokenResponse.getToken();
    }
}
