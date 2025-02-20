package nextstep.security.authentication;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import nextstep.security.authentication.OAuth2ClientProperties.Provider;
import nextstep.security.authentication.OAuth2ClientProperties.Registration;
import org.springframework.http.HttpMethod;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.util.UriComponentsBuilder;

public class OAuth2LoginRedirectFilter extends GenericFilterBean {

  private static final String REQUEST_URI = "/oauth2/authorization";

  private final Map<String, Registration> registrations;
  private final Map<String, Provider> providers;

  public OAuth2LoginRedirectFilter(OAuth2ClientProperties properties) {
    this.registrations = properties.getRegistrations();
    this.providers = properties.getProviders();
  }

  @Override
  public void doFilter(ServletRequest request,
      ServletResponse response,
      FilterChain chain) throws IOException, ServletException {
    HttpServletRequest httpServletRequest = (HttpServletRequest) request;
    HttpServletResponse httpServletResponse = (HttpServletResponse) response;

    String requestURI = httpServletRequest.getRequestURI();
    String method = httpServletRequest.getMethod();

    String registrationSource = null;
    if (requestURI.startsWith(REQUEST_URI)) {
      registrationSource = getRegistrationId(requestURI);
    }

    if (registrationSource != null && method.equals(HttpMethod.GET.name())) {
      Registration registration = registrations.get(registrationSource);
      Provider provider = providers.get(registrationSource);

      String queryString = UriComponentsBuilder.newInstance()
          .queryParam("client_id", registration.clientId())
          .queryParam("scope", registration.scope())
          .queryParam("response_type", "code")
          .queryParam("redirect_uri", registration.redirectUri())
          .queryParam("authorization_grant_type", registration.authorizationGrantType())
          .build()
          .toUri()
          .getQuery();

      httpServletResponse.sendRedirect(provider.authorizationUri() + queryString);
      return;
    }

    chain.doFilter(request, response);
  }

  private String getRegistrationId(String requestURI) {
    if (requestURI.contains("github")) {
      return "github";
    }
    if (requestURI.contains("google")) {
      return "google";
    }
    return null;
  }
}