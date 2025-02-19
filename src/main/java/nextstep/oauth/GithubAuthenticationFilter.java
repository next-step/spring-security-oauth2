package nextstep.oauth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nextstep.app.domain.Member;
import nextstep.app.domain.MemberRepository;
import nextstep.security.authentication.UsernamePasswordAuthenticationToken;
import nextstep.security.context.HttpSessionSecurityContextRepository;
import nextstep.security.context.SecurityContext;
import nextstep.security.context.SecurityContextHolder;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class GithubAuthenticationFilter extends GenericFilterBean {
    private static final String MATCH_REQUEST_URI = "/login/oauth2/code/github";
    private static final String GITHUB_TOKEN_REQUEST_URI = "http://localhost:8089/login/oauth/access_token";
    private static final String GITHUB_RESOURCE_REQUEST_URI = "http://localhost:8089/user";

    private final HttpSessionSecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();
    private final RestTemplate restTemplate = new RestTemplate();
    private final MemberRepository memberRepository;

    public GithubAuthenticationFilter(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        if (!request.getRequestURI().equals(MATCH_REQUEST_URI)) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        //토큰요청
        String code = request.getParameter("code");
        Map<String, String> accessTokenRequestBody = new HashMap<>();
        accessTokenRequestBody.put("code", code);

        HttpHeaders accessTokenHeaders = new HttpHeaders();
        accessTokenHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> accessTokenRequestEntity = new HttpEntity<>(accessTokenRequestBody, accessTokenHeaders);

        ResponseEntity<Map> accessTokenResponseEntity = restTemplate.exchange(
                GITHUB_TOKEN_REQUEST_URI, HttpMethod.POST, accessTokenRequestEntity, Map.class);
        Map<String, Object> accessTokenBody = (Map<String, Object>) accessTokenResponseEntity.getBody();
        if (Objects.isNull(accessTokenBody)) {
            throw new RuntimeException();
        }
        String accessToken = accessTokenBody.get("access_token").toString();
        String tokenType = accessTokenBody.get("token_type").toString();

        //리소스 조회 요청
        Map<String, String> resourceRequestBody = new HashMap<>();
        resourceRequestBody.put("access_token", accessToken);

        HttpHeaders resourceHeaders = new HttpHeaders();
        resourceHeaders.setBearerAuth(accessToken);

        HttpEntity<Map<String, String>> resourceRequestEntity = new HttpEntity<>(resourceRequestBody, resourceHeaders);

        ResponseEntity<Map> resourceResponseEntity = restTemplate.exchange(
                GITHUB_RESOURCE_REQUEST_URI, HttpMethod.GET, resourceRequestEntity, Map.class);
        Map<String, Object> resourceBody = (Map<String, Object>) resourceResponseEntity.getBody();
        if (Objects.isNull(resourceBody)) {
            throw new RuntimeException();
        }
        String email = resourceBody.get("email").toString();
        String name = resourceBody.get("name").toString();
        String avatarUrl = resourceBody.get("avatar_url").toString();

        //사용자 조회 혹은 저장
        Member member = memberRepository.findByEmail(email)
                .orElse(memberRepository.save(new Member(email, null, name, avatarUrl, Set.of("USER"))));

        UsernamePasswordAuthenticationToken authenticated = UsernamePasswordAuthenticationToken
                .authenticated(member.getEmail(), null, member.getRoles());

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authenticated);
        SecurityContextHolder.setContext(context);
        this.securityContextRepository.saveContext(context, request, response);

        response.sendRedirect("/");
    }
}
