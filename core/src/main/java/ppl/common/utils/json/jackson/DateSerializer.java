package ppl.common.utils.json.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateSerializer extends JsonSerializer<Date> {

    private static final String DEFAULT_FORMAT_STRING = "yyyy-MM-dd HH:mm:ss";

    private final DateTimeFormatter formatter;

    public DateSerializer() {
        this(DEFAULT_FORMAT_STRING);
    }

    public DateSerializer(String format) {
        this.formatter = DateTimeFormatter.ofPattern(format);
    }

    @Override
    public void serialize(Date value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeNull();
        } else {
            gen.writeString(LocalDateTime.ofInstant(value.toInstant(), ZoneId.systemDefault()).format(formatter));
        }
    }

    @Override
    public Class<Date> handledType() {
        return Date.class;
    }
}
