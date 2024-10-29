package ppl.common.utils.json.jackson;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.text.DateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 'yyyy' and 'yyyy-MM' are also compatible on default.
 */
public class DateDeserializer extends StdDeserializer<Date> implements ContextualDeserializer {

    private static final DateTimeFormatter DEFAULT_FORMATTER;

    static {
        DateTimeFormatterBuilder formatterBuilder = new DateTimeFormatterBuilder();
        formatterBuilder.appendPattern("yyyy");
        formatterBuilder.optionalStart();
        formatterBuilder.appendLiteral('-');
        formatterBuilder.appendPattern("MM");
        formatterBuilder.optionalStart();
        formatterBuilder.appendLiteral('-');
        formatterBuilder.appendPattern("dd");
        formatterBuilder.optionalStart();
        formatterBuilder.appendLiteral(' ');
        formatterBuilder.appendPattern("HH");
        formatterBuilder.optionalStart();
        formatterBuilder.appendLiteral(':');
        formatterBuilder.appendPattern("mm");
        formatterBuilder.optionalStart();
        formatterBuilder.appendLiteral(':');
        formatterBuilder.appendPattern("ss");
        formatterBuilder.appendFraction(ChronoField.MILLI_OF_SECOND, 0, 3, true);
        formatterBuilder.optionalEnd();
        formatterBuilder.optionalEnd();
        formatterBuilder.optionalEnd();
        formatterBuilder.optionalEnd();
        formatterBuilder.optionalEnd();
        DEFAULT_FORMATTER = formatterBuilder.toFormatter();
    }

    private final DateTimeFormatter formatter;
    private String _formatString;

    public DateDeserializer() {
        super(Date.class);
        this.formatter = null;
    }

    public DateDeserializer(DateTimeFormatter formatter, String formatString) {
        super(Date.class);
        this._formatString = formatString;
        this.formatter = formatter;
    }

    @Override
    public Date deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (p.hasToken(JsonToken.VALUE_STRING)) {
            String str = p.getText().trim();
            if (str.isEmpty()) {
                return null;
            }

            DateTimeFormatter formatter = this.formatter;
            formatter = formatter == null ? DEFAULT_FORMATTER : formatter;
            try {
                TemporalAccessor accessor = formatter.parse(str);
                ZonedDateTime zonedDateTime;
                if (accessor.isSupported(ChronoField.OFFSET_SECONDS)) {
                    zonedDateTime = ZonedDateTime.from(accessor);
                } else {
                    LocalDateTime localDateTime = LocalDateTime.of(
                            accessor.get(ChronoField.YEAR),
                            accessor.isSupported(ChronoField.MONTH_OF_YEAR) ? accessor.get(ChronoField.MONTH_OF_YEAR) : 1,
                            accessor.isSupported(ChronoField.DAY_OF_MONTH) ? accessor.get(ChronoField.DAY_OF_MONTH) : 1,
                            accessor.isSupported(ChronoField.HOUR_OF_DAY) ? accessor.get(ChronoField.HOUR_OF_DAY) : 0,
                            accessor.isSupported(ChronoField.MINUTE_OF_HOUR) ? accessor.get(ChronoField.MINUTE_OF_HOUR) : 0,
                            accessor.isSupported(ChronoField.SECOND_OF_MINUTE) ? accessor.get(ChronoField.SECOND_OF_MINUTE) : 0,
                            accessor.isSupported(ChronoField.NANO_OF_SECOND) ? accessor.get(ChronoField.NANO_OF_SECOND) : 0);
                    zonedDateTime = ZonedDateTime.of(localDateTime, formatter.getZone() == null ? ZoneId.systemDefault() : formatter.getZone());
                }

                return Date.from(zonedDateTime.toInstant());
            } catch (DateTimeParseException e) {
                return (java.util.Date) ctxt.handleWeirdStringValue(handledType(), str,
                        "expected format \"%s\"", _formatString);
            }
        }
        return super._parseDate(p, ctxt);
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
        final JsonFormat.Value format = findFormatOverrides(ctxt, property,
                handledType());

        if (format != null && !JsonFormat.Value.empty().equals(format)) {
            TimeZone tz = format.getTimeZone();
            final Boolean lenient = format.getLenient();

            // First: fully custom pattern?
            if (format.hasPattern()) {
                final String pattern = format.getPattern();
                final Locale loc = format.hasLocale() ? format.getLocale() : ctxt.getLocale();
                DateTimeFormatter df = DateTimeFormatter.ofPattern(pattern, loc);
                if (tz == null) {
                    tz = ctxt.getTimeZone();
                }
                df = df.withZone(tz.toZoneId());
                if (lenient != null) {
                    df = df.withResolverStyle(lenient ? ResolverStyle.LENIENT : ResolverStyle.STRICT);
                }
                return new DateDeserializer(df, pattern);
            }

            DateFormat df0 = ctxt.getConfig().getDateFormat();
            if (df0 != null) {
                ctxt.reportBadDefinition(handledType(), String.format(
                        "Configured `DateFormat` (%s) is not supported; cannot get `Locale` from.",
                        df0.getClass().getName()));
            }
        }
        return this;
    }
}
