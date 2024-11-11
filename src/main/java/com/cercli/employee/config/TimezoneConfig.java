package com.cercli.employee.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
@ConfigurationProperties(prefix = "app")
@Getter
@Setter
public class TimezoneConfig {

    private ServerConfig server = new ServerConfig("UTC"); // Default to "UTC" if not set
    private Map<String, String> timezones;

    // Using Java record for a more concise representation of 'server' properties
    public record ServerConfig(String timezone) {}

    // Method to get server timezone
    public String getServerTimezone() {
        return Optional.ofNullable(server)
                .map(ServerConfig::timezone)
                .orElse("UTC");
    }

    // Method to get timezone for a specific country code
    public String getTimezoneForCountry(String countryCode) {
        return Optional.ofNullable(timezones)
                .map(map -> map.getOrDefault(countryCode, getServerTimezone()))
                .orElse(getServerTimezone());
    }
}
