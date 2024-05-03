package ppl.common.utils.test.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import ppl.common.utils.test.point.Point;
import ppl.common.utils.test.point.Points;

import java.io.IOException;

public final class PointDeserializer extends JsonDeserializer<Point> implements JsonPointHead {

    private final Points points;

    public PointDeserializer() {
        this.points = Points.def();
    }

    public PointDeserializer(Points points) {
        this.points = points;
    }

    @Override
    public Point deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonNode node = p.readValueAs(JsonNode.class);
        if (node == null || node.isEmpty()) {
            return null;
        }

        JsonNode type = node.get(TYPE);
        if (type == null || type.asText("").isEmpty()) {
            throw new PointDeserializerException("Point type is required.");
        }

        Class<? extends Point> clazz = points.getPointClass(type.asText());
        if (clazz == null) {
            throw new PointDeserializerException("Invalid point type: '" + type + "'.");
        }

        JsonNode data = node.get(DATA);
        if (data == null) {
            throw new PointDeserializerException("Data is required.");
        }

        try (JsonParser parser = data.traverse(p.getCodec())) {
            return parser.readValueAs(clazz);
        }
    }

    private static class PointDeserializerException extends JsonProcessingException {
        protected PointDeserializerException(String msg) {
            super(msg);
        }
    }
}
