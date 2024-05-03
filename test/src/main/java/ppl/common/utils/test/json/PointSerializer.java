package ppl.common.utils.test.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import ppl.common.utils.test.point.Point;
import ppl.common.utils.test.point.PointType;
import ppl.common.utils.test.point.Points;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public final class PointSerializer extends JsonSerializer<Point> implements JsonPointHead {

    private final Points points;

    public PointSerializer() {
        this.points = Points.def();
    }

    public PointSerializer(Points points) {
        this.points = points;
    }

    @Override
    public void serialize(Point value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeNull();
            return;
        }

        Class<? extends Point> sourceClass = value.getClass();
        PointType pt = sourceClass.getAnnotation(PointType.class);
        if (pt == null || pt.value() == null || pt.value().isEmpty()) {
            throw new IllegalArgumentException("Point type is required.");
        }

        Class<? extends Point> targetClass = points.getPointClass(pt.value());
        if (targetClass == null) {
            throw new IllegalArgumentException("Unsupported point: '" + sourceClass + "'.");
        }

        if (!sourceClass.equals(targetClass)) {
            throw new IllegalArgumentException(String.format(
                    "Unmatched points '%s' and '%s'.", sourceClass, targetClass));
        }

        Map<String, Object> properties = new HashMap<>();
        properties.put(TYPE, pt.value());
        properties.put(DATA, value);
        gen.writeObject(properties);
    }
}
