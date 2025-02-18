package nextstep.security.authentication;

import static nextstep.security.authentication.GithubLoginRedirectFilter.*;
import static nextstep.security.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

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
import nextstep.security.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

public class GithubAuthenticationFilter extends GenericFilterBean {

  private static final String REQUEST_URI = "/login/oauth2/code/github";
  private final GithubGetAccessTokenClient githubGetAccessTokenClient = new GithubGetAccessTokenClient();
  private final GithubGetUserInfoClient githubGetUserInfoClient = new GithubGetUserInfoClient();
  private final MemberRepository memberRepository;

  public GithubAuthenticationFilter(MemberRepository memberRepository) {
    this.memberRepository = memberRepository;
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest httpServletRequest = (HttpServletRequest) request;
    HttpServletResponse httpServletResponse = (HttpServletResponse) response;

    String requestURI = httpServletRequest.getRequestURI();
    String method = httpServletRequest.getMethod();

    if (requestURI.startsWith(REQUEST_URI) && method.equals(AUTHORIZATION_REQUEST_METHOD.name())) {
      String code = httpServletRequest.getParameter("code");
      String accessToken = githubGetAccessTokenClient.getAccessToken(code);
      Map<String, String> userInfo = githubGetUserInfoClient.getUserInfo(accessToken);
      String email = userInfo.get("email");

      Member member = memberRepository.findByEmail(email).orElse(
          new Member(userInfo.get("email"), "", userInfo.get("name"), userInfo.get("avatarl_url"),
              Set.of("USER")));

      UsernamePasswordAuthenticationToken authentication = UsernamePasswordAuthenticationToken.authenticated(
          member.getEmail(), null, member.getRoles());
      SecurityContextHolder.getContext().setAuthentication(authentication);
      httpServletRequest.getSession(true)
          .setAttribute(SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());
      httpServletResponse.sendRedirect("/");

      return;
    }

    chain.doFilter(request, response);
  }
}
