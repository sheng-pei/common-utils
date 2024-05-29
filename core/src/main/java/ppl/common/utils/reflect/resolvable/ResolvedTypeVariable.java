package ppl.common.utils.reflect.resolvable;

import ppl.common.utils.reflect.resolvable.variableresolver.VariableResolver;

import java.lang.reflect.Type;
import java.util.Objects;

public class ResolvedTypeVariable extends BoundResolvable {

    private final TypeVariableResolvable raw;

    ResolvedTypeVariable(TypeVariableResolvable raw, Resolvable[] bounds) {
        super(raw.getKind(), bounds);
        this.raw = raw;
    }

    @Override
    protected Type[] bounds() {
        return raw.bounds();
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
        ResolvedTypeVariable that = (ResolvedTypeVariable) object;
        return Objects.equals(raw, that.raw);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), raw);
    }
}
