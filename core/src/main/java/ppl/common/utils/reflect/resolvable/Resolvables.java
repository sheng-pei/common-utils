package ppl.common.utils.reflect.resolvable;

import ppl.common.utils.cache.Cache;
import ppl.common.utils.cache.ConcurrentReferenceValueCache;
import ppl.common.utils.cache.ReferenceType;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

public final class Resolvables {

    private static final Cache<Type, Object> CACHE = new ConcurrentReferenceValueCache<>(ReferenceType.WEAK);
    public static final Resolvable[] ZERO_RESOLVABLE = new Resolvable[0];

    private Resolvables() {}

    public static ClassResolvable getClassResolvable(Class<?> clazz) {
        try {
            return (ClassResolvable) CACHE.get(clazz, () ->
                    ClassResolvable.createClassResolvable(clazz));
        } catch (ExecutionException e) {
            throw new IllegalArgumentException("Failed to resolve class: '" + clazz + "'.", e.getCause());
        }
    }

    public static TypeVariableResolvable getTypeVariableResolvable(TypeVariable<?> variable) {
        try {
            return (TypeVariableResolvable) CACHE.get(variable, () ->
                    TypeVariableResolvable.createVariableResolvable(variable));
        } catch (ExecutionException e) {
            throw new IllegalArgumentException(String.format(
                    "Failed to resolve type variable: '%s' of '%s'.",
                    variable, variable.getGenericDeclaration()), e.getCause());
        }
    }

    public static ParameterizedTypeResolvable getParameterizedTypeResolvable(ParameterizedType parameterizedType) {
        try {
            return (ParameterizedTypeResolvable) CACHE.get(parameterizedType,
                    () -> ParameterizedTypeResolvable.createParameterizedResolvable(parameterizedType));
        } catch (ExecutionException e) {
            throw new IllegalArgumentException("Failed to create reflect class.", e.getCause());
        }
    }
}
