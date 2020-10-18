package ppl.common.utils;

import ppl.common.utils.exception.TypeResolvedException;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.Objects;

public class TypeResolver {

    private Class<?> raw;
    private Type[] actuals;

    private TypeResolver(ParameterizedType type) {
        this(type.getRawType(), type.getActualTypeArguments());
    }

    private TypeResolver(Type rawType, Type[] actuals) {
        if (!(rawType instanceof Class)) {
            throw new TypeResolvedException("Raw type is not class???");
        }
        this.raw = (Class<?>) rawType;
        this.actuals = actuals;
    }

    public static TypeResolver make(Field field) {
        Type type = field.getGenericType();
        if (type instanceof ParameterizedType) {
            return new TypeResolver((ParameterizedType) type);
        }
        return null;
    }

    public TypeResolver getSuperType() {
        Type superType = raw.getGenericSuperclass();
        if (superType instanceof ParameterizedType) {
            ParameterizedType superParameterizedType = (ParameterizedType) superType;
            Type[] superActuals = resolveVariables(superParameterizedType.getActualTypeArguments());
            return new TypeResolver(superParameterizedType.getRawType(), superActuals);
        }
        return null;
    }

    public TypeResolver[] getInterfaceTypes() {
        Type[] interfaceTypes = raw.getGenericInterfaces();
        TypeResolver[] result = new TypeResolver[interfaceTypes.length];
        for (int i = 0; i < interfaceTypes.length; i++) {
            if (interfaceTypes[i] instanceof ParameterizedType) {
                ParameterizedType interfaceParameterizedType = (ParameterizedType) interfaceTypes[i];
                Type[] interfaceActuals = resolveVariables(interfaceParameterizedType.getActualTypeArguments());
                result[i] = new TypeResolver(interfaceParameterizedType.getRawType(), interfaceActuals);
            }
        }
        return result;
    }

    public TypeResolver getFieldType(String fieldName) {
        Field field;
        try {
            field = this.raw.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            throw new TypeResolvedException("No such field: " + fieldName, e);
        }

        Type fieldType = field.getGenericType();
        if (fieldType instanceof ParameterizedType) {
            ParameterizedType superParameterizedType = (ParameterizedType) fieldType;
            Type[] superActuals = resolveVariables(superParameterizedType.getActualTypeArguments());
            return new TypeResolver(superParameterizedType.getRawType(), superActuals);
        }
        return null;
    }

    public Type[] getActualTypeParameters() {
        return actuals;
    }

    private Type resolveVariable(TypeVariable<?> variable) {
        Objects.requireNonNull(variable, "The variable to be resolved couldn't be null");

        TypeVariable<?>[] formals = this.raw.getTypeParameters();
        int i = 0;
        for (; i < formals.length; i++) {
            if (StringUtils.equals(formals[i].getName(), variable.getName())) {
                return actuals[i];
            }
        }
        return null;
    }

    private Type[] resolveVariables(Type[] variables) {
        Arrays.stream(variables).forEach(variable -> {if (!(variable instanceof TypeVariable)) {
            throw new IllegalArgumentException("Couldn't resolve variable been not TypeVariable");
        }});

        return Arrays.stream(variables)
                .map(variable -> resolveVariable((TypeVariable<?>) variable))
                .toArray(Type[]::new);
    }

}
