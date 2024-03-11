package ppl.common.utils.test.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import ppl.common.utils.test.Data;

import java.io.IOException;

public class DataSerializer extends JsonSerializer<Data> {
    @Override
    public void serialize(Data data, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {

    }

    @Override
    public Class<Data> handledType() {
        return Data.class;
    }
}
