package ppl.common.utils.test.json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import ppl.common.utils.test.Data;
import ppl.common.utils.test.JsonData;

import java.io.IOException;
import java.util.Date;

public class JsonUtil {
    private static final JsonMapper MAPPER;

    static {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Date.class, new DateTimeDeserializer());
        module.addSerializer(Date.class, new DateTimeSerializer());
        module.addSerializer(JsonData.class, new JsonDataSerializer());
        module.addDeserializer(JsonData.class, new JsonDataDeserializer());
        JsonMapper mapper = new JsonMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.registerModule(module);
        MAPPER = mapper;
    }

    public static JsonNode parseNode(String json) {
        try {
            return MAPPER.readTree(json);
        } catch (IOException e) {
//            throw new JsonException("Json parse error", e);
        }
        return null;
    }

    public static <T> T parseObject(String json, TypeReference<T> reference) {
        try {
            return MAPPER.readerFor(reference).readValue(json);
        } catch (IOException e) {
//            throw new JsonException("Json parse error for " + reference.getType().getTypeName(), e);
        }
        return null;
    }

    public static String toString(Object obj) {
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (IOException e) {
//            throw new JsonException(e.getMessage(), e);
        }
        return null;
    }

}
