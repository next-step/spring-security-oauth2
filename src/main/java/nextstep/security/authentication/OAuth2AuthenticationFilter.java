package nextstep.security.authentication;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import nextstep.app.domain.Member;
import nextstep.app.domain.MemberRepository;
import nextstep.security.authentication.OAuth2ClientProperties.Provider;
import nextstep.security.authentication.OAuth2ClientProperties.Registration;
import nextstep.security.context.HttpSessionSecurityContextRepository;
import nextstep.security.context.SecurityContextHolder;
import org.springframework.http.HttpMethod;
import org.springframework.web.filter.GenericFilterBean;

public class OAuth2AuthenticationFilter extends GenericFilterBean {

  private static final String REQUEST_URI = "/login/oauth2/code";

    private final OAuth2ClientProperties oAuth2ClientProperties;
    private final OAuth2AccessTokenClient oAuth2AccessTokenClient;
    private final MemberRepository memberRepository;
    private final OAuth2UserInfoClient oAuth2UserInfoClient;

  public OAuth2AuthenticationFilter(OAuth2ClientProperties oAuth2ClientProperties,
                                    MemberRepository memberRepository,
                                    OAuth2AccessTokenClient oAuth2AccessTokenClient,
                                    OAuth2UserInfoClient oAuth2UserInfoClient) {
    this.oAuth2AccessTokenClient = oAuth2AccessTokenClient;
    this.oAuth2UserInfoClient = oAuth2UserInfoClient;
    this.oAuth2ClientProperties = oAuth2ClientProperties;
    this.memberRepository = memberRepository;
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response,
      FilterChain filterChain) throws IOException, ServletException {
    HttpServletRequest httpRequest = (HttpServletRequest) request;
    HttpServletResponse httpResponse = (HttpServletResponse) response;

    String requestURI = httpRequest.getRequestURI();
    String method = httpRequest.getMethod();

    String registrationSource = null;
    if (requestURI.startsWith(REQUEST_URI)) {
      registrationSource = getRegistrationId(requestURI);
    }

    if (registrationSource != null && method.equals(HttpMethod.GET.name())) {
      Registration registration = oAuth2ClientProperties.getRegistrations().get(registrationSource);
      Provider provider = oAuth2ClientProperties.getProviders().get(registrationSource);

      String code = httpRequest.getParameter("code");

      String accessToken = oAuth2AccessTokenClient.getAccessToken(code, registration, provider);

      Map<String, String> userInfo = oAuth2UserInfoClient.getUserInfo(accessToken, provider);

      String email = userInfo.get("email");
      Member member = memberRepository.findByEmail(email)
          .orElse(new Member(
              email, "", userInfo.get("name"), userInfo.get("avatar_url"), Set.of("USER")
          ));

      UsernamePasswordAuthenticationToken authentication =
          UsernamePasswordAuthenticationToken.authenticated(
              member.getEmail(), null, member.getRoles());

      SecurityContextHolder.getContext().setAuthentication(authentication);

      httpRequest.getSession(true).setAttribute(
          HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
          SecurityContextHolder.getContext()
      );

      httpResponse.sendRedirect("/");
      return;
    }

    filterChain.doFilter(request, response);
  }

  private String getRegistrationId(String requestURI) {
    for (String registrationId : oAuth2ClientProperties.getRegistrations().keySet()) {
      if (requestURI.contains(registrationId)) {
        return registrationId;
      }
    }
    return null;
  }
}
