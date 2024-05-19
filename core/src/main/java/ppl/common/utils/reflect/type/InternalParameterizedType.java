package ppl.common.utils.reflect.type;

import ppl.common.utils.ArrayUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class InternalParameterizedType implements ParameterizedType {
    private final Class<?> raw;
    private final Type[] actualTypeArguments;
    private final Type owner;

    private InternalParameterizedType(Class<?> raw, Type owner) {
        this.raw = raw;
        this.actualTypeArguments = ArrayUtils.zero(Type.class);
        this.owner = owner;
    }

    private InternalParameterizedType(ParameterizedType parameterizedType) {
        this.raw = (Class<?>) parameterizedType.getRawType();
        this.actualTypeArguments = parameterizedType.getActualTypeArguments();
        this.owner = parameterizedType.getOwnerType();
    }

    public static InternalParameterizedType create(Class<?> raw, Type owner) {
        return new InternalParameterizedType(raw, owner);
    }

    public static InternalParameterizedType create(ParameterizedType parameterizedType) {
        if (parameterizedType instanceof InternalParameterizedType) {
            return (InternalParameterizedType) parameterizedType;
        } else {
            return new InternalParameterizedType(parameterizedType);
        }
    }

    @Override
    public Type[] getActualTypeArguments() {
        return actualTypeArguments;
    }

    @Override
    public Type getRawType() {
        return raw;
    }

    @Override
    public Type getOwnerType() {
        return owner;
    }

    @Override
    public String getTypeName() {
        return toString();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (this.owner != null) {
            builder.append(this.owner.getTypeName())
                    .append("$")
                    .append(this.raw.getSimpleName());
        } else {
            builder.append(this.raw.getName());
        }

        Type[] actualTypeArguments = this.actualTypeArguments;
        if (actualTypeArguments != null && actualTypeArguments.length > 0) {
            builder.append("<");
            builder.append(Arrays.stream(actualTypeArguments)
                    .map(Type::getTypeName)
                    .collect(Collectors.joining(", ")));
            builder.append(">");
        }
        return builder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ParameterizedType) {
            ParameterizedType other = (ParameterizedType)o;
            if (this == other) {
                return true;
            } else {
                Type owner = other.getOwnerType();
                Type raw = other.getRawType();
                return Objects.equals(this.owner, owner) &&
                        Objects.equals(this.raw, raw) &&
                        Arrays.equals(this.actualTypeArguments, other.getActualTypeArguments());
            }
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(raw, owner);
        result = 31 * result + Arrays.hashCode(actualTypeArguments);
        return result;
    }
}
