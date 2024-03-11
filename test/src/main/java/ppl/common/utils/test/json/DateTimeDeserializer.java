package ppl.common.utils.test.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Date;

public class DateTimeDeserializer extends JsonDeserializer<Date> {

    @Override
    public Date deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        String text = jsonParser.getText();
        if (text == null || text.isEmpty()) {
            return null;
        }

        DateTimeFormatterBuilder formatterBuilder = new DateTimeFormatterBuilder();
        formatterBuilder.appendPattern("yyyy-MM-dd'T'HH:mm:ss");
        formatterBuilder.appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true);
        formatterBuilder.appendZoneId();
        return Date.from(ZonedDateTime.parse(text, formatterBuilder.toFormatter()).toInstant());
    }

    @Override
    public Class<Date> handledType() {
        return Date.class;
    }

}
