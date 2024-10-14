package ppl.common.utils.json.jackson;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;

import java.util.Date;

public class CommonModule extends SimpleModule {

    @Override
    public void setupModule(SetupContext context) {
        DelegateBeanSerializerModifier delegateBeanSerializerModifier = new DelegateBeanSerializerModifier();
        delegateBeanSerializerModifier.addSerializerModifier(new EnumSerializerModifier());
        delegateBeanSerializerModifier.addSerializerModifier(new BeanSerializerModifier() {
            @Override
            public JsonSerializer<?> modifySerializer(SerializationConfig config, BeanDescription beanDesc, JsonSerializer<?> serializer) {
                if (Date.class.isAssignableFrom(beanDesc.getBeanClass())) {
                    return new DateSerializer();
                }
                return super.modifySerializer(config, beanDesc, serializer);
            }
        });
        delegateBeanSerializerModifier.addSerializerModifier(_serializerModifier);
        setSerializerModifier(delegateBeanSerializerModifier);

        DelegateBeanDeserializerModifier delegateBeanDeserializerModifier = new DelegateBeanDeserializerModifier();
        delegateBeanDeserializerModifier.addDeserializerModifier(new EnumDeserializerModifier());
        delegateBeanDeserializerModifier.addDeserializerModifier(new BeanDeserializerModifier() {
            @Override
            public JsonDeserializer<?> modifyDeserializer(DeserializationConfig config, BeanDescription beanDesc, JsonDeserializer<?> deserializer) {
                if (Date.class.isAssignableFrom(beanDesc.getBeanClass())) {
                    return new DateDeserializer();
                }
                return super.modifyDeserializer(config, beanDesc, deserializer);
            }
        });
        delegateBeanDeserializerModifier.addDeserializerModifier(_deserializerModifier);
        setDeserializerModifier(delegateBeanDeserializerModifier);
        super.setupModule(context);
    }
}
