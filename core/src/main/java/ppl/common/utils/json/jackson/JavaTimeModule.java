package ppl.common.utils.json.jackson;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

public class JavaTimeModule extends SimpleModule {
    private final com.fasterxml.jackson.datatype.jsr310.JavaTimeModule module;

    public JavaTimeModule() {
        this.module = new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule();
    }

    @Override
    public void setupModule(SetupContext context) {
        module.setupModule(context);
        DelegateBeanSerializerModifier delegateBeanSerializerModifier = new DelegateBeanSerializerModifier();
        delegateBeanSerializerModifier.addSerializerModifier(new BeanSerializerModifier() {
            @Override
            public JsonSerializer<?> modifySerializer(SerializationConfig config, BeanDescription beanDesc, JsonSerializer<?> serializer) {
                if (beanDesc.getBeanClass().equals(ZonedDateTime.class)) {
                    return new ZonedDateTimeSerializer();
                }
                if (beanDesc.getBeanClass().equals(LocalDateTime.class)) {
                    return new LocalDateTimeSerializer();
                }
                return super.modifySerializer(config, beanDesc, serializer);
            }
        });
        delegateBeanSerializerModifier.addSerializerModifier(_serializerModifier);
        setSerializerModifier(delegateBeanSerializerModifier);

        DelegateBeanDeserializerModifier delegateBeanDeserializerModifier = new DelegateBeanDeserializerModifier();
        delegateBeanDeserializerModifier.addDeserializerModifier(new BeanDeserializerModifier() {
            @Override
            public JsonDeserializer<?> modifyDeserializer(DeserializationConfig config, BeanDescription beanDesc, JsonDeserializer<?> deserializer) {
                if (beanDesc.getBeanClass().equals(ZonedDateTime.class)) {
                    return new ZonedDateTimeDeserializer();
                }
                if (beanDesc.getBeanClass().equals(LocalDateTime.class)) {
                    return new LocalDateTimeDeserializer();
                }
                return super.modifyDeserializer(config, beanDesc, deserializer);
            }
        });
        delegateBeanDeserializerModifier.addDeserializerModifier(_deserializerModifier);
        setDeserializerModifier(delegateBeanDeserializerModifier);
        super.setupModule(context);
    }
}
