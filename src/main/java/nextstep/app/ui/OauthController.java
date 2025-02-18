package nextstep.app.ui;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nextstep.app.domain.Member;
import nextstep.app.domain.MemberRepository;
import nextstep.security.authentication.UsernamePasswordAuthenticationToken;
import nextstep.security.context.HttpSessionSecurityContextRepository;
import nextstep.security.context.SecurityContext;
import nextstep.security.context.SecurityContextHolder;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Controller
public class OauthController {
    private final MemberRepository memberRepository;
    private final HttpSessionSecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();
    private final RestTemplate restTemplate = new RestTemplate();

    public OauthController(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @GetMapping("/oauth2/authorization/github")
    public String gitHubAuthenticationRequest() {
        String clientId = "Ov23liTBhugSIcf8VX1v";
        String responseType = "code";
        String scope = "read:user";
        String redirectUrl = "http://localhost:8080/login/oauth2/code/github";

        String url = "https://github.com/login/oauth/authorize" +
                "?client_id=" + clientId +
                "&response_type=" + responseType +
                "&scope=" + scope +
                "&redirect_uri=" + redirectUrl;

        return "redirect:" + url;
    }

    @GetMapping("/oauth2/authorization/google")
    public String googleAuthenticationRequest() {
        String clientId = "Ov23liTBhugSIcf8VX1v";
        String responseType = "code";
        String scope = "email%20profile";
        String redirectUrl = "http://localhost:8080/login/oauth2/code/google";

        String url = "https://accounts.google.com/o/oauth2/v2/auth" +
                "?client_id=" + clientId +
                "&response_type=" + responseType +
                "&scope=" + scope +
                "&redirect_uri=" + redirectUrl;

        return "redirect:" + url;
    }

    @GetMapping("/login/oauth2/code/github")
    public String redirectFromGitHubWithCode(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam("code") String code) {
        //토큰요청
        String accessTokenRequestUrl = "http://localhost:8089/login/oauth/access_token";

        Map<String, String> accessTokenRequestBody = new HashMap<>();
        accessTokenRequestBody.put("code", code);

        HttpHeaders accessTokenHeaders = new HttpHeaders();
        accessTokenHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> accessTokenRequestEntity = new HttpEntity<>(accessTokenRequestBody, accessTokenHeaders);

        ResponseEntity<Map> accessTokenResponseEntity = restTemplate.exchange(
                accessTokenRequestUrl, HttpMethod.POST, accessTokenRequestEntity, Map.class);
        Map<String, Object> accessTokenBody = (Map<String, Object>) accessTokenResponseEntity.getBody();
        if (Objects.isNull(accessTokenBody)) {
            throw new RuntimeException();
        }
        String accessToken = accessTokenBody.get("access_token").toString();
        String tokenType = accessTokenBody.get("token_type").toString();

        //리소스 조회 요청
        String resourceRequestUrl = "http://localhost:8089/user";

        Map<String, String> resourceRequestBody = new HashMap<>();
        resourceRequestBody.put("access_token", accessToken);

        HttpHeaders resourceHeaders = new HttpHeaders();
        resourceHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> resourceRequestEntity = new HttpEntity<>(resourceRequestBody, resourceHeaders);

        ResponseEntity<Map> resourceResponseEntity = restTemplate.exchange(
                resourceRequestUrl, HttpMethod.GET, resourceRequestEntity, Map.class);
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
        return "redirect:/";
    }

    @GetMapping("/login/oauth2/code/google")
    public String redirectFromGoogleWithCode(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam("code") String code) {
        //토큰요청
        String accessTokenRequestUrl = "http://localhost:8089/token";

        Map<String, String> accessTokenRequestBody = new HashMap<>();
        accessTokenRequestBody.put("code", code);

        HttpHeaders accessTokenHeaders = new HttpHeaders();
        accessTokenHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> accessTokenRequestEntity = new HttpEntity<>(accessTokenRequestBody, accessTokenHeaders);

        ResponseEntity<Map> accessTokenResponseEntity = restTemplate.exchange(
                accessTokenRequestUrl, HttpMethod.POST, accessTokenRequestEntity, Map.class);
        Map<String, Object> accessTokenBody = (Map<String, Object>) accessTokenResponseEntity.getBody();
        if (Objects.isNull(accessTokenBody)) {
            throw new RuntimeException();
        }
        String accessToken = accessTokenBody.get("access_token").toString();
        String expiresIn = accessTokenBody.get("expires_in").toString();
        String refreshToken = accessTokenBody.get("refresh_token").toString();
        String scope = accessTokenBody.get("scope").toString();
        String tokenType = accessTokenBody.get("token_type").toString();
        String idToken = accessTokenBody.get("id_token").toString();

        //리소스 조회 요청
        String resourceRequestUrl = "http://localhost:8089/v1/userinfo";

        Map<String, String> resourceRequestBody = new HashMap<>();
        resourceRequestBody.put("access_token", accessToken);

        HttpHeaders resourceHeaders = new HttpHeaders();
        resourceHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> resourceRequestEntity = new HttpEntity<>(resourceRequestBody, resourceHeaders);

        ResponseEntity<Map> resourceResponseEntity = restTemplate.exchange(
                resourceRequestUrl, HttpMethod.GET, resourceRequestEntity, Map.class);
        Map<String, Object> resourceBody = (Map<String, Object>) resourceResponseEntity.getBody();
        if (Objects.isNull(resourceBody)) {
            throw new RuntimeException();
        }
        String email = resourceBody.get("email").toString();
        String name = resourceBody.get("name").toString();
        String pictureUrl = resourceBody.get("picture").toString();

        //사용자 조회 혹은 저장
        Member member = memberRepository.findByEmail(email)
                .orElse(memberRepository.save(new Member(email, null, name, pictureUrl, Set.of("USER"))));

        UsernamePasswordAuthenticationToken authenticated = UsernamePasswordAuthenticationToken
                .authenticated(member.getEmail(), null, member.getRoles());

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authenticated);
        SecurityContextHolder.setContext(context);
        this.securityContextRepository.saveContext(context, request, response);
        return "redirect:/";
    }
}
