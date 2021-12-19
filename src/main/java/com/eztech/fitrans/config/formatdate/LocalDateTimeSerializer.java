package com.eztech.fitrans.config.formatdate;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Thực hiện parse data response to string json
 */
@Slf4j
public class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {
    public static final String YYYY_MM_DD_T_HH_MM_SS = "yyyy-MM-dd'T'HH:mm:ss";

    @Override
    public void serialize(LocalDateTime localDateTime, JsonGenerator jsonGenerator,
                          SerializerProvider serializerProvider) throws IOException {
        try {
            String result = localDateTimeToString(localDateTime, YYYY_MM_DD_T_HH_MM_SS);
            jsonGenerator.writeString(result);
        } catch (DateTimeParseException e) {
            log.error(e.getMessage(), e);
            jsonGenerator.writeString("");
        }
    }

    public static Boolean isNull(Object obj) {
        return obj == null;
    }

    public static Boolean notNull(Object obj) {
        return !isNull(obj);
    }

    public static String localDateTimeToString(LocalDateTime value, String format) {
        if (!notNull(value)) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return value.format(formatter); // "1986-04-08 12:30"
    }
}
