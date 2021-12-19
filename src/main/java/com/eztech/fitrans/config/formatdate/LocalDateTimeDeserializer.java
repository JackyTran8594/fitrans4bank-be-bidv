package com.eztech.fitrans.config.formatdate;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Thực hiện parse data json request to localDateTime
 */
@Slf4j
public class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static final String YYYY_MM_DD_T_HH_MM_SS = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String YYYY_MM_DD_FULL = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    @Override
    public LocalDateTime deserialize(JsonParser jsonParser,
                                     DeserializationContext deserializationContext) throws IOException {
        String str = jsonParser.getText();
        try {
            return convertStringToLocalDateTime(str, YYYY_MM_DD_T_HH_MM_SS);
        } catch (DateTimeParseException e) {
            log.error(e.getMessage(), e);
            return deserialize(str);
        }
    }

    private LocalDateTime deserialize(String str) {
        try {
            return convertStringToLocalDateTime(str, YYYY_MM_DD_HH_MM_SS);
        } catch (DateTimeParseException e) {
            log.error(e.getMessage(), e);
            return deserialize2(str);
        }
    }

    private LocalDateTime deserialize2(String str) {
        try {
            return convertStringToLocalDateTime(str, YYYY_MM_DD_FULL);
        } catch (DateTimeParseException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public static LocalDateTime convertStringToLocalDateTime(String value, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        if (value == null || value.trim().isEmpty()) {
            return null;
        } else if (value.contains(".")) {
            value = value.substring(0, value.indexOf('.'));
        }
        LocalDateTime formatDateTime = LocalDateTime.parse(value, formatter);
        return formatDateTime;
    }
}
