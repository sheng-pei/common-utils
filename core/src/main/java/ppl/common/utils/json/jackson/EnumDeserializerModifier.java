package ppl.common.utils.json.jackson;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import ppl.common.utils.enumerate.EnumUtils;

import java.io.IOException;

public class EnumDeserializerModifier extends BeanDeserializerModifier {
    @Override
    public JsonDeserializer<?> modifyEnumDeserializer(DeserializationConfig config, JavaType type, BeanDescription beanDesc, JsonDeserializer<?> deserializer) {
        Class<?> raw = type.getRawClass();
        if (raw.isEnum()) {
            @SuppressWarnings("unchecked")
            Class<? extends Enum<?>> eClass = (Class<? extends Enum<?>>) raw;
            if (EnumUtils.isEncodeSupport(eClass)) {
                return new JsonDeserializer<Enum<? extends Enum<?>>>() {
                    @Override
                    @SuppressWarnings({"RedundantThrows", "DuplicateThrows"})
                    public Enum<? extends Enum<?>> deserialize(JsonParser p, DeserializationContext context) throws IOException, JacksonException {
                        Class<?> keyClass = EnumUtils.getKeyType(eClass);
                        return EnumUtils.enumOf(eClass, p.readValueAs(keyClass));
                    }
                };
            }
        }
        return super.modifyEnumDeserializer(config, type, beanDesc, deserializer);
    }
}
