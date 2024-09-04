package ppl.common.utils.json.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {
    private static final String DEFAULT_FORMAT_STRING = "yyyy-MM-dd HH:mm:ss";

    private final DateTimeFormatter formatter;

    public LocalDateTimeSerializer() {
        this(DEFAULT_FORMAT_STRING);
    }

    public LocalDateTimeSerializer(String format) {
        this.formatter = DateTimeFormatter.ofPattern(format);
    }

    @Override
    public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeNull();
        } else {
            gen.writeString(value.format(formatter));
        }
    }

    @Override
    public Class<LocalDateTime> handledType() {
        return LocalDateTime.class;
    }
}
