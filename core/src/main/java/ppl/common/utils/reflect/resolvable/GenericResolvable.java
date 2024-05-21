package ppl.common.utils.reflect.resolvable;

import ppl.common.utils.exception.UnreachableCodeException;

import java.lang.reflect.*;
import java.util.Arrays;

public abstract class GenericResolvable implements Resolvable {

    private final Resolvable[] generics;
    private final Resolvable owner;

    private volatile Resolvable parent;
    private final Resolvable[] interfaces;

    protected GenericResolvable(
            Class<?> clazz,
            Resolvable[] generics,
            Resolvable owner) {
        this.generics = generics;
        this.owner = owner;
        this.interfaces = new Resolvable[clazz.getGenericInterfaces().length];
    }

    public abstract Class<?> getType();

    public Resolvable getParent() {
        Resolvable parent = this.parent;
        if (parent == null) {
            Type pType = getType().getGenericSuperclass();
            if (pType instanceof Class) {
                parent = Resolvables.getClassResolvable((Class<?>) pType);
            } else if (pType instanceof ParameterizedType) {
                parent = Resolvables.getParameterizedTypeResolvable((ParameterizedType) pType);
            } else {
                throw new UnreachableCodeException("Unsupported type for 'extends ?'.");
            }
            parent = parent.resolve(new DefaultVariableResolver(this, owner));
            this.parent = parent;
        }
        return parent;
    }

    public Resolvable[] getInterfaces() {
//        Resolvable[] ret = new Resolvable[this.interfaces.length];
//        for (int i = 0; i < ret.length; i++) {
//            ret[i] = getInterface(i);
//        }
//        return ret;
        return null;
    }

//    private Resolvable getInterface(int i) {
//        Resolvable r = this.interfaces[i];
//        if (r == null) {
//            r = resolve(raw.getInterfaces()[i]);
//            this.interfaces[i] = r;
//        }
//        return r;
//    }
//
//    private Resolvable resolve(Resolvable resolvable) {
//        if (resolvable instanceof ParameterizedTypeResolvable) {
//            ParameterizedTypeResolvable ppt = (ParameterizedTypeResolvable) resolvable;
//            resolvable = ppt.resolve(new DefaultVariableResolver(this, owner));
//        } else {
//            throw new UnreachableCodeException("Unsupported type for 'implements ?'.");
//        }
//        return resolvable;
//    }

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
