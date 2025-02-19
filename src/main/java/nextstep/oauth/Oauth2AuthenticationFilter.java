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

public class Oauth2AuthenticationFilter extends GenericFilterBean {
    private static final String MATCH_REQUEST_URI_PREFIX = "/login/oauth2/code/";

    private final RestTemplate restTemplate = new RestTemplate();
    private final HttpSessionSecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    private final MemberRepository memberRepository;
    private final OAuth2ClientProperties oAuth2ClientProperties;

    public Oauth2AuthenticationFilter(MemberRepository memberRepository, OAuth2ClientProperties oAuth2ClientProperties) {
        this.memberRepository = memberRepository;
        this.oAuth2ClientProperties = oAuth2ClientProperties;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        if (!request.getRequestURI().startsWith(MATCH_REQUEST_URI_PREFIX)) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        String providerKey = request.getRequestURI().replaceFirst(MATCH_REQUEST_URI_PREFIX, "");

        OAuth2ClientProperties.Provider provider = oAuth2ClientProperties.getProvider().get(providerKey);
        OAuth2ClientProperties.Registration registration = oAuth2ClientProperties.getRegistration().get(providerKey);

        //토큰요청
        String code = request.getParameter("code");
        Map<String, Object> accessTokenBody = getAccessTokenBody(code, provider, registration);

        //리소스 조회 요청
        String accessToken = accessTokenBody.get("access_token").toString();
        String tokenType = accessTokenBody.get("token_type").toString();

        Map<String, Object> resourceBody = getResourceBody(tokenType, accessToken, provider);

        String email = resourceBody.get("email").toString();
        String name = resourceBody.get("name").toString();
        String imageUrl = getImageUrl(providerKey, resourceBody);

        //사용자 조회 혹은 저장
        Member member = memberRepository.findByEmail(email)
                .orElse(memberRepository.save(new Member(email, null, name, imageUrl, Set.of("USER"))));

        UsernamePasswordAuthenticationToken authenticated = UsernamePasswordAuthenticationToken
                .authenticated(member.getEmail(), null, member.getRoles());

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authenticated);
        SecurityContextHolder.setContext(context);
        this.securityContextRepository.saveContext(context, request, response);

        response.sendRedirect("/");
    }

    private String getImageUrl(String providerKey, Map<String, Object> resourceBody) {
        if (providerKey.equals("github")) {
            return resourceBody.get("avatar_url").toString();
        } else if (providerKey.equals("google")) {
            return resourceBody.get("picture").toString();
        }
        return "";
    }

    private Map<String, Object> getAccessTokenBody(String code
            , OAuth2ClientProperties.Provider provider
            , OAuth2ClientProperties.Registration registration) {
        Map<String, String> accessTokenRequestBody = new HashMap<>();
        accessTokenRequestBody.put("code", code);
        accessTokenRequestBody.put("client_id", registration.getClientId());
        accessTokenRequestBody.put("client_secret", registration.getClientSecret());
        accessTokenRequestBody.put("redirect_uri", registration.getRedirectUri());
        accessTokenRequestBody.put("grant_type", "authorization_code");

        HttpHeaders accessTokenRequestHeaders = new HttpHeaders();
        accessTokenRequestHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> accessTokenRequestEntity = new HttpEntity<>(accessTokenRequestBody, accessTokenRequestHeaders);

        ResponseEntity<Map> accessTokenResponseEntity = restTemplate.exchange(
                provider.getTokenUri(), HttpMethod.POST, accessTokenRequestEntity, Map.class);
        Map<String, Object> accessTokenBody = (Map<String, Object>) accessTokenResponseEntity.getBody();

        if (Objects.isNull(accessTokenBody)) {
            throw new RuntimeException("Invalid access token body");
        }

        return accessTokenBody;
    }

    private Map<String, Object> getResourceBody(String tokenType
            , String accessToken
            , OAuth2ClientProperties.Provider provider) {
        if (!tokenType.equals("Bearer")) {
            throw new RuntimeException("Invalid access token type");
        }

        Map<String, String> resourceRequestBody = new HashMap<>();
        resourceRequestBody.put("access_token", accessToken);

        HttpHeaders resourceHeaders = new HttpHeaders();
        resourceHeaders.setBearerAuth(accessToken);

        HttpEntity<Map<String, String>> resourceRequestEntity = new HttpEntity<>(resourceRequestBody, resourceHeaders);

        ResponseEntity<Map> resourceResponseEntity = restTemplate.exchange(
                provider.getUserInfoUri(), HttpMethod.GET, resourceRequestEntity, Map.class);

        Map<String, Object> resourceBody = (Map<String, Object>) resourceResponseEntity.getBody();

        if (Objects.isNull(resourceBody)) {
            throw new RuntimeException();
        }
        return resourceBody;
    }
}
