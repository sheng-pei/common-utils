package ppl.common.utils.reflect.resolvable;

import ppl.common.utils.reflect.resolvable.variableresolver.VariableResolver;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.Objects;

public class TypeVariableResolvable implements Resolvable {

    private final TypeVariable<?> type;
    private volatile Resolvable[] bounds;

    private TypeVariableResolvable(TypeVariable<?> type) {
        this.type = type;
    }

    private TypeVariableResolvable(TypeVariable<?> type, Resolvable[] bounds) {
        this.type = type;
        this.bounds = bounds;
    }

    static TypeVariableResolvable createResolvable(TypeVariable<?> variable) {
        return new TypeVariableResolvable(variable);
    }

    public Resolvable getBound(int idx) {
        return getBounds()[idx];
    }

    public Resolvable[] getBounds() {
        Resolvable[] bounds = this.bounds;
        if (bounds == null) {
            Type[] typeBounds = type.getBounds();
            if (typeBounds == null || typeBounds.length == 0) {
                bounds = new Resolvable[1];
                bounds[0] = Resolvables.getResolvable(Object.class);
            } else {
                bounds = Arrays.stream(typeBounds)
                        .map(Resolvables::getResolvable)
                        .toArray(Resolvable[]::new);
            }
            this.bounds = bounds;
        }
        Resolvable[] ret = new Resolvable[bounds.length];
        System.arraycopy(bounds, 0, ret, 0, ret.length);
        return ret;
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
        return new TypeVariableResolvable(type, bounds);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        TypeVariableResolvable that = (TypeVariableResolvable) object;
        return Objects.equals(type, that.type) && Arrays.equals(getBounds(), that.getBounds());
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(type);
        result = 31 * result + Arrays.hashCode(getBounds());
        return result;
    }
}
