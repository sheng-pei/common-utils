package ppl.common.utils.reflect.resolvable;

import ppl.common.utils.reflect.resolvable.variableresolver.VariableResolver;

import java.util.Objects;

public class ResolvedGenericArrayType extends ArrayTypeResolvable {

    private final GenericArrayTypeResolvable raw;

    ResolvedGenericArrayType(GenericArrayTypeResolvable raw, Resolvable component) {
        super(component);
        this.raw = raw;
    }

    @Override
    public Resolvable resolve(VariableResolver variableResolver) {
        return this;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        if (!super.equals(object)) return false;
        ResolvedGenericArrayType that = (ResolvedGenericArrayType) object;
        return Objects.equals(raw, that.raw);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), raw);
    }
}
