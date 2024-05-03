package ppl.common.utils.test.json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ObjectNode;
import ppl.common.utils.json.JsonException;
import ppl.common.utils.test.JsonData;
import ppl.common.utils.test.point.Point;
import ppl.common.utils.test.point.Points;
import ppl.common.utils.test.point.specific.TimePoint;
import ppl.common.utils.test.point.specific.TimeSeqPoint;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

public class JsonUtil {
    private static final JsonMapper MAPPER;

    static {
        SimpleModule module = new SimpleModule();
        module.addSerializer(Date.class, new DateTimeSerializer());
        module.addDeserializer(Date.class, new DateTimeDeserializer());
        module.addSerializer(JsonData.class, new JsonDataSerializer());
        module.addDeserializer(JsonData.class, new JsonDataDeserializer());
        module.addSerializer(Point.class, new PointSerializer());
        module.addDeserializer(Point.class, new PointDeserializer());
        JsonMapper mapper = new JsonMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.registerModule(module);
        MAPPER = mapper;
    }

    public static JsonNode parseNode(Map<String, Object> properties) {
        ObjectNode ret = MAPPER.createObjectNode();
        for (Map.Entry<String, Object> e : properties.entrySet()) {
            ret.putPOJO(e.getKey(), e.getValue());
        }
        return ret;
    }

    public static JsonNode parseNode(String json) {
        try {
            return MAPPER.readTree(json);
        } catch (IOException e) {
            throw new JsonException("Json parse error", e);
        }
    }

    public static <T> T parseObject(String json, TypeReference<T> reference) {
        try {
            return MAPPER.readerFor(reference).readValue(json);
        } catch (IOException e) {
            throw new JsonException("Json parse error for " + reference.getType().getTypeName(), e);
        }
    }

    public static <T> T parseObject(String json, Class<T> clazz) {
        try {
            return MAPPER.readerFor(clazz).readValue(json);
        } catch (IOException e) {
            throw new JsonException("Json parse error for " + clazz.getCanonicalName(), e);
        }
    }

    public static <T> T parseObject(byte[] json, TypeReference<T> reference) {
        try {
            return MAPPER.readerFor(reference).readValue(json);
        } catch (IOException e) {
            throw new JsonException("Json parse error for " + reference.getType().getTypeName(), e);
        }
    }

    public static <T> T parseObject(byte[] json, Class<T> clazz) {
        try {
            return MAPPER.readerFor(clazz).readValue(json);
        } catch (IOException e) {
            throw new JsonException("Json parse error for " + clazz.getCanonicalName(), e);
        }
    }

    public static <T> T parseObject(JsonNode json, TypeReference<T> reference) {
        try {
            return MAPPER.readerFor(reference).readValue(json);
        } catch (IOException e) {
            throw new JsonException("Json parse error for " + reference.getType().getTypeName(), e);
        }
    }

    public static <T> T parseObject(JsonNode json, Class<T> clazz) {
        try {
            return MAPPER.readerFor(clazz).readValue(json);
        } catch (IOException e) {
            throw new JsonException("Json parse error for " + clazz.getCanonicalName(), e);
        }
    }

    public static String toString(Object obj) {
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (IOException e) {
            throw new JsonException(e.getMessage(), e);
        }
    }

    public static byte[] toBytes(Object obj) {
        try {
            return MAPPER.writeValueAsBytes(obj);
        } catch (IOException e) {
            throw new JsonException(e.getMessage(), e);
        }
    }

}
