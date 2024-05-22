package ppl.common.utils.reflect.resolvable.variableresolver;

import ppl.common.utils.reflect.resolvable.GenericResolvable;
import ppl.common.utils.reflect.resolvable.Resolvable;
import ppl.common.utils.reflect.resolvable.TypeVariableResolvable;

//TODO, 优化解析，无变更则使用原引用
public final class DefaultVariableResolver implements VariableResolver {
    private final GenericResolvable core;
    private final Resolvable owner;

    public DefaultVariableResolver(GenericResolvable core, Resolvable owner) {
        this.core = core;
        this.owner = owner;
    }

    @Override
    public Resolvable resolve(Resolvable resolvable) {
        Resolvable ret = null;
        if (resolvable instanceof TypeVariableResolvable) {
            GenericResolvable core = this.core;
            ret = core.getGeneric((TypeVariableResolvable) resolvable);

            Resolvable owner = this.owner;
            while (owner instanceof GenericResolvable && ret == null) {
                GenericResolvable o = (GenericResolvable) owner;
                ret = o.getGeneric((TypeVariableResolvable) resolvable);
                owner = o.getOwner();
            }

            if (ret == null) {
                ret = resolvable;
            }
        } else if (resolvable instanceof GenericResolvable) {
            GenericResolvable r = (GenericResolvable) resolvable;
            ret = r.resolve(this);
        }
        return ret;
    }
}
