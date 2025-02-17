package ppl.common.utils.json.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import ppl.common.utils.enumerate.EnumUtils;

import java.io.IOException;

public class EnumSerializerModifier extends BeanSerializerModifier {
    @Override
    public JsonSerializer<?> modifyEnumSerializer(SerializationConfig config, JavaType valueType, BeanDescription beanDesc, JsonSerializer<?> serializer) {
        Class<?> raw = valueType.getRawClass();
        if (raw.isEnum()) {
            @SuppressWarnings("unchecked")
            Class<? extends Enum<?>> eClass = (Class<? extends Enum<?>>) raw;
            if (EnumUtils.isEncodeSupport(eClass)) {
                return new JsonSerializer<Enum<? extends Enum<?>>>() {
                    @Override
                    public void serialize(Enum<? extends Enum<?>> value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                        gen.writePOJO(EnumUtils.encode(value));
                    }
                };
            }
        }
        return super.modifyEnumSerializer(config, valueType, beanDesc, serializer);
    }
}
