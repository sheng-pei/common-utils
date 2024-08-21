package ppl.common.utils.json.jackson;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.BeanDeserializerBuilder;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.type.*;

import java.util.ArrayList;
import java.util.List;

public class DelegateBeanDeserializerModifier extends BeanDeserializerModifier {
    private final List<BeanDeserializerModifier> modifiers = new ArrayList<>();

    public DelegateBeanDeserializerModifier addDeserializerModifier(BeanDeserializerModifier modifier) {
        if (modifier != null) {
            modifiers.add(modifier);
        }
        return this;
    }

    @Override
    public List<BeanPropertyDefinition> updateProperties(DeserializationConfig config, BeanDescription beanDesc, List<BeanPropertyDefinition> propDefs) {
        for (BeanDeserializerModifier modifier : modifiers) {
            propDefs = modifier.updateProperties(config, beanDesc, propDefs);
        }
        return super.updateProperties(config, beanDesc, propDefs);
    }

    @Override
    public BeanDeserializerBuilder updateBuilder(DeserializationConfig config, BeanDescription beanDesc, BeanDeserializerBuilder builder) {
        for (BeanDeserializerModifier modifier : modifiers) {
            builder = modifier.updateBuilder(config, beanDesc, builder);
        }
        return super.updateBuilder(config, beanDesc, builder);
    }

    @Override
    public JsonDeserializer<?> modifyDeserializer(DeserializationConfig config, BeanDescription beanDesc, JsonDeserializer<?> deserializer) {
        for (BeanDeserializerModifier modifier : modifiers) {
            deserializer = modifier.modifyDeserializer(config, beanDesc, deserializer);
        }
        return super.modifyDeserializer(config, beanDesc, deserializer);
    }

    @Override
    public JsonDeserializer<?> modifyEnumDeserializer(DeserializationConfig config, JavaType type, BeanDescription beanDesc, JsonDeserializer<?> deserializer) {
        for (BeanDeserializerModifier modifier : modifiers) {
            deserializer = modifier.modifyEnumDeserializer(config, type, beanDesc, deserializer);
        }
        return super.modifyEnumDeserializer(config, type, beanDesc, deserializer);
    }

    @Override
    public JsonDeserializer<?> modifyReferenceDeserializer(DeserializationConfig config, ReferenceType type, BeanDescription beanDesc, JsonDeserializer<?> deserializer) {
        for (BeanDeserializerModifier modifier : modifiers) {
            deserializer = modifier.modifyReferenceDeserializer(config, type, beanDesc, deserializer);
        }
        return super.modifyReferenceDeserializer(config, type, beanDesc, deserializer);
    }

    @Override
    public JsonDeserializer<?> modifyArrayDeserializer(DeserializationConfig config, ArrayType valueType, BeanDescription beanDesc, JsonDeserializer<?> deserializer) {
        for (BeanDeserializerModifier modifier : modifiers) {
            deserializer = modifier.modifyArrayDeserializer(config, valueType, beanDesc, deserializer);
        }
        return super.modifyArrayDeserializer(config, valueType, beanDesc, deserializer);
    }

    @Override
    public JsonDeserializer<?> modifyCollectionDeserializer(DeserializationConfig config, CollectionType type, BeanDescription beanDesc, JsonDeserializer<?> deserializer) {
        for (BeanDeserializerModifier modifier : modifiers) {
            deserializer = modifier.modifyCollectionDeserializer(config, type, beanDesc, deserializer);
        }
        return super.modifyCollectionDeserializer(config, type, beanDesc, deserializer);
    }

    @Override
    public JsonDeserializer<?> modifyCollectionLikeDeserializer(DeserializationConfig config, CollectionLikeType type, BeanDescription beanDesc, JsonDeserializer<?> deserializer) {
        for (BeanDeserializerModifier modifier : modifiers) {
            deserializer = modifier.modifyCollectionLikeDeserializer(config, type, beanDesc, deserializer);
        }
        return super.modifyCollectionLikeDeserializer(config, type, beanDesc, deserializer);
    }

    @Override
    public JsonDeserializer<?> modifyMapDeserializer(DeserializationConfig config, MapType type, BeanDescription beanDesc, JsonDeserializer<?> deserializer) {
        for (BeanDeserializerModifier modifier : modifiers) {
            deserializer = modifier.modifyMapDeserializer(config, type, beanDesc, deserializer);
        }
        return super.modifyMapDeserializer(config, type, beanDesc, deserializer);
    }

    @Override
    public JsonDeserializer<?> modifyMapLikeDeserializer(DeserializationConfig config, MapLikeType type, BeanDescription beanDesc, JsonDeserializer<?> deserializer) {
        for (BeanDeserializerModifier modifier : modifiers) {
            deserializer = modifier.modifyMapLikeDeserializer(config, type, beanDesc, deserializer);
        }
        return super.modifyMapLikeDeserializer(config, type, beanDesc, deserializer);
    }

    @Override
    public KeyDeserializer modifyKeyDeserializer(DeserializationConfig config, JavaType type, KeyDeserializer deserializer) {
        for (BeanDeserializerModifier modifier : modifiers) {
            deserializer = modifier.modifyKeyDeserializer(config, type, deserializer);
        }
        return super.modifyKeyDeserializer(config, type, deserializer);
    }
}
