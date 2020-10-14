package ppl.common.utils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

public class ParameterizedTypeResolver {

    private final Class<?> raw;
    private final TypeVariable<?>[] formals;
    private final Type[] actuals;

    private final ParameterizedTypeResolver ownerResolver;
    private final ParameterizedTypeResolver superResolver;
    private final ParameterizedTypeResolver[] interfaceResolvers;

    private ParameterizedTypeResolver(Class<?> raw, TypeVariable<?>[] formals, Type[] actuals) {
        this.raw = raw;
        this.actuals = actuals.clone();
        this.formals = formals;
        this.ownerResolver = null;
        this.superResolver = null;
        this.interfaceResolvers = new ParameterizedTypeResolver[0];
    }

    public static ParameterizedTypeResolver make(Class<?> raw, Type[] actuals) {
        TypeVariable<?>[] formals = raw.getTypeParameters();
        if (formals.length != actuals.length) {
            throw new IllegalArgumentException("The amount of actual type parameters must be the same as the amount of formal type parameters.");
        }

        return new ParameterizedTypeResolver(raw, formals, actuals);
    }

    public static ParameterizedTypeResolver make(ParameterizedType parameterizedType) {
        return make((Class<?>) parameterizedType.getRawType(), parameterizedType.getActualTypeArguments());
    }

    public Type resolveVariable(TypeVariable<?> variable) {
        int i = 0;
        for (; i < this.formals.length; i++) {
            if (StringUtils.equals(this.formals[i].getName(), variable.getName())) {
                return actuals[i];
            }
        }
        return null;
    }

}
