package ppl.common.utils.reflect.resolvable;

/**
 *
 */
public interface Resolvable {
    Resolvable[] resolveGenerics(VariableResolver variableResolver);
}
