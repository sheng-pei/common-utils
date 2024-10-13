package ppl.common.utils.json.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;

public class ZonedDateTimeDeserializer extends StdDeserializer<ZonedDateTime> {
    private static final DateTimeFormatter DATE_TIME_FORMATTER;

    static {
        DateTimeFormatterBuilder formatterBuilder = new DateTimeFormatterBuilder();
        formatterBuilder.appendPattern("yyyy-MM-dd'T'HH:mm:ss");
        formatterBuilder.appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true);
        formatterBuilder.appendZoneId();
        DATE_TIME_FORMATTER = formatterBuilder.toFormatter();
    }

    public ZonedDateTimeDeserializer() {
        super(ZonedDateTime.class);
    }

    @Override
    public ZonedDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (p.hasToken(JsonToken.VALUE_STRING)) {
            String str = p.getText().trim();
            if (str.isEmpty()) {
                return null;
            }

            try {
                return ZonedDateTime.parse(str, DATE_TIME_FORMATTER);
            } catch (DateTimeParseException e) {
                throw new JsonMappingException(p, String.format("Failed to deserialize %s: (%s) %s",
                        handledType().getName(), e.getClass().getName(), e.getMessage()), e);
            }
        }
        throw new JsonMappingException(p, String.format("Unexpected token (%s), expected %s for %s value",
                p.getCurrentToken(), JsonToken.VALUE_STRING, handledType().getName()));
    }

}
