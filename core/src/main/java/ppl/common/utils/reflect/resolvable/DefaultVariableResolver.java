package ppl.common.utils.reflect.resolvable;

import java.lang.reflect.Modifier;
import java.lang.reflect.TypeVariable;
import java.util.Objects;

public final class DefaultVariableResolver implements VariableResolver<Resolvable> {
    private final ClassResolvable raw;
    private final Resolvable[] generics;
    private final Resolvable owner;

    public DefaultVariableResolver(
            ClassResolvable raw,
            Resolvable[] generics,
            Resolvable owner) {
        Objects.requireNonNull(raw);
        Objects.requireNonNull(generics);
        this.raw = raw;
        this.generics = generics;
        this.owner = owner;
    }

    @Override
    public Resolvable resolve(TypeVariable<?> variable) {
        Class<?> rawClass = raw.getType();
        int idx = index(rawClass, variable);
        if (idx >= 0) {
            return generics[idx];
        }

        Resolvable owner = this.owner;
        while (owner != null && !Modifier.isStatic(rawClass.getModifiers())) {
            if (owner instanceof ParameterizedResolvable) {
                ParameterizedResolvable pOwner = (ParameterizedResolvable) owner;
                Resolvable ret = pOwner.getGeneric(variable);
                if (ret != null) {
                    return ret;
                }
            }
            owner = owner.getOwner();
        }
        return null;
    }

    private static int index(Class<?> rawClass, TypeVariable<?> src) {
        TypeVariable<?>[] parameters = rawClass.getTypeParameters();
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].getName().equals(src.getName())) {
                return i;
            }
        }
        return -1;
    }
}
