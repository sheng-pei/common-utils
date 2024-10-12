package ppl.common.utils.json.jackson;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.text.DateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Serialize with yyyy-MM-dd HH:mm:ss on default.
 */
public class DateSerializer extends StdSerializer<Date> implements ContextualSerializer {

    private static final DateTimeFormatter DEFAULT_FORMAT_STRING = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final Boolean _useTimestamp;
    private final DateTimeFormatter formatter;

    public DateSerializer() {
        super(Date.class);
        this._useTimestamp = false;
        this.formatter = null;
    }

    public DateSerializer(Boolean useTimestamp, DateTimeFormatter formatter) {
        super(Date.class);
        this._useTimestamp = useTimestamp;
        this.formatter = formatter;
    }

    @Override
    public void serialize(Date value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeNull();
        } else {
            if (_asTimestamp(serializers)) {
                gen.writeNumber(value.getTime());
            } else {
                gen.writeString(ZonedDateTime.ofInstant(value.toInstant(), ZoneId.systemDefault())
                        .format(formatter == null ? DEFAULT_FORMAT_STRING : formatter));
            }
        }
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider serializers, BeanProperty property) throws JsonMappingException {
        // Note! Should not skip if `property` null since that'd skip check
        // for config overrides, in case of root value
        JsonFormat.Value format = findFormatOverrides(serializers, property, handledType());
        if (format == null) {
            return this;
        }

        // Simple case first: serialize as numeric timestamp?
        JsonFormat.Shape shape = format.getShape();
        if (shape.isNumeric()) {
            return new DateSerializer(Boolean.TRUE, null);
        }

        // 08-Jun-2017, tatu: With [databind#1648], this gets bit tricky..
        // First: custom pattern will override things
        if (format.hasPattern()) {
            final Locale loc = format.hasLocale()
                    ? format.getLocale()
                    : serializers.getLocale();
            DateTimeFormatter f = DateTimeFormatter.ofPattern(format.getPattern(), loc);
            TimeZone tz = format.hasTimeZone() ? format.getTimeZone()
                    : serializers.getTimeZone();
            f = f.withZone(tz.toZoneId());
            return new DateSerializer(Boolean.FALSE, f);
        }

        // Otherwise, need one of these changes:
        final boolean hasLocale = format.hasLocale();
        final boolean hasTZ = format.hasTimeZone();
        final boolean asString = (shape == JsonFormat.Shape.STRING);

        if (!hasLocale && !hasTZ && !asString) {
            return this;
        }

        DateFormat df0 = serializers.getConfig().getDateFormat();
        if (df0 != null) {
            serializers.reportBadDefinition(handledType(), String.format(
                    "Configured `DateFormat` (%s) is not supported; cannot get `Locale` from.",
                    df0.getClass().getName()));
        }
        return this;
    }

    protected boolean _asTimestamp(SerializerProvider serializers) {
        if (_useTimestamp != null) {
            return _useTimestamp;
        }
        if (formatter == null) {
            if (serializers != null) {
                return serializers.isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            }
            // 12-Jun-2014, tatu: Is it legal not to have provider? Was NPE:ing earlier so leave a check
            throw new IllegalArgumentException("Null SerializerProvider passed for " + handledType().getName());
        }
        return false;
    }
}
