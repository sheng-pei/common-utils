package ppl.common.utils.reflect.resolvable;

import ppl.common.utils.exception.UnreachableCodeException;

import java.lang.reflect.*;

public class ParameterizedTypeResolvable implements Resolvable, InitializingResolvable {

    private final ClassResolvable raw;
    private final Resolvable[] generics;
    private volatile Resolvable parent;
    private volatile Resolvable[] interfaces;
    private volatile Resolvable owner;

    private ParameterizedTypeResolvable(
            ClassResolvable raw,
            Resolvable[] generics,
            Resolvable owner) {
        this.raw = raw;
        this.generics = generics;
        this.owner = owner;
    }

    public static ParameterizedTypeResolvable createParameterizedResolvable(ParameterizedType parameterizedType) {
        Class<?> clazz = (Class<?>) parameterizedType.getRawType();
        ClassResolvable raw = Resolvables.getClassResolvable(clazz);

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
        if (ownerType instanceof Class) {
            owner = Resolvables.getClassResolvable((Class<?>) ownerType);
        } else if (ownerType instanceof ParameterizedType) {
            owner = Resolvables.getParameterizedTypeResolvable((ParameterizedType) ownerType);
        } else {
            throw new UnreachableCodeException("Unsupported owner type of parameterized type.");
        }
        return new ParameterizedTypeResolvable(raw, generics, owner);
    }

//    private static Resolvable ownerType(Type owner) {
//        Resolvable ret = null;
//        if (owner != null) {
//            if (owner instanceof Class) {
//                ret = ClassResolvable.getRawResolvable((Class<?>) owner);
//            } else if (owner instanceof ParameterizedType) {
//                ret = ParameterizedResolvable.getParameterizedResolvable((ParameterizedType) owner);
//            } else {
//                throw new UnreachableCodeException("Owner type must be Class or ParameterizedType.");
//            }
//        }
//        return ret;
//    }
//
//    private static Variable getVariable(ParameterizedResolvable parameterizedResolvable, TypeVariable<?> variable) {
//        try {
//            return (Variable) cache.get(variable, () -> parameterizedResolvable.new Variable(variable));
//        } catch (ExecutionException e) {
//            throw new IllegalArgumentException("Failed to create reflect class.", e);
//        }
//    }
//
//    private static Resolvable boundType(Type bound, VariableResolver<Resolvable> variableResolver) {
//        if (bound instanceof Class) {
//            return getReflectClass((Class<?>) bound);
//        } else if (bound instanceof ParameterizedType) {
//            return getReflectClass((ParameterizedType) bound);
//        } else if (bound instanceof TypeVariable) {
//            return variableResolver.resolve((TypeVariable<?>) bound);
//        } else {
//            throw new UnreachableCodeException("Bound type must be ParameterizedType or TypeVariable or Class.");
//        }
//    }
//    private static void resolve(Type[] actualParameters, Object[] generics) {
//        for (int i = 0; i < generics.length; i++) {
//            Type t = actualParameters[i];
//            if (t instanceof Class) {
//                generics[i] = create((Class<?>) t);
//            } else if (t instanceof TypeVariable) {
//                generics[i] = (Function<VariableResolver<Resolvable>, Resolvable>)
//                        vr -> vr.resolve((TypeVariable<?>) t);
//            } else if (t instanceof ParameterizedType) {
//                generics[i] = create((ParameterizedType) t);
//            } else if (t instanceof GenericArrayType) {
//                generics[i] = (Function<VariableResolver<Resolvable>, Resolvable>)
//                        vr -> create((GenericArrayType) t, vr);
//            } else if (t instanceof WildcardType) {
//                generics[i] = (Function<VariableResolver<Resolvable>, Resolvable>)
//                        vr -> create((WildcardType) t, vr);
//            } else {
//                throw new UnreachableCodeException("Unknown reflect type: '" + t.getClass() + "'.");
//            }
//        }
//    }
//
    //final
//    public static ParameterizedResolvable create(Class<?> clazz) {
//        ParameterizedResolvable ret = getReflectClass(clazz);
//
//        VariableResolver<Resolvable> variableResolver =
//                new DefaultVariableResolver(ret,
//                        Modifier.isStatic(clazz.getModifiers()) ? null : ret.ownerReflectClass);
//        TypeVariable<?>[] parameters = clazz.getTypeParameters();
//        if (parameters.length != 0) {
//            ret.init(Arrays.stream(parameters)
//                    .map(p -> ret.new Variable(p, variableResolver))
//                    .toArray(Resolvable[]::new));//init
//        }
//        return ret;
//    }
//
//    public static WildcardResolvable create(WildcardType wildcardType, VariableResolver<Resolvable> variableResolver) {
//        BoundedType boundedType = null;
//        Resolvable bound = null;
//        Type[] uppers = wildcardType.getUpperBounds();
//        Type[] lowers = wildcardType.getLowerBounds();
//        if (uppers.length != 0) {
//            boundedType = BoundedType.UPPER;
//            bound = boundType(uppers[0], variableResolver);
//        }
//        if (lowers.length != 0) {
//            boundedType = BoundedType.LOWER;
//            bound = boundType(lowers[0], variableResolver);
//        }
//        return boundedType == null ? WildcardResolvable.ANY : new WildcardResolvable(bound, boundedType);
//    }
//
//    private static Resolvable boundType(Type bound, VariableResolver<Resolvable> variableResolver) {
//        if (bound instanceof Class) {
//            return create((Class<?>) bound);
//        } else if (bound instanceof ParameterizedType) {
//            return create((ParameterizedType) bound, variableResolver);
//        } else if (bound instanceof TypeVariable) {
//            return variableResolver.resolve((TypeVariable<?>) bound);
//        } else {
//            throw new UnreachableCodeException("Bound type must be ParameterizedType or TypeVariable or Class.");
//        }
//    }
//
//    public static ParameterizedResolvable create(ParameterizedType parameterizedType, VariableResolver<Resolvable> variableResolver) {
//        return create(parameterizedType).apply(variableResolver);
//    }
//
//    private static Function<VariableResolver<Resolvable>, ParameterizedResolvable> create(ParameterizedType parameterizedType) {
//        Class<?> c = (Class<?>) parameterizedType.getRawType();
//        ParameterizedResolvable rawClass = getReflectClass(c);
//
//        Type[] actual = parameterizedType.getActualTypeArguments();
//        Object[] generics = new Object[actual.length];
//        resolve(actual, generics);//init
//        return vr -> {
//            rawClass.init(GenericsResolver.DEFAULT.apply(vr, generics));//init
//            return rawClass;
//        };
//    }
//
//    public static ResolvableArray create(GenericArrayType genericArrayType, VariableResolver<Resolvable> variableResolver) {
//        Type type = genericArrayType.getGenericComponentType();
//        return new ResolvableArray(componentType(type, variableResolver));
//    }
//
//    private static Resolvable componentType(Type bound, VariableResolver<Resolvable> variableResolver) {
//        if (bound instanceof ParameterizedType) {
//            return create((ParameterizedType) bound, variableResolver);
//        } else if (bound instanceof TypeVariable) {
//            return variableResolver.resolve((TypeVariable<?>) bound);
//        } else {
//            throw new UnreachableCodeException("Component type must be ParameterizedType or TypeVariable.");
//        }
//    }

    public Resolvable getParent() {
        return parent;
    }

    public Resolvable[] getInterfaces() {
        return interfaces;
    }

    public Resolvable getOwner() {
        return owner;
    }

    public Resolvable getGeneric(TypeVariable<?> variable) {
        Class<?> rawClass = raw.getType();
        if (!variable.getGenericDeclaration().equals(rawClass)) {
            return null;
        }

        int idx = index(rawClass, variable);
        if (idx >= 0) {
            return generics[idx];
        }
        return null;
    }

    private static int index(Class<?> rawClass, TypeVariable<?> src) {
        TypeVariable<?>[] parameters = rawClass.getTypeParameters();
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].getName().equals(src.getName())) {
                return i;
            }
        }
        return -1;
    }

    public Resolvable getGeneric(int idx) {
        return generics[idx];
    }

    public Resolvable[] getGenerics() {
        Resolvable[] ret = new Resolvable[this.generics.length];
        System.arraycopy(this.generics, 0, ret, 0, this.generics.length);
        return ret;
    }

    @Override
    public void init() {
        Resolvable p = raw.getParent();

    }

    @Override
    public Resolvable resolve() {
        return null;
    }

    @Override
    public Resolvable resolveVariables(VariableResolver<Resolvable> variableResolver) {
        Resolvable rawParent = raw.getParent();
        if (rawParent instanceof ClassResolvable) {
            parent = rawParent;
        } else {//rawParent instanceof ParameterizedResolvable

        }
        return null;
//        Resolvable[] generics = getGenerics();
//        Resolvable[] newGenerics = new Resolvable[generics.length];
//        for (int i = 0; i < generics.length; i++) {
//            Resolvable o = generics[i];
//            if (t instanceof Class) {
//                generics[i] = ClassResolvable.getRawResolvable((Class<?>) t);
//                //variable resolver
//                generics[i].resolveVariables(null);
//            } else if (t instanceof TypeVariable) {
//                generics[i] = variableResolver.resolve((TypeVariable<?>) t);
//                //
//                generics[i].resolveVariables(null);
//            } else if (t instanceof ParameterizedType) {
//                generics[i] = getParameterizedResolvable((ParameterizedType) t);
//                generics[i].resolveVariables(variableResolver);
//            }
//            else if (t instanceof GenericArrayType) {
//                generics[i] = (Function<VariableResolver<Resolvable>, Resolvable>)
//                        vr -> create((GenericArrayType) t, vr);
//            } else if (t instanceof WildcardType) {
//                generics[i] = (Function<VariableResolver<Resolvable>, Resolvable>)
//                        vr -> create((WildcardType) t, vr);
//            }
//            else {
//                throw new UnreachableCodeException("Unknown reflect type: '" + t.getClass() + "'.");
//            }
//        }
    }

//    private static final class GenericsResolver implements BiFunction<VariableResolver<Resolvable>, Object[], Resolvable[]> {
//
//        public static final GenericsResolver DEFAULT = new GenericsResolver();
//
//        @Override
//        public Resolvable[] apply(VariableResolver<Resolvable> variableResolver, Object[] objects) {
//            Resolvable[] ret = new Resolvable[objects.length];
//            for (int i = 0; i < objects.length; i++) {
//                Object object = objects[i];
//                if (object instanceof Function) {
//                    @SuppressWarnings("unchecked")
//                    Function<VariableResolver<Resolvable>, Resolvable> func =
//                            (Function<VariableResolver<Resolvable>, Resolvable>) object;
//                    ret[i] = func.apply(variableResolver);
//                } else if (object instanceof Resolvable) {
//                    ret[i] = (Resolvable) object;
//                } else {
//                    throw new UnreachableCodeException();
//                }
//            }
//            return ret;
//        }
//    }
//
//
//    private static final class OnOwnerVariableResolver implements BiFunction<ParameterizedResolvable, TypeVariable<?>, Resolvable> {
//
//        public static final BiFunction<ParameterizedResolvable, TypeVariable<?>, ? extends Resolvable> DEFAULT = new OnOwnerVariableResolver();
//
//        @Override
//        public Resolvable apply(ParameterizedResolvable parameterizedResolvable, TypeVariable<?> typeVariable) {
//            ParameterizedResolvable current = parameterizedResolvable;
//            while (current != null) {
//                VariableResolver<Resolvable> variableResolver =
//                        new DefaultVariableResolver(current,
//                                Modifier.isStatic(current.raw.getModifiers()) ? null : current.ownerReflectClass);
//                TypeVariable<?> v = getTypeVariable(current.raw.getTypeParameters(), typeVariable);
//                if (v != null) {
//                    return current.new Variable(v, variableResolver);
//                }
//                current = current.ownerReflectClass;
//            }
//            return null;
//        }
//    }
//
//    private static final class OnCoreVariableResolver implements BiFunction<ParameterizedResolvable, TypeVariable<?>, Resolvable> {
//
//        public static final BiFunction<ParameterizedResolvable, TypeVariable<?>, Resolvable> DEFAULT = new OnCoreVariableResolver();
//
//        @Override
//        public Resolvable apply(ParameterizedResolvable parameterizedResolvable, TypeVariable<?> typeVariable) {
//            VariableResolver<Resolvable> variableResolver =
//                    new DefaultVariableResolver(parameterizedResolvable,
//                            Modifier.isStatic(parameterizedResolvable.raw.getModifiers()) ? null : parameterizedResolvable.ownerReflectClass);
//            TypeVariable<?> v = getTypeVariable(parameterizedResolvable.raw.getTypeParameters(), typeVariable);
//            if (v != null) {
//                return parameterizedResolvable.new Variable(v, variableResolver);
//            }
//            return null;
//        }
//    }
//
//    private static TypeVariable<?> getTypeVariable(TypeVariable<?>[] variables, TypeVariable<?> src) {
//        return Arrays.stream(variables)
//                .filter(v -> src.getName().equals(v.getName()))
//                .findFirst()
//                .orElse(null);
//    }
//
//    private class Variable implements Resolvable {
//        private final TypeVariable<?> variable;
//        private volatile Resolvable[] bounds = ZERO_RESOLVABLE;
//
//        public Variable(TypeVariable<?> variable) {
//            this.variable = variable;
//        }
//
//        public void init(Resolvable[] bounds) {
//            this.bounds = bounds;
////                    Arrays.stream(variable.getBounds())
////                    .filter(t -> !t.equals(Object.class))
////                    .map(t -> boundType(t, variableResolver))
////                    .peek(r -> Objects.requireNonNull(r, "Unknown bound type."))
////                    .toArray(Resolvable[]::new);
//        }
//
//        public ParameterizedResolvable getDeclaration() {
//            return ParameterizedResolvable.this;
//        }
//
//        @Override
//        public Class<?> resolve() {
//            TypeVariable<?>[] parameters = raw.getTypeParameters();
//            for (int i = 0; i < parameters.length; i++) {
//                if (parameters[i].getName().equals(variable.getName())) {
//                    Resolvable resolvable = generics[i];
//                    if (resolvable instanceof Variable) {
//                        Variable variable = (Variable) resolvable;
//                        if (variable.getDeclaration() == ParameterizedResolvable.this) {
//                            if (bounds.length == 0) {
//                                return Object.class;
//                            } else {
//                                return bounds[0].resolve();
//                            }
//                        } else {
//                            return resolvable.resolve();
//                        }
//                    } else {
//                        return resolvable.resolve();
//                    }
//                }
//            }
//            if (bounds.length == 0) {
//                return Object.class;
//            } else {
//                return bounds[0].resolve();
//            }
//        }
//
//        @Override
//        public Class<?> resolve(VariableResolver<ParameterizedResolvable> resolver) {
//            return null;
//        }
//    }

}
