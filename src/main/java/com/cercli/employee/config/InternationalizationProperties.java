package com.cercli.employee.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "app.internationalization")
@Getter
@Setter
public class InternationalizationProperties {

    private String defaultCountryCode;
    private List<String> supportedCountryCodes;
    private String defaultLocale;

}
