package ppl.common.utils.reflect.resolvable;

import ppl.common.utils.exception.UnreachableCodeException;

import java.lang.reflect.*;
import java.util.Arrays;

public class ParameterizedTypeResolvable implements Resolvable {

    private final Class<?> type;
    private final Resolvable[] generics;
    private final Resolvable owner;

    private volatile Resolvable parent;
    private final Resolvable[] interfaces;

    private ParameterizedTypeResolvable(
            Class<?> type,
            Resolvable[] generics,
            Resolvable owner) {
        this.type = type;
        this.generics = generics;
        this.owner = owner;
        Type[] interfaces = type.getGenericInterfaces();
        this.interfaces = new Resolvable[interfaces.length];
    }

    static ParameterizedTypeResolvable createResolvable(ParameterizedType parameterizedType) {
        Class<?> clazz = (Class<?>) parameterizedType.getRawType();

        Type[] actualArguments = parameterizedType.getActualTypeArguments();
        Resolvable[] generics = new Resolvable[actualArguments.length];
        for (int i = 0; i < actualArguments.length; i++) {
            Type actualArgument = actualArguments[i];
            if (actualArgument instanceof Class) {
                generics[i] = Resolvables.getClassResolvable((Class<?>) actualArgument);
            } else if (actualArgument instanceof ParameterizedType) {
                generics[i] = Resolvables.getParameterizedTypeResolvable((ParameterizedType) actualArgument);
            } else if (actualArgument instanceof TypeVariable) {
                generics[i] = Resolvables.getTypeVariableResolvable((TypeVariable<?>) actualArgument);
            } else if (actualArgument instanceof WildcardType) {

            } else if (actualArgument instanceof GenericArrayType) {

            } else {
                throw new UnreachableCodeException("Unsupported actual argument of parameterized type.");
            }
        }

        Resolvable owner;
        Type ownerType = parameterizedType.getOwnerType();
        if (ownerType == null) {
            owner = null;
        } else if (ownerType instanceof Class) {
            owner = Resolvables.getClassResolvable((Class<?>) ownerType);
        } else if (ownerType instanceof ParameterizedType) {
            owner = Resolvables.getParameterizedTypeResolvable((ParameterizedType) ownerType);
        } else {
            throw new UnreachableCodeException("Unsupported owner type of parameterized type.");
        }
        return new ParameterizedTypeResolvable(clazz, generics, owner);
    }

    static ParameterizedTypeResolvable createResolvable(Class<?> clazz) {
        TypeVariable<?>[] variables = clazz.getTypeParameters();
        Resolvable[] generics = new Resolvable[variables.length];
        for (int i = 0; i < variables.length; i++) {
            TypeVariable<?> variable = variables[i];
            generics[i] = Resolvables.getTypeVariableResolvable(variable);
        }

        Resolvable owner;
        Class<?> ownerType = clazz.getEnclosingClass();
        if (ownerType == null) {
            owner = null;
        } else {
            owner = Resolvables.getClassResolvable(ownerType);
        }
        return new ParameterizedTypeResolvable(clazz, generics, owner);
    }

    public Class<?> getType() {
        return type;
    }

    public Resolvable getParent() {
        Resolvable parent = this.parent;
        if (parent == null) {
            Type pType = type.getGenericSuperclass();
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

//    public Resolvable getGeneric(TypeVariableResolvable variable) {
//        int idx = raw.index(variable);
//        if (idx >= 0) {
//            return generics[idx];
//        }
//        return variable;
//    }

    public Resolvable getGeneric(int idx) {
        return generics[idx];
    }

    public Resolvable[] getGenerics() {
        Resolvable[] ret = new Resolvable[this.generics.length];
        System.arraycopy(this.generics, 0, ret, 0, this.generics.length);
        return ret;
    }

    @Override
    public Resolvable resolve(VariableResolver variableResolver) {
        return new ParameterizedTypeResolvable(type,
                resolveGenerics(variableResolver),
                getOwner().resolve(variableResolver));
    }

    private Resolvable[] resolveGenerics(VariableResolver variableResolver) {
        Resolvable[] generics = this.generics;
        return Arrays.stream(generics)
                .map(variableResolver::resolve)
                .toArray(Resolvable[]::new);
    }

}
