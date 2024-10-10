package ppl.common.utils.json.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;

public class DateDeserializer extends JsonDeserializer<Date> {

    private static final DateTimeFormatter DEFAULT_FORMAT_STRING;

    static {
        DateTimeFormatterBuilder formatterBuilder = new DateTimeFormatterBuilder();
        formatterBuilder.appendPattern("yyyy");
        formatterBuilder.optionalStart();
        formatterBuilder.appendPattern("-MM");
        formatterBuilder.optionalStart();
        formatterBuilder.appendPattern("-dd");
        formatterBuilder.optionalStart();
        formatterBuilder.appendPattern(" HH");
        formatterBuilder.optionalStart();
        formatterBuilder.appendPattern(":mm");
        formatterBuilder.optionalStart();
        formatterBuilder.appendPattern(":ss");
        formatterBuilder.appendFraction(ChronoField.MILLI_OF_SECOND, 0, 3, true);
        formatterBuilder.optionalEnd();
        formatterBuilder.optionalEnd();
        formatterBuilder.optionalEnd();
        formatterBuilder.optionalEnd();
        formatterBuilder.optionalEnd();
        DEFAULT_FORMAT_STRING = formatterBuilder.toFormatter();
    }

    private final DateTimeFormatter formatter;

    public DateDeserializer() {
        this.formatter = DEFAULT_FORMAT_STRING;
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

        TemporalAccessor accessor = formatter.parse(text);
        Calendar.Builder builder = new Calendar.Builder();
        builder.setDate(accessor.get(ChronoField.YEAR),
                accessor.isSupported(ChronoField.MONTH_OF_YEAR) ? accessor.get(ChronoField.MONTH_OF_YEAR) - 1 : 0,
                accessor.isSupported(ChronoField.DAY_OF_MONTH) ? accessor.get(ChronoField.DAY_OF_MONTH) : 1);
        builder.setTimeOfDay(accessor.isSupported(ChronoField.HOUR_OF_DAY) ? accessor.get(ChronoField.HOUR_OF_DAY) : 0,
                accessor.isSupported(ChronoField.MINUTE_OF_HOUR) ? accessor.get(ChronoField.MINUTE_OF_HOUR) : 0,
                accessor.isSupported(ChronoField.SECOND_OF_MINUTE) ? accessor.get(ChronoField.SECOND_OF_MINUTE) : 0,
                accessor.isSupported(ChronoField.MILLI_OF_SECOND) ? accessor.get(ChronoField.MILLI_OF_SECOND) : 0);
        return builder.build().getTime();
    }

    @Override
    public Class<?> handledType() {
        return Date.class;
    }
}
