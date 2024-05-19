package ppl.common.utils.reflect.resolvable;

public final class DefaultVariableResolver implements VariableResolver {
    private final ParameterizedTypeResolvable core;
    private final Resolvable owner;

    public DefaultVariableResolver(ParameterizedTypeResolvable core, Resolvable owner) {
        this.core = core;
        this.owner = owner;
    }

    @Override
    public Resolvable resolve(Resolvable resolvable) {
        Resolvable ret = resolvable;
        if (resolvable instanceof ClassResolvable) {

        } else if (resolvable instanceof TypeVariableResolvable) {
            ParameterizedTypeResolvable core = this.core;
            ret = core.getGeneric((TypeVariableResolvable) ret);

            Resolvable owner = this.owner;
            while (owner instanceof ParameterizedTypeResolvable && ret.equals(resolvable)) {
                ParameterizedTypeResolvable o = (ParameterizedTypeResolvable) owner;
                ret = o.getGeneric((TypeVariableResolvable) ret);
                owner = o.getOwner();
            }
        } else if (resolvable instanceof ParameterizedTypeResolvable) {
            ParameterizedTypeResolvable r = (ParameterizedTypeResolvable) resolvable;
            Resolvable[] generics = r.resolveGenerics(this);
            return ParameterizedTypeResolvable.createParameterizedResolvable(r.getRaw(), generics, resolve(r.getOwner()));
        }
        return ret;
    }
}
