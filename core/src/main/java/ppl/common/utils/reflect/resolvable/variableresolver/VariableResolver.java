package ppl.common.utils.reflect.resolvable.variableresolver;

import ppl.common.utils.reflect.resolvable.Resolvable;

public interface VariableResolver {
    Resolvable resolve(Resolvable resolvable);
}
