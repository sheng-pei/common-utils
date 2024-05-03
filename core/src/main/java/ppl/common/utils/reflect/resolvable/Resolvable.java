package ppl.common.utils.reflect.resolvable;

import ppl.common.utils.cache.Cache;
import ppl.common.utils.cache.ConcurrentReferenceValueCache;
import ppl.common.utils.cache.ReferenceType;

import java.lang.reflect.Type;

public interface Resolvable {
    void init();

    Resolvable resolve();

    Resolvable resolveVariables(VariableResolver<Resolvable> variableResolver);

    enum Status {
        NEW, INITIALIZING, INITIALIZED, RESOLVING, RESOLVED
    }
}
