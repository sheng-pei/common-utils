package ppl.common.utils.reflect.resolvable;

import ppl.common.utils.exception.UnreachableCodeException;
import ppl.common.utils.reflect.resolvable.variableresolver.VariableResolver;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.Objects;

public class TypeVariableResolvable extends BoundResolvable {

    private final TypeVariable<?> type;

    private TypeVariableResolvable(TypeVariable<?> type) {
        super(BoundKind.UPPER, null);
        this.type = type;
    }

    static TypeVariableResolvable createResolvable(TypeVariable<?> variable) {
        return new TypeVariableResolvable(variable);
    }

    @Override
    public Resolvable resolve(VariableResolver variableResolver) {
        Resolvable[] s = getBounds();
        Resolvable[] bounds = Arrays.stream(s)
                .map(variableResolver::resolve)
                .toArray(Resolvable[]::new);
        if (Arrays.equals(s, bounds)) {
            return this;
        }
        return new ResolvedTypeVariable(this, bounds);
    }

    @Override
    protected Type[] bounds() {
        Type[] ret = type.getBounds();
        Arrays.stream(ret).forEach(t -> {
            if (!(t instanceof Class) &&
                    !(t instanceof TypeVariable) &&
                    !(t instanceof ParameterizedType)) {
                throw new UnreachableCodeException("Unsupported bound of type variable. " +
                        "Please check java reflect library.");
            }
        });
        return ret;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        TypeVariableResolvable that = (TypeVariableResolvable) object;
        return Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }
}
