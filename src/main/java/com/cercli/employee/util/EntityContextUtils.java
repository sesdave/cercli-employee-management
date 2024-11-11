package com.cercli.employee.util;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
@Component
@RequestScope
public class EntityContextUtils {

    private static final String ENTITY_KEY = "X_ENTITY_CODE";

    public String getCountryCode() {
        return (String) RequestContextHolder.currentRequestAttributes().getAttribute(ENTITY_KEY, RequestAttributes.SCOPE_REQUEST);
    }
}
