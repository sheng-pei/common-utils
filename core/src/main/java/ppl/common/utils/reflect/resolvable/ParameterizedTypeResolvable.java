package ppl.common.utils.reflect.resolvable;

import ppl.common.utils.exception.UnreachableCodeException;

import java.lang.reflect.*;

public class ParameterizedTypeResolvable extends GenericResolvable {

    private final ClassResolvable raw;

    ParameterizedTypeResolvable(ClassResolvable raw, Resolvable[] generics, Resolvable owner) {
        super(raw.getType(), generics, owner);
        this.raw = raw;
    }

    static GenericResolvable createResolvable(ParameterizedType parameterizedType) {
        Class<?> clazz = (Class<?>) parameterizedType.getRawType();
        ClassResolvable raw = Resolvables.getClassResolvable(clazz);

        Type[] actualArguments = parameterizedType.getActualTypeArguments();
        Resolvable[] generics = new Resolvable[actualArguments.length];
        for (int i = 0; i < actualArguments.length; i++) {
            Type actualArgument = actualArguments[i];
            if (actualArgument instanceof Class) {
                generics[i] = Resolvables.getClassResolvable((Class<?>) actualArgument);
            } else if (actualArgument instanceof ParameterizedType) {
                generics[i] = Resolvables.getParameterizedTypeResolvable((ParameterizedType) actualArgument);
            } else if (actualArgument instanceof TypeVariable) {
                generics[i] = Resolvables.getTypeVariableResolvable((TypeVariable<?>) actualArgument);
            } else if (actualArgument instanceof WildcardType) {

            } else if (actualArgument instanceof GenericArrayType) {

            } else {
                throw new UnreachableCodeException("Unsupported actual argument of parameterized type.");
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
            throw new UnreachableCodeException("Unsupported owner type of parameterized type.");
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
}
