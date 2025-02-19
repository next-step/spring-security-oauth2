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
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Controller
public class OauthController {
    private static final String GITHUB_TOKEN_REQUEST_URI = "http://localhost:8089/login/oauth/access_token";
    private static final String GITHUB_RESOURCE_REQUEST_URI = "http://localhost:8089/user";

    private static final String RESPONSE_TYPE = "code";

    private static final String GOOGLE_AUTHORIZATION_URI = "https://accounts.google.com/o/oauth2/v2/auth";
    private static final String GOOGLE_CLIENT_ID = "mock_google_client_id";
    private static final String GOOGLE_CLIENT_SECRET = "mock_google_client_secret";
    private static final String GOOGLE_SCOPE = "https://www.googleapis.com/auth/userinfo.profile";
    private static final String GOOGLE_APPROVED_REDIRECT_URI = "http://localhost:8080/login/oauth2/code/google";
    private static final String GOOGLE_TOKEN_REQUEST_URI = "http://localhost:8089/token";
    private static final String GOOGLE_RESOURCE_REQUEST_URI = "http://localhost:8089/oauth2/v2/userinfo";
    private static final String GOOGLE_GRANT_TYPE = "authorization_code";


    private final MemberRepository memberRepository;
    private final HttpSessionSecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();
    private final RestTemplate restTemplate = new RestTemplate();

    public OauthController(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @GetMapping("/oauth2/authorization/google")
    public String googleAuthenticationRequest() {
        String url = UriComponentsBuilder.fromHttpUrl(GOOGLE_AUTHORIZATION_URI)
                .queryParam("client_id", GOOGLE_CLIENT_ID)
                .queryParam("response_type", RESPONSE_TYPE)
                .queryParam("scope", GOOGLE_SCOPE)
                .queryParam("redirect_uri", GOOGLE_APPROVED_REDIRECT_URI)
                .toUriString();

        return "redirect:" + url;
    }

    @GetMapping("/login/oauth2/code/github")
    public String redirectFromGitHubWithCode(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam("code") String code) {
        //토큰요청
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
        return "redirect:/";
    }

    @GetMapping("/login/oauth2/code/google")
    public String redirectFromGoogleWithCode(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam("code") String code) {
        //토큰요청
        Map<String, String> accessTokenRequestBody = new HashMap<>();
        accessTokenRequestBody.put("code", code);
        accessTokenRequestBody.put("redirect_uri", GOOGLE_APPROVED_REDIRECT_URI);
        accessTokenRequestBody.put("client_id", GOOGLE_CLIENT_ID);
        accessTokenRequestBody.put("client_secret", GOOGLE_CLIENT_SECRET);
        accessTokenRequestBody.put("grant_type", GOOGLE_GRANT_TYPE);

        HttpHeaders accessTokenRequestHeaders = new HttpHeaders();
        accessTokenRequestHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> accessTokenRequestEntity = new HttpEntity<>(accessTokenRequestBody, accessTokenRequestHeaders);

        ResponseEntity<Map> accessTokenResponseEntity = restTemplate.exchange(
                GOOGLE_TOKEN_REQUEST_URI, HttpMethod.POST, accessTokenRequestEntity, Map.class);
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
        Map<String, String> resourceRequestBody = new HashMap<>();
        resourceRequestBody.put("access_token", accessToken);

        HttpHeaders resourceHeaders = new HttpHeaders();
        resourceHeaders.setBearerAuth(accessToken);

        HttpEntity<Map<String, String>> resourceRequestEntity = new HttpEntity<>(resourceRequestBody, resourceHeaders);

        ResponseEntity<Map> resourceResponseEntity = restTemplate.exchange(
                GOOGLE_RESOURCE_REQUEST_URI, HttpMethod.GET, resourceRequestEntity, Map.class);
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
