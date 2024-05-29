package ppl.common.utils.reflect.resolvable;

import ppl.common.utils.exception.UnreachableCodeException;

import java.lang.reflect.*;
import java.util.Objects;

public class ParameterizedTypeResolvable extends GenericResolvable {

    private final ClassResolvable raw;

    ParameterizedTypeResolvable(ClassResolvable raw, Resolvable[] generics, Resolvable owner) {
        super(raw.getType(), generics, owner);
        this.raw = raw;
    }

    static ParameterizedTypeResolvable createResolvable(ParameterizedType parameterizedType) {
        Class<?> clazz = (Class<?>) parameterizedType.getRawType();
        ClassResolvable raw = Resolvables.getClassResolvable(clazz);

        Type[] actualArguments = parameterizedType.getActualTypeArguments();
        Resolvable[] generics = new Resolvable[actualArguments.length];
        for (int i = 0; i < actualArguments.length; i++) {
            Type actualArgument = actualArguments[i];
            if (actualArgument instanceof Class) {
                Class<?> aClazz = (Class<?>) actualArgument;
                if (!aClazz.isArray()) {
                    generics[i] = Resolvables.getClassResolvable(aClazz);
                } else {
                    generics[i] = Resolvables.getArrayTypeResolvable(aClazz);
                }
            } else if (actualArgument instanceof ParameterizedType) {
                generics[i] = Resolvables.getParameterizedTypeResolvable((ParameterizedType) actualArgument);
            } else if (actualArgument instanceof TypeVariable) {
                generics[i] = Resolvables.getTypeVariableResolvable((TypeVariable<?>) actualArgument);
            } else if (actualArgument instanceof WildcardType) {
                generics[i] = Resolvables.getWildcardTypeResolvable((WildcardType) actualArgument);
            } else if (actualArgument instanceof GenericArrayType) {

            } else {
                throw new UnreachableCodeException("Unsupported actual argument of parameterized type. " +
                        "Please check java reflect library.");
            }
        }

        Resolvable owner;
        Type ownerType = parameterizedType.getOwnerType();
        if (ownerType == null) {
            owner = null;
        } else if (ownerType instanceof Class) {
            owner = Resolvables.getClassResolvable((Class<?>) ownerType);
        } else if (ownerType instanceof ParameterizedType) {
            owner = Resolvables.getParameterizedTypeResolvable((ParameterizedType) ownerType);
        } else {
            throw new UnreachableCodeException("Unsupported owner type of parameterized type. " +
                    "Please check java reflect library.");
        }
        return new ParameterizedTypeResolvable(raw, generics, owner);
    }

    @Override
    public Class<?> getType() {
        return raw.getType();
    }

    @Override
    protected int index(TypeVariableResolvable variable) {
        return raw.index(variable);
    }

    @Override
    protected Resolvable create(Resolvable[] generics, Resolvable owner) {
        return new ParameterizedTypeResolvable(raw, generics, owner);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        if (!super.equals(object)) return false;
        ParameterizedTypeResolvable that = (ParameterizedTypeResolvable) object;
        return Objects.equals(raw, that.raw);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), raw);
    }
}
