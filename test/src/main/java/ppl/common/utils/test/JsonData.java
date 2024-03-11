package ppl.common.utils.test;

import com.fasterxml.jackson.databind.JsonNode;

public class JsonData implements Data {
    private final JsonNode data;

    public JsonData(Object data) {
        this.data = (JsonNode) data;
    }

    public JsonNode getData() {
        return data;
    }
}
