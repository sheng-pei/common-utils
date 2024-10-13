package ppl.common.utils.json.jackson;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class LocalDateTimeSerializer extends StdSerializer<LocalDateTime> implements ContextualSerializer {
    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final DateTimeFormatter formatter;

    public LocalDateTimeSerializer() {
        this(null);
    }

    public LocalDateTimeSerializer(DateTimeFormatter formatter) {
        super(LocalDateTime.class);
        this.formatter = formatter == null ? DEFAULT_FORMATTER : formatter;
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
    public JsonSerializer<?> createContextual(SerializerProvider serializers, BeanProperty property) throws JsonMappingException {
        // Note! Should not skip if `property` null since that'd skip check
        // for config overrides, in case of root value
        JsonFormat.Value format = findFormatOverrides(serializers, property, handledType());
        if (format == null || !format.hasPattern()) {
            return this;
        }

        final Locale loc = format.hasLocale()
                ? format.getLocale()
                : serializers.getLocale();
        DateTimeFormatter f = DateTimeFormatter.ofPattern(format.getPattern(), loc);
        return new LocalDateTimeSerializer(f);
    }
}
