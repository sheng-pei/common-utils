package ppl.common.utils.json.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Date;

public class DateTimeDeserializer extends JsonDeserializer<Date> {

    private static final DateTimeFormatter DATE_TIME_FORMATTER;

    static {
        DateTimeFormatterBuilder formatterBuilder = new DateTimeFormatterBuilder();
        formatterBuilder.appendPattern("yyyy-MM-dd'T'HH:mm:ss");
        formatterBuilder.appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true);
        formatterBuilder.appendZoneId();
        DATE_TIME_FORMATTER = formatterBuilder.toFormatter();
    }

    @Override
    public Date deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        String text = jsonParser.getText();
        if (text == null || text.isEmpty()) {
            return null;
        }

        return Date.from(ZonedDateTime.parse(text, DATE_TIME_FORMATTER).toInstant());
    }

    @Override
    public Class<?> handledType() {
        return Date.class;
    }
}
