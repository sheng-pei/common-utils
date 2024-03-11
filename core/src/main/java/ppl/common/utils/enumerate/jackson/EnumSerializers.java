package ppl.common.utils.enumerate.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleSerializers;
import ppl.common.utils.enumerate.EnumUtils;

import java.io.IOException;

@SuppressWarnings("unused")
public class EnumSerializers extends SimpleSerializers {
    private static final EnumSerializers INSTANCE = new EnumSerializers();

    public static EnumSerializers getInstance() {
        return INSTANCE;
    }

    @Override
    public JsonSerializer<?> findSerializer(SerializationConfig config, JavaType type, BeanDescription beanDesc) {
        Class<?> raw = type.getRawClass();
        if (type.getRawClass().isEnum()) {
            @SuppressWarnings("unchecked")
            Class<? extends Enum<?>> eClass = (Class<? extends Enum<?>>) raw;
            if (EnumUtils.isEncodeSupport(eClass)) {
                return new JsonSerializer<Enum<? extends Enum<?>>>() {
                    @Override
                    public void serialize(Enum<? extends Enum<?>> value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                        Object obj = EnumUtils.encode(value);
                        if (obj instanceof String) {
                            gen.writeString(obj.toString());
                        } else if (obj instanceof Character) {
                            gen.writeString(obj.toString());
                        } else if (obj instanceof Number) {
                            gen.writeNumber(obj.toString());
                        }
                    }
                };
            }
        }
        return super.findSerializer(config, type, beanDesc);
    }
}
