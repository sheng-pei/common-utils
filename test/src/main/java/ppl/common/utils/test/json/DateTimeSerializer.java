package ppl.common.utils.test.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateTimeSerializer extends JsonSerializer<Date> {

    @Override
    public void serialize(Date value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX");
        if (value == null) {
            gen.writeNull();
        } else {
            gen.writeString(ZonedDateTime.ofInstant(value.toInstant(), ZoneId.systemDefault()).format(format));
        }
    }

    @Override
    public Class<Date> handledType() {
        return Date.class;
    }
}
