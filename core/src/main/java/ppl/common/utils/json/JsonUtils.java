package ppl.common.utils.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleDeserializers;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.module.SimpleSerializers;
import ppl.common.utils.IOUtils;
import ppl.common.utils.enumerate.EnumUtils;
import ppl.common.utils.enumerate.jackson.EnumDeserializers;
import ppl.common.utils.enumerate.jackson.EnumSerializers;

import java.io.*;
import java.util.Date;
import java.util.List;

public class JsonUtils {
    private static final ObjectMapper OBJECT_MAPPER;

    static {
        SimpleModule module = new SimpleModule();
        module.setSerializers(new EnumSerializers());
        module.setDeserializers(new EnumDeserializers());
        module.addDeserializer(Date.class, new DateTimeDeserializer());
        module.addSerializer(Date.class, new DateTimeSerializer());
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(module);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        OBJECT_MAPPER = mapper;
    }

    public static String writePretty(Object obj) {
        try {
            return OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (Throwable t) {
            throw new JsonException("Failed to write value as string.", t);
        }
    }

    public static String write(Object obj) {
        try {
            return OBJECT_MAPPER.writeValueAsString(obj);
        } catch (Throwable t) {
            throw new JsonException("Failed to write value as string.", t);
        }
    }

    public static byte[] writeAsBytes(Object obj) {
        try {
            return OBJECT_MAPPER.writeValueAsBytes(obj);
        } catch (Throwable t) {
            throw new JsonException("Failed to write value as bytes.", t);
        }
    }

    public static <T> T read(InputStream is, Class<T> clazz) {
        try {
            try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
                IOUtils.copy(is, os);
                return OBJECT_MAPPER.readValue(os.toByteArray(), clazz);
            }
        } catch (Throwable t) {
            throw new JsonException(String.format("%s could not be read from some String.",
                    clazz.getCanonicalName()), t);
        }
    }

    public static <T> T read(String str, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(str, clazz);
        } catch (Throwable t) {
            throw new JsonException(String.format("%s could not be read from some String.",
                    clazz.getCanonicalName()), t);
        }
    }

    public static <T> T read(byte[] bytes, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(bytes, clazz);
        } catch (Throwable t) {
            throw new JsonException(String.format("%s could not be read from some bytes.",
                    clazz.getCanonicalName()), t);
        }
    }

    public static <T> T read(byte[] bytes, int offset, int length, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(bytes, offset, length, clazz);
        } catch (Throwable t) {
            throw new JsonException(String.format("%s could not be read from some bytes.",
                    clazz.getCanonicalName()), t);
        }
    }

    public static <T> T read(String json, TypeReference<T> reference) {
        try {
            return OBJECT_MAPPER.readerFor(reference).readValue(json);
        } catch (IOException e) {
            throw new JsonException("Json parse error for " + reference.getType().getTypeName(), e);
        }
    }

    public static <T> List<T> readList(String json, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readerForListOf(clazz)
                    .readValue(json);
        } catch (Throwable t) {
            throw new JsonException(String.format("%s list could not be read from some String.",
                    clazz.getCanonicalName()), t);
        }
    }
}
