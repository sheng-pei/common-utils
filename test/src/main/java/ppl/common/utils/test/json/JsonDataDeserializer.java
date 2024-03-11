package ppl.common.utils.test.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import ppl.common.utils.test.JsonData;

import java.io.IOException;

public class JsonDataDeserializer extends JsonDeserializer<JsonData> {
    @Override
    public JsonData deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        JsonNode node = jsonParser.readValueAs(JsonNode.class);
        if (node == null || node.isEmpty()) {
            return null;
        }
        return new JsonData(node);
    }

    @Override
    public Class<?> handledType() {
        return JsonData.class;
    }
}
