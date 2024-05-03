package ppl.common.utils.reflect.resolvable;

import ppl.common.utils.exception.UnreachableCodeException;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;

public class ClassResolvable implements Resolvable, InitializingResolvable {

    private final Class<?> type;
    private volatile Resolvable[] variables;
    private volatile Resolvable parent;
    private volatile Resolvable[] interfaces;
    private volatile Resolvable owner;

    private ClassResolvable(Class<?> type) {
        this.type = type;
    }

    public static ClassResolvable createClassResolvable(Class<?> clazz) {
        executableOwnerInnerClassNotAllowed(clazz);
        return new ClassResolvable(clazz);
    }

    private static void executableOwnerInnerClassNotAllowed(Class<?> clazz) {
        if (clazz.getEnclosingMethod() != null || clazz.getEnclosingConstructor() != null) {
            throw new IllegalArgumentException("Executable owner inner class is not supported.");
        }
    }

    public Class<?> getType() {
        return this.type;
    }

    public Resolvable getParent() {
        return this.parent;
    }

    public Resolvable[] getInterfaces() {
        return this.interfaces;
    }

    public Resolvable getOwner() {
        return this.owner;
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

    @Override
    public void init() {
        this.variables = Arrays.stream(type.getTypeParameters())
                .map(Resolvables::getVariableResolvable)
                .toArray(Resolvable[]::new);
    }

    @Override
    public Resolvable resolve() {
        TypeVariable<?>[] parameters = type.getTypeParameters();
        Resolvable[] generics = Arrays.stream(parameters)
                .map(TypeVariableResolvable::getVariableResolvable)
                .toArray(Resolvable[]::new);
        Arrays.stream(generics).forEach(Resolvable::resolve);


        ClassResolvable owner = ownerType(type.getEnclosingClass());
        Resolvable parent = inheritedType(type.getGenericSuperclass());
        Resolvable[] interfaces = Arrays.stream(type.getGenericInterfaces())
                .map(ClassResolvable::inheritedType)
                .toArray(Resolvable[]::new);
        this.parent = parent;
        this.interfaces = interfaces;
        this.owner = owner;
        return this;
    }

    private static Resolvable ownerType(Class<?> owner) {
        Resolvable ret = null;
        if (owner != null) {
            ret = getClassResolvable(owner).resolve();
        }
        return ret;
    }

    private static Resolvable inheritedType(Type type) {
        Resolvable ret = null;
        if (type != null) {
            if (type instanceof ParameterizedType) {
                ret = ParameterizedResolvable.getParameterizedResolvable((ParameterizedType) type);
            } else if (type instanceof Class) {
                ret = getClassResolvable((Class<?>) type).resolve();
            } else {
                throw new UnreachableCodeException("Inherited type must be Class or ParameterizedType.");
            }
        }
        return ret;
    }

    @Override
    public Resolvable resolveVariables(VariableResolver<Resolvable> variableResolver) {
        throw new UnsupportedOperationException();
    }

    public Resolvable getGeneric(int idx) {
        return generics[idx];
    }

    public Resolvable[] getGenerics() {
        Resolvable[] ret = new Resolvable[this.generics.length];
        System.arraycopy(this.generics, 0, ret, 0, this.generics.length);
        return ret;
    }
}
