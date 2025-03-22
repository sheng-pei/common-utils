package ppl.common.utils.reflect.resolvable;

import ppl.common.utils.reflect.resolvable.variableresolver.VariableResolver;

public interface Resolvable {
    Class<?> getType();
    Resolvable resolve(VariableResolver variableResolver);
}
