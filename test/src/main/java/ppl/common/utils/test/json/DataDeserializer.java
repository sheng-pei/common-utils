package ppl.common.utils.test.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import ppl.common.utils.test.Data;

public class DataDeserializer extends JsonDeserializer<Data> {
    @Override
    public Data deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        JsonNode text = jsonParser.readValueAs(JsonNode.class);
        if (text == null || text.isEmpty()) {
            return null;
        }
        System.out.println(text.toPrettyString());
        return null;
    }

    @Override
    public Class<?> handledType() {
        return Data.class;
    }
}
