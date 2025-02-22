package nextstep.security.access;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.PathContainer;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

public class PathPatternRequestMatcher implements RequestMatcher {
    private final HttpMethod method;
    private final PathPattern pattern;

    public PathPatternRequestMatcher(HttpMethod method, String pattern) {
        this.method = method;
        this.pattern = new PathPatternParser().parse(pattern);
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        if (!method.matches(request.getMethod())) {
            return false;
        }

        return pattern.matches(PathContainer.parsePath(request.getRequestURI()));
    }

    public String getPathVariable(HttpServletRequest request, String variableName) {
        PathContainer path = PathContainer.parsePath(request.getRequestURI());
        PathPattern.PathMatchInfo matchInfo = pattern.matchAndExtract(path);

        if (matchInfo == null) {
            return null;
        }

        return matchInfo.getUriVariables().get(variableName);
    }
}
