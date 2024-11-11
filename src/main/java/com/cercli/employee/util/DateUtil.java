package com.cercli.employee.util;

import com.cercli.employee.config.TimezoneConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
@RequiredArgsConstructor
public class DateUtil {

    private final TimezoneConfig timezoneConfig;

    // Convert server time to local time based on the country code
    public LocalDateTime toLocalTime(LocalDateTime serverDateTime, String countryCode) {
        ZoneId serverZoneId = ZoneId.of(timezoneConfig.getServerTimezone());
        ZoneId localZoneId = ZoneId.of(timezoneConfig.getTimezoneForCountry(countryCode));
        return serverDateTime.atZone(serverZoneId).withZoneSameInstant(localZoneId).toLocalDateTime();
    }

    public LocalDateTime toServerTime(LocalDateTime localDateTime) {
        ZoneId localZoneId = ZoneId.systemDefault();
        ZoneId serverZoneId = ZoneId.of(timezoneConfig.getServerTimezone());
        return localDateTime.atZone(localZoneId)
                .withZoneSameInstant(serverZoneId)
                .toLocalDateTime();
    }
}
