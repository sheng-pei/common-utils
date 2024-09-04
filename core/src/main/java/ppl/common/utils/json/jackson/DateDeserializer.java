package ppl.common.utils.json.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateDeserializer extends JsonDeserializer<Date> {

    private static final String DEFAULT_FORMAT_STRING = "yyyy-MM-dd HH:mm:ss";

    private final DateTimeFormatter formatter;

    public DateDeserializer() {
        this(DEFAULT_FORMAT_STRING);
    }

    public DateDeserializer(String format) {
        this.formatter = DateTimeFormatter.ofPattern(format);
    }

    @Override
    public Date deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        String text = jsonParser.getText();
        if (text == null || text.isEmpty()) {
            return null;
        }

        LocalDateTime localDateTime = LocalDateTime.parse(text, formatter);
        ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, ZoneId.systemDefault());
        return Date.from(zonedDateTime.toInstant());
    }

    @Override
    public Class<?> handledType() {
        return Date.class;
    }
}
