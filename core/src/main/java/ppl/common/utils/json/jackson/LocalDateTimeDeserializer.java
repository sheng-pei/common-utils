package ppl.common.utils.json.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
    private static final String DEFAULT_FORMAT_STRING = "yyyy-MM-dd HH:mm:ss";

    private final DateTimeFormatter formatter;

    public LocalDateTimeDeserializer() {
        this(DEFAULT_FORMAT_STRING);
    }

    public LocalDateTimeDeserializer(String format) {
        this.formatter = DateTimeFormatter.ofPattern(format);
    }

    @Override
    public LocalDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        String text = jsonParser.getText();
        if (text == null || text.isEmpty()) {
            return null;
        }

        return LocalDateTime.parse(text, formatter);
    }

    @Override
    public Class<?> handledType() {
        return LocalDateTime.class;
    }
}
