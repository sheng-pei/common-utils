package ppl.common.utils.test.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import ppl.common.utils.test.JsonData;

import java.io.IOException;

public class JsonDataSerializer extends JsonSerializer<JsonData> {
    @Override
    public void serialize(JsonData value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeNull();
        } else {
            gen.writeTree(value.getData());
        }
    }

    @Override
    public Class<JsonData> handledType() {
        return JsonData.class;
    }
}
