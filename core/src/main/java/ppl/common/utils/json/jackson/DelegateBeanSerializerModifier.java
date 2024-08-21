package ppl.common.utils.json.jackson;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerBuilder;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.databind.type.*;

import java.util.ArrayList;
import java.util.List;

public class DelegateBeanSerializerModifier extends BeanSerializerModifier {
    private final List<BeanSerializerModifier> modifiers = new ArrayList<>();

    public DelegateBeanSerializerModifier addSerializerModifier(BeanSerializerModifier modifier) {
        if (modifier != null) {
            modifiers.add(modifier);
        }
        return this;
    }

    @Override
    public List<BeanPropertyWriter> changeProperties(SerializationConfig config, BeanDescription beanDesc, List<BeanPropertyWriter> beanProperties) {
        for (BeanSerializerModifier modifier : modifiers) {
            beanProperties = modifier.changeProperties(config, beanDesc, beanProperties);
        }
        return super.changeProperties(config, beanDesc, beanProperties);
    }

    @Override
    public List<BeanPropertyWriter> orderProperties(SerializationConfig config, BeanDescription beanDesc, List<BeanPropertyWriter> beanProperties) {
        for (BeanSerializerModifier modifier : modifiers) {
            beanProperties = modifier.orderProperties(config, beanDesc, beanProperties);
        }
        return super.orderProperties(config, beanDesc, beanProperties);
    }

    @Override
    public BeanSerializerBuilder updateBuilder(SerializationConfig config, BeanDescription beanDesc, BeanSerializerBuilder builder) {
        for (BeanSerializerModifier modifier : modifiers) {
            builder = modifier.updateBuilder(config, beanDesc, builder);
        }
        return super.updateBuilder(config, beanDesc, builder);
    }

    @Override
    public JsonSerializer<?> modifySerializer(SerializationConfig config, BeanDescription beanDesc, JsonSerializer<?> serializer) {
        for (BeanSerializerModifier modifier : modifiers) {
            serializer = modifier.modifySerializer(config, beanDesc, serializer);
        }
        return super.modifySerializer(config, beanDesc, serializer);
    }

    @Override
    public JsonSerializer<?> modifyArraySerializer(SerializationConfig config, ArrayType valueType, BeanDescription beanDesc, JsonSerializer<?> serializer) {
        for (BeanSerializerModifier modifier : modifiers) {
            serializer = modifier.modifyArraySerializer(config, valueType, beanDesc, serializer);
        }
        return super.modifyArraySerializer(config, valueType, beanDesc, serializer);
    }

    @Override
    public JsonSerializer<?> modifyCollectionSerializer(SerializationConfig config, CollectionType valueType, BeanDescription beanDesc, JsonSerializer<?> serializer) {
        for (BeanSerializerModifier modifier : modifiers) {
            serializer = modifier.modifyCollectionSerializer(config, valueType, beanDesc, serializer);
        }
        return super.modifyCollectionSerializer(config, valueType, beanDesc, serializer);
    }

    @Override
    public JsonSerializer<?> modifyCollectionLikeSerializer(SerializationConfig config, CollectionLikeType valueType, BeanDescription beanDesc, JsonSerializer<?> serializer) {
        for (BeanSerializerModifier modifier : modifiers) {
            serializer = modifier.modifyCollectionLikeSerializer(config, valueType, beanDesc, serializer);
        }
        return super.modifyCollectionLikeSerializer(config, valueType, beanDesc, serializer);
    }

    @Override
    public JsonSerializer<?> modifyMapSerializer(SerializationConfig config, MapType valueType, BeanDescription beanDesc, JsonSerializer<?> serializer) {
        for (BeanSerializerModifier modifier : modifiers) {
            serializer = modifier.modifyMapSerializer(config, valueType, beanDesc, serializer);
        }
        return super.modifyMapSerializer(config, valueType, beanDesc, serializer);
    }

    @Override
    public JsonSerializer<?> modifyMapLikeSerializer(SerializationConfig config, MapLikeType valueType, BeanDescription beanDesc, JsonSerializer<?> serializer) {
        for (BeanSerializerModifier modifier : modifiers) {
            serializer = modifier.modifyMapLikeSerializer(config, valueType, beanDesc, serializer);
        }
        return super.modifyMapLikeSerializer(config, valueType, beanDesc, serializer);
    }

    @Override
    public JsonSerializer<?> modifyEnumSerializer(SerializationConfig config, JavaType valueType, BeanDescription beanDesc, JsonSerializer<?> serializer) {
        for (BeanSerializerModifier modifier : modifiers) {
            serializer = modifier.modifyEnumSerializer(config, valueType, beanDesc, serializer);
        }
        return super.modifyEnumSerializer(config, valueType, beanDesc, serializer);
    }

    @Override
    public JsonSerializer<?> modifyKeySerializer(SerializationConfig config, JavaType valueType, BeanDescription beanDesc, JsonSerializer<?> serializer) {
        for (BeanSerializerModifier modifier : modifiers) {
            serializer = modifier.modifyKeySerializer(config, valueType, beanDesc, serializer);
        }
        return super.modifyKeySerializer(config, valueType, beanDesc, serializer);
    }
}
