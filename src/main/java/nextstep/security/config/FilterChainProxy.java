package nextstep.security.config;


import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class FilterChainProxy extends GenericFilterBean {

    private final List<SecurityFilterChain> filterChains;

    public FilterChainProxy(List<SecurityFilterChain> filterChains) {
        this.filterChains = filterChains;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        List<Filter> filters = getFilters((HttpServletRequest) request);

        VirtualFilterChain virtualFilterChain = new VirtualFilterChain(chain, filters);
        virtualFilterChain.doFilter(request, response);
    }

    private List<Filter> getFilters(HttpServletRequest request) {
        for (SecurityFilterChain filterChain : filterChains) {
            if (filterChain.matches(request)) {
                return filterChain.getFilters();
            }
        }

        return Collections.emptyList();
    }

    private static final class VirtualFilterChain implements FilterChain {

        private final FilterChain originalChain;

        private final List<Filter> additionalFilters;

        private final int size;

        private int currentPosition = 0;

        public VirtualFilterChain(FilterChain originalChain, List<Filter> additionalFilters) {
            this.originalChain = originalChain;
            this.additionalFilters = additionalFilters;
            this.size = additionalFilters.size();
        }

        @Override
        public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
            if (currentPosition == size) {
                originalChain.doFilter(request, response);
            } else {
                currentPosition++;
                Filter nextFilter = additionalFilters.get(currentPosition - 1);
                nextFilter.doFilter(request, response, this);
            }
        }
    }
}
