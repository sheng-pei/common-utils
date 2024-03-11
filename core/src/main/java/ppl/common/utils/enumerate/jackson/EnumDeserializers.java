package ppl.common.utils.enumerate.jackson;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleDeserializers;
import ppl.common.utils.enumerate.EnumUtils;

import java.io.IOException;

@SuppressWarnings("unused")
public class EnumDeserializers extends SimpleDeserializers {
    private static final EnumDeserializers INSTANCE = new EnumDeserializers();

    public static EnumDeserializers getInstance() {
        return INSTANCE;
    }

    @Override
    public JsonDeserializer<?> findEnumDeserializer(Class<?> type, DeserializationConfig config, BeanDescription beanDesc) throws JsonMappingException {
        if (type.isEnum()) {
            @SuppressWarnings("unchecked")
            Class<? extends Enum<?>> eClass = (Class<? extends Enum<?>>) type;
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
        return super.findEnumDeserializer(type, config, beanDesc);
    }
}
