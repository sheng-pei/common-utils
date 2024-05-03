package ppl.common.utils.reflect.resolvable;

import java.lang.reflect.TypeVariable;

public interface VariableResolver<T extends Resolvable> {
    T resolve(TypeVariable<?> variable);
}
