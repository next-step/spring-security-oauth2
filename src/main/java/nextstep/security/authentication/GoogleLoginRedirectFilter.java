package nextstep.security.authentication;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpMethod;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.util.UriComponentsBuilder;

public class GoogleLoginRedirectFilter extends GenericFilterBean {
  private static final String REQUEST_URI = "/oauth2/authorization/google";
  public static final HttpMethod AUTHORIZATION_REQUEST_METHOD = HttpMethod.GET;
  public static final String GOOGLE_AUTHORIZATION_URI = "https://account.google.com/o/oauth2/v2/auth?";
  public static final String REDIRECT_URI = "http://localhost:8080/login/oauth2/code/google";

  @Override
  public void doFilter(ServletRequest request, ServletResponse response,
      FilterChain chain) throws IOException, ServletException {
    HttpServletRequest httpServletRequest = (HttpServletRequest) request;
    HttpServletResponse httpServletResponse = (HttpServletResponse) response;

    String requestURI = httpServletRequest.getRequestURI();
    String method = httpServletRequest.getMethod();

    if (requestURI.equals(REQUEST_URI) && method.equals(AUTHORIZATION_REQUEST_METHOD.name())) {
      String queryString = UriComponentsBuilder.newInstance()
          .queryParam("scope", "email%20profile")
          .queryParam("response_type", "code")
          .queryParam("redirect_uri", REDIRECT_URI)
          .queryParam("client_id", "349246449409-h7hgk8kms3k8d7tgal8nesbh24h34t0d.apps.googleusercontent.com")
          .build()
          .toUri()
          .getQuery();

      httpServletResponse.sendRedirect(GOOGLE_AUTHORIZATION_URI + queryString);
      return;
    }

    chain.doFilter(request, response);
  }
}
