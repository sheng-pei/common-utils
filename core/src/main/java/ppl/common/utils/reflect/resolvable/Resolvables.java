package ppl.common.utils.reflect.resolvable;

import ppl.common.utils.cache.Cache;
import ppl.common.utils.cache.ConcurrentReferenceValueCache;
import ppl.common.utils.cache.ReferenceType;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.concurrent.ExecutionException;

public final class Resolvables {

    private static final Cache<Type, Object> CACHE = new ConcurrentReferenceValueCache<>(ReferenceType.WEAK);

    private Resolvables() {}

    public static Resolvable getResolvable(Type type) {
        if (type instanceof Class) {
            return getClassResolvable((Class<?>) type);
        } else if (type instanceof ParameterizedType) {
            return getParameterizedTypeResolvable((ParameterizedType) type);
        } else if (type instanceof TypeVariable) {
            return getTypeVariableResolvable((TypeVariable<?>) type);
        } else {
            throw new IllegalArgumentException("Unsupported type.");
        }
    }

    public static Resolvable getClassResolvable(Class<?> clazz) {
        try {
            return (Resolvable) CACHE.get(clazz, () ->
                    ParameterizedTypeResolvable.createResolvable(clazz));
        } catch (ExecutionException e) {
            throw new IllegalArgumentException("Failed to resolve class: '" + clazz + "'.", e.getCause());
        }
    }

    public static Resolvable getTypeVariableResolvable(TypeVariable<?> variable) {
        try {
            return (Resolvable) CACHE.get(variable, () ->
                    TypeVariableResolvable.createResolvable(variable));
        } catch (ExecutionException e) {
            throw new IllegalArgumentException(String.format(
                    "Failed to resolve type variable: '%s' of '%s'.",
                    variable, variable.getGenericDeclaration()), e.getCause());
        }
    }

    public static Resolvable getParameterizedTypeResolvable(ParameterizedType parameterizedType) {
        try {
            return (Resolvable) CACHE.get(parameterizedType,
                    () -> ParameterizedTypeResolvable.createResolvable(parameterizedType));
        } catch (ExecutionException e) {
            throw new IllegalArgumentException("Failed to create reflect class.", e.getCause());
        }
    }
}
