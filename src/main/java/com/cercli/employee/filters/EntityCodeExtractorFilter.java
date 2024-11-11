package com.cercli.employee.filters;

import com.cercli.employee.config.InternationalizationProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class EntityCodeExtractorFilter extends OncePerRequestFilter {

    private static final String ENTITY_KEY = "X_ENTITY_CODE";
    private static final int SCOPE_ID = 0;

    private final InternationalizationProperties intlProperties;

    private String getParamFromQueryString(String queryString, String paramName) {
        System.out.println("Entered Param " + queryString + paramName);
        if (queryString == null || queryString.isEmpty()) {
            return null;
        }
        Map<String, String> queryParams = new HashMap<>();
        String[] pairs = queryString.split("&");
        try {
            for (String pair : pairs) {
                int idx = pair.indexOf("=");
                queryParams.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
            }
            return queryParams.get(paramName);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException, ServletException {
        String countryCode = request.getHeader("X-Entity");
        if (countryCode == null || countryCode.isEmpty()) {
            countryCode = getParamFromQueryString(request.getQueryString(), "entity");
            countryCode = countryCode == null || countryCode.isEmpty() ? intlProperties.getDefaultCountryCode() : countryCode;
        }

        if (!intlProperties.getSupportedCountryCodes().contains(countryCode.toUpperCase())) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid country code.");
            return;
        }

        RequestContextHolder.currentRequestAttributes().setAttribute(ENTITY_KEY, countryCode, RequestAttributes.SCOPE_REQUEST);
        filterChain.doFilter(request, response);
    }
}
