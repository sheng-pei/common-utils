package ppl.common.utils.reflect.resolvable;

import ppl.common.utils.cache.Cache;
import ppl.common.utils.cache.ConcurrentReferenceValueCache;
import ppl.common.utils.cache.ReferenceType;

import java.lang.reflect.*;
import java.util.concurrent.ExecutionException;

public final class Resolvables {

    private static final Cache<Type, Object> CACHE = new ConcurrentReferenceValueCache<>(ReferenceType.WEAK);

    private Resolvables() {}

    public static Resolvable getResolvable(Type type) {
        if (type instanceof Class) {
            Class<?> clazz = (Class<?>) type;
            if (!clazz.isArray()) {
                return getClassResolvable(clazz);
            } else {
                return getArrayTypeResolvable(clazz);
            }
        } else if (type instanceof ParameterizedType) {
            return getParameterizedTypeResolvable((ParameterizedType) type);
        } else if (type instanceof TypeVariable) {
            return getTypeVariableResolvable((TypeVariable<?>) type);
        } else if (type instanceof WildcardType) {
            return getWildcardTypeResolvable((WildcardType) type);
        } else if (type instanceof GenericArrayType) {
            return getGenericArrayTypeResolvable((GenericArrayType) type);
        } else {
            throw new IllegalArgumentException("Unsupported type.");
        }
    }

    public static ArrayTypeResolvable getArrayTypeResolvable(Class<?> clazz) {
        try {
            return (ArrayTypeResolvable) CACHE.get(clazz, () ->
                    ArrayTypeResolvable.createResolvable(clazz));
        } catch (ExecutionException e) {
            throw new IllegalArgumentException("Failed to resolve class: '" + clazz + "'.", e.getCause());
        }
    }

    public static ClassResolvable getClassResolvable(Class<?> clazz) {
        try {
            return (ClassResolvable) CACHE.get(clazz, () ->
                    ClassResolvable.createResolvable(clazz));
        } catch (ExecutionException e) {
            throw new IllegalArgumentException("Failed to resolve class: '" + clazz + "'.", e.getCause());
        }
    }

    public static TypeVariableResolvable getTypeVariableResolvable(TypeVariable<?> variable) {
        try {
            return (TypeVariableResolvable) CACHE.get(variable, () ->
                    TypeVariableResolvable.createResolvable(variable));
        } catch (ExecutionException e) {
            throw new IllegalArgumentException(String.format(
                    "Failed to resolve type variable: '%s' of '%s'.",
                    variable, variable.getGenericDeclaration()), e.getCause());
        }
    }

    public static ParameterizedTypeResolvable getParameterizedTypeResolvable(ParameterizedType parameterizedType) {
        try {
            return (ParameterizedTypeResolvable) CACHE.get(parameterizedType,
                    () -> ParameterizedTypeResolvable.createResolvable(parameterizedType));
        } catch (ExecutionException e) {
            throw new IllegalArgumentException("Failed to create reflect class.", e.getCause());
        }
    }

    public static WildcardTypeResolvable getWildcardTypeResolvable(WildcardType wildcardType) {
        try {
            return (WildcardTypeResolvable) CACHE.get(wildcardType,
                    () -> WildcardTypeResolvable.createResolvable(wildcardType));
        } catch (ExecutionException e) {
            throw new IllegalArgumentException("Failed to create reflect class.", e.getCause());
        }
    }

    public static ArrayTypeResolvable getGenericArrayTypeResolvable(GenericArrayType genericArrayType) {
        try {
            return (ArrayTypeResolvable) CACHE.get(genericArrayType,
                    () -> ArrayTypeResolvable.createResolvable(genericArrayType));
        } catch (ExecutionException e) {
            throw new IllegalArgumentException("Failed to create reflect class.", e.getCause());
        }
    }
}
