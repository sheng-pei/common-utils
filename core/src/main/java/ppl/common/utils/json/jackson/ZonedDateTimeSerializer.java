package ppl.common.utils.json.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ZonedDateTimeSerializer extends JsonSerializer<ZonedDateTime> {
    private static final String DEFAULT_FORMAT_STRING = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX";

    private final DateTimeFormatter formatter;

    public ZonedDateTimeSerializer() {
        this(DEFAULT_FORMAT_STRING);
    }

    public ZonedDateTimeSerializer(String format) {
        this.formatter = DateTimeFormatter.ofPattern(format);
    }

    @Override
    public void serialize(ZonedDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeNull();
        } else {
            gen.writeString(value.format(formatter));
        }
    }

    @Override
    public Class<ZonedDateTime> handledType() {
        return ZonedDateTime.class;
    }
}
