package ppl.common.utils.reflect.resolvable;

import ppl.common.utils.reflect.resolvable.variableresolver.VariableResolver;

import java.lang.reflect.*;
import java.util.Objects;

public class TypeVariableResolvable implements Resolvable {

    private final TypeVariable<?> type;
    private volatile BoundKind boundKind;
    private volatile Resolvable[] bounds;

    private TypeVariableResolvable(TypeVariable<?> type) {
        this.type = type;
    }

    static TypeVariableResolvable createResolvable(TypeVariable<?> variable) {
        return new TypeVariableResolvable(variable);
    }

    public Resolvable getBound(int idx) {
        return null;
    }

    public Resolvable[] getBounds() {
        return null;
    }

    @Override
    public Resolvable resolve(VariableResolver variableResolver) {
        return variableResolver.resolve(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypeVariableResolvable that = (TypeVariableResolvable) o;
        return Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }
}
