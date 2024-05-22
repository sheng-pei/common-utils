package ppl.common.utils.reflect.resolvable;

import ppl.common.utils.exception.UnreachableCodeException;
import ppl.common.utils.reflect.resolvable.variableresolver.DefaultVariableResolver;
import ppl.common.utils.reflect.resolvable.variableresolver.VariableResolver;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReferenceArray;

public abstract class GenericResolvable implements Resolvable {

    private final Resolvable[] generics;
    private final Resolvable owner;

    private volatile Resolvable parent;
    private final AtomicReferenceArray<Resolvable> interfaces;

    protected GenericResolvable(
            Class<?> clazz,
            Resolvable[] generics,
            Resolvable owner) {
        this.generics = generics;
        this.owner = owner;
        this.interfaces = new AtomicReferenceArray<>(
                clazz.getGenericInterfaces().length);
    }

    public abstract Class<?> getType();

    public Resolvable getParent() {
        Resolvable parent = this.parent;
        if (parent == null) {
            parent = getAndResolveResolvable(getType().getGenericSuperclass());
            this.parent = parent;
        }
        return parent;
    }

    public Resolvable[] getInterfaces() {
        Type[] originInterfaces = getType().getInterfaces();
        Resolvable[] ret = new Resolvable[this.interfaces.length()];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = getAndResolveResolvableInterface(i, originInterfaces[i]);
        }
        return ret;
    }

    private Resolvable getAndResolveResolvableInterface(int i, Type origin) {
        Resolvable r = this.interfaces.get(i);
        if (r == null) {
            r = getAndResolveResolvable(origin);
            this.interfaces.set(i, r);
        }
        return r;
    }

    private Resolvable getAndResolveResolvable(Type origin) {
        Resolvable ret = null;
        if (origin != null) {
            if (origin instanceof Class) {
                ret = Resolvables.getClassResolvable((Class<?>) origin);
            } else if (origin instanceof ParameterizedType) {
                ret = Resolvables.getParameterizedTypeResolvable((ParameterizedType) origin);
            } else {
                throw new UnreachableCodeException("Unsupported type for 'implements ?'.");
            }
            ret = ret.resolve(new DefaultVariableResolver(this, owner));
        }
        return ret;
    }

    public Resolvable getOwner() {
        return owner;
    }

    public Resolvable getGeneric(TypeVariableResolvable variable) {
        int idx = index(variable);
        if (idx >= 0) {
            return generics[idx];
        }
        return null;
    }

    public Resolvable getGeneric(int idx) {
        return generics[idx];
    }

    public Resolvable[] getGenerics() {
        Resolvable[] ret = new Resolvable[this.generics.length];
        System.arraycopy(this.generics, 0, ret, 0, this.generics.length);
        return ret;
    }

    protected abstract int index(TypeVariableResolvable variable);

    @Override
    public Resolvable resolve(VariableResolver variableResolver) {
        Resolvable owner = getOwner();
        if (owner != null) {
            owner = owner.resolve(variableResolver);
        }
        return create(resolveGenerics(variableResolver), owner);
    }

    private Resolvable[] resolveGenerics(VariableResolver variableResolver) {
        Resolvable[] generics = this.generics;
        return Arrays.stream(generics)
                .map(variableResolver::resolve)
                .toArray(Resolvable[]::new);
    }

    protected abstract Resolvable create(Resolvable[] generics, Resolvable owner);

}
