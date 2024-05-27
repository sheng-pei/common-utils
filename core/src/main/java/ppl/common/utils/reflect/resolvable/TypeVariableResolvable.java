package ppl.common.utils.reflect.resolvable;

import ppl.common.utils.reflect.resolvable.variableresolver.VariableResolver;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.Objects;

public class TypeVariableResolvable implements Resolvable {

    private final TypeVariable<?> type;
    private final Resolvable[] bounds;

    private TypeVariableResolvable(TypeVariable<?> type, Resolvable[] bounds) {
        this.type = type;
        this.bounds = bounds;
    }

    static TypeVariableResolvable createResolvable(TypeVariable<?> variable) {
        Type[] typeBounds = variable.getBounds();
        Resolvable[] bounds;
        if (typeBounds == null || typeBounds.length == 0) {
            bounds = new Resolvable[1];
            bounds[0] = Resolvables.getResolvable(Object.class);
        } else {
            bounds = Arrays.stream(typeBounds)
                    .map(Resolvables::getResolvable)
                    .toArray(Resolvable[]::new);
        }
        return new TypeVariableResolvable(variable, bounds);
    }

    public Resolvable getBound(int idx) {
        return bounds[idx];
    }

    public Resolvable[] getBounds() {
        Resolvable[] ret = new Resolvable[bounds.length];
        System.arraycopy(bounds, 0, ret, 0, ret.length);
        return ret;
    }

    @Override
    public Resolvable resolve(VariableResolver variableResolver) {
        Resolvable ret = variableResolver.resolve(this);
        if (ret == this) {

        }
        return ret;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        TypeVariableResolvable that = (TypeVariableResolvable) object;
        return Objects.equals(type, that.type);// && Arrays.equals(bounds, that.bounds);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(type);
        //result = 31 * result + Arrays.hashCode(bounds);
        return result;
    }
}
