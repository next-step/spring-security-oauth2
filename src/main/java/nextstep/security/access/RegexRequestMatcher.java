package nextstep.security.access;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpMethod;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexRequestMatcher implements RequestMatcher {

    private HttpMethod method;
    private final Pattern pattern;

    public RegexRequestMatcher(HttpMethod method, String pattern) {
        this.method = method;
        this.pattern = Pattern.compile(pattern);
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        if (this.method != null && !this.method.name().equals(request.getMethod())) {
            return false;
        }

        Matcher matcher = pattern.matcher(request.getRequestURI());
        return matcher.matches();
    }

}
