package ppl.common.utils.json.jackson;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

public class LocalDateTimeDeserializer extends StdDeserializer<LocalDateTime> implements ContextualDeserializer {
    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final DateTimeFormatter formatter;

    public LocalDateTimeDeserializer() {
        this(null);
    }

    public LocalDateTimeDeserializer(DateTimeFormatter formatter) {
        super(LocalDateTime.class);
        this.formatter = formatter == null ? DEFAULT_FORMATTER : formatter;
    }

    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (p.hasToken(JsonToken.VALUE_STRING)) {
            String str = p.getText().trim();
            if (str.isEmpty()) {
                return null;
            }

            try {
                return LocalDateTime.parse(str, formatter);
            } catch (DateTimeParseException e) {
                throw new JsonMappingException(p, String.format("Failed to deserialize %s: (%s) %s",
                        handledType().getName(), e.getClass().getName(), e.getMessage()), e);
            }
        }
        throw new JsonMappingException(p, String.format("Unexpected token (%s), expected %s for %s value",
                p.getCurrentToken(), JsonToken.VALUE_STRING, handledType().getName()));
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
        final JsonFormat.Value format = findFormatOverrides(ctxt, property,
                handledType());

        if (format != null && format.hasPattern()) {
            final String pattern = format.getPattern();
            final Locale loc = format.hasLocale() ? format.getLocale() : ctxt.getLocale();
            DateTimeFormatter df = DateTimeFormatter.ofPattern(pattern, loc);
            return new LocalDateTimeDeserializer(df);
        }
        return this;
    }
}
