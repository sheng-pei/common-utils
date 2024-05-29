package ppl.common.utils.reflect.resolvable;

import java.lang.reflect.TypeVariable;
import java.util.Objects;

public class ClassResolvable extends GenericResolvable {

    private final Class<?> type;

    private ClassResolvable(Class<?> type, Resolvable[] generics, Resolvable owner) {
        super(type, generics, owner);
        this.type = type;
    }

    static ClassResolvable createResolvable(Class<?> clazz) {
        executableOwnerInnerClassNotAllowed(clazz);
        checkArrayType(clazz);

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
        return new ClassResolvable(clazz, generics, owner);
    }

    private static void executableOwnerInnerClassNotAllowed(Class<?> clazz) {
        if (clazz.getEnclosingMethod() != null || clazz.getEnclosingConstructor() != null) {
            throw new IllegalArgumentException("Executable owner inner class is not supported.");
        }
    }

    private static void checkArrayType(Class<?> clazz) {
        if (clazz.isArray()) {
            throw new IllegalArgumentException("Array is not allowed.");
        }
    }

    @Override
    public Class<?> getType() {
        return this.type;
    }

    @Override
    protected int index(TypeVariableResolvable variable) {
        Resolvable[] generics = getGenerics();
        for (int i = 0; i < generics.length; i++) {
            if (generics[i].equals(variable)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    protected Resolvable create(Resolvable[] generics, Resolvable owner) {
        return new ParameterizedTypeResolvable(this, generics, owner);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        ClassResolvable that = (ClassResolvable) object;
        return Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }
}
