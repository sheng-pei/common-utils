package ppl.common.utils.json.jackson;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.util.Date;

public class CommonModule extends SimpleModule {

    @Override
    public void setupModule(SetupContext context) {
        DelegateBeanSerializerModifier delegateBeanSerializerModifier = new DelegateBeanSerializerModifier();
        delegateBeanSerializerModifier.addSerializerModifier(new EnumSerializerModifier());
        delegateBeanSerializerModifier.addSerializerModifier(_serializerModifier);
        setSerializerModifier(delegateBeanSerializerModifier);

        DelegateBeanDeserializerModifier delegateBeanDeserializerModifier = new DelegateBeanDeserializerModifier();
        delegateBeanDeserializerModifier.addDeserializerModifier(new EnumDeserializerModifier());
        delegateBeanDeserializerModifier.addDeserializerModifier(new BeanDeserializerModifier() {
            @Override
            public JsonDeserializer<?> modifyDeserializer(DeserializationConfig config, BeanDescription beanDesc, JsonDeserializer<?> deserializer) {
                if (beanDesc.getBeanClass().equals(Date.class)) {
                    return new DateTimeDeserializer();
                }
                return super.modifyDeserializer(config, beanDesc, deserializer);
            }
        });
        delegateBeanDeserializerModifier.addDeserializerModifier(_deserializerModifier);
        setDeserializerModifier(delegateBeanDeserializerModifier);
        super.setupModule(context);
    }
}
