package com.cercli.employee.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.LocaleResolver;

import java.util.Locale;
import java.util.Map;

@Component
public class EntityCodeLocaleResolver implements LocaleResolver {

    private static final String ENTITY_KEY = "X_ENTITY_CODE";

    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        String entityCode = (String) request.getAttribute(ENTITY_KEY);
        Locale locale = determineLocaleByCountryCode(entityCode);
        return locale;
    }

    @Override
    public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {

    }

    private static final Map<String, Locale> COUNTRY_CODE_TO_LOCALE = Map.of(
            "NG", Locale.forLanguageTag("en-NG"),
            "IN", Locale.forLanguageTag("en-IN"),
            "US", Locale.US,
            "UK", Locale.forLanguageTag("en-GB")
            // Add more mappings as needed
    );

    private Locale determineLocaleByCountryCode(String countryCode) {
        return COUNTRY_CODE_TO_LOCALE.getOrDefault(countryCode.toUpperCase(), Locale.getDefault());
    }
}