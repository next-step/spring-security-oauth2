package nextstep.security.authentication;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nextstep.security.context.HttpSessionSecurityContextRepository;
import nextstep.security.context.SecurityContext;
import nextstep.security.context.SecurityContextHolder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

public class GithubAuthenticationFilter extends OncePerRequestFilter {

    private final HttpSessionSecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getMethod().equals("GET") && request.getRequestURI().equals("/login/oauth2/code/github")) {

            String code = request.getParameter("code");

            AccessTokenRequestDTO requestDTO = new AccessTokenRequestDTO("Ov23limHOsuGxvCvCFss", "a9347ac79cd41d71844d55c967d99646d39faac7", code);
            String uri = "https://github.com/login/oauth/access_token";
            RestTemplate restTemplate = new RestTemplate();

            HttpEntity<AccessTokenRequestDTO> requestEntity = new HttpEntity<>(requestDTO);
            ResponseEntity<AccessTokenResponseDTO> responseEntity = restTemplate.postForEntity(uri, requestEntity, AccessTokenResponseDTO.class);
            AccessTokenResponseDTO accessTokenResponseDTO = responseEntity.getBody();
            String token = accessTokenResponseDTO.getToken();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", token);
            HttpEntity<Object> httpEntity = new HttpEntity<>(headers);
            String url = "https://api.github.com/user";
            ResponseEntity<UserProfileDTO> userProfileDTOResponseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, UserProfileDTO.class);
            UserProfileDTO body = userProfileDTOResponseEntity.getBody();

            UsernamePasswordAuthenticationToken authenticated = UsernamePasswordAuthenticationToken.authenticated(body.getName(), null, Set.of());
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authenticated);
            SecurityContextHolder.setContext(context);
            securityContextRepository.saveContext(context, request, response);

            response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
            response.sendRedirect("/");
            return;
        }


        filterChain.doFilter(request, response);
    }
}
