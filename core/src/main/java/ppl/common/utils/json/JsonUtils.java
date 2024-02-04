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

import java.io.*;
import java.util.List;

public class JsonUtils {
    private static final ObjectMapper OBJECT_MAPPER;

    static {
        SimpleModule module = new SimpleModule();
        module.setSerializers(new SimpleSerializers() {
            @Override
            public JsonSerializer<?> findSerializer(SerializationConfig config, JavaType type, BeanDescription beanDesc) {
                Class<?> raw = type.getRawClass();
                if (type.getRawClass().isEnum()) {
                    @SuppressWarnings("unchecked")
                    Class<? extends Enum<?>> eClass = (Class<? extends Enum<?>>) raw;
                    if (EnumUtils.isEncodeSupport(eClass)) {
                        return new JsonSerializer<Enum<? extends Enum<?>>>() {
                            @Override
                            public void serialize(Enum<? extends Enum<?>> value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                                Object obj = EnumUtils.encode(value);
                                if (obj instanceof String) {
                                    gen.writeString(obj.toString());
                                } else if (obj instanceof Character) {
                                    gen.writeString(obj.toString());
                                } else if (obj instanceof Number) {
                                    gen.writeNumber(obj.toString());
                                }
                            }
                        };
                    }
                }
                return super.findSerializer(config, type, beanDesc);
            }
        });
        module.setDeserializers(new SimpleDeserializers() {
            @Override
            public JsonDeserializer<?> findEnumDeserializer(Class<?> type, DeserializationConfig config, BeanDescription beanDesc) throws JsonMappingException {
                if (type.isEnum()) {
                    @SuppressWarnings("unchecked")
                    Class<? extends Enum<?>> eClass = (Class<? extends Enum<?>>) type;
                    if (EnumUtils.isEncodeSupport(eClass)) {
                        return new JsonDeserializer<Enum<? extends Enum<?>>>() {
                            @SuppressWarnings({"DuplicateThrows", "RedundantThrows"})
                            @Override
                            public Enum<? extends Enum<?>> deserialize(JsonParser p, DeserializationContext context) throws IOException, JacksonException {
                                Class<?> keyClass = EnumUtils.getKeyType(eClass);
                                return EnumUtils.enumOf(eClass, p.readValueAs(keyClass));
                            }
                        };
                    }
                }
                return super.findEnumDeserializer(type, config, beanDesc);
            }
        });
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
