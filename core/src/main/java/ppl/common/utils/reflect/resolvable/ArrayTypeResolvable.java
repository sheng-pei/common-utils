package ppl.common.utils.reflect.resolvable;

import ppl.common.utils.exception.UnreachableCodeException;
import ppl.common.utils.reflect.resolvable.variableresolver.VariableResolver;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Objects;

public class ArrayTypeResolvable implements Resolvable {
    private final Resolvable component;

    ArrayTypeResolvable(Resolvable component) {
        this.component = component;
    }

    public static ArrayTypeResolvable createResolvable(Class<?> clazz) {
        checkArrayType(clazz);
        return new ArrayTypeResolvable(componentType(clazz));
    }

    private static void checkArrayType(Class<?> clazz) {
        if (!clazz.isArray()) {
            throw new IllegalArgumentException("Require array type.");
        }
    }

    public static ArrayTypeResolvable createResolvable(GenericArrayType genericArrayType) {
        return new ArrayTypeResolvable(componentType(genericArrayType));
    }

    private static Resolvable componentType(Class<?> clazz) {
        Class<?> component = clazz.getComponentType();
        if (component.isArray()) {
            return Resolvables.getArrayTypeResolvable(component);
        } else {
            return Resolvables.getClassResolvable(component);
        }
    }

    private static Resolvable componentType(GenericArrayType type) {
        Type genericComponentType = type.getGenericComponentType();
        if (genericComponentType instanceof TypeVariable) {
            return Resolvables.getTypeVariableResolvable((TypeVariable<?>) genericComponentType);
        } else if (genericComponentType instanceof ParameterizedType) {
            return Resolvables.getParameterizedTypeResolvable((ParameterizedType) genericComponentType);
        } else {
            throw new UnreachableCodeException("Unsupported component type of generic array type. " +
                    "Please check java reflect library.");
        }
    }

    @SuppressWarnings("unused")
    public Resolvable getComponent() {
        return component;
    }

    @Override
    public Resolvable resolve(VariableResolver variableResolver) {
        return new ArrayTypeResolvable(component.resolve(variableResolver));
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        ArrayTypeResolvable that = (ArrayTypeResolvable) object;
        return Objects.equals(component, that.component);
    }

    @Override
    public int hashCode() {
        return Objects.hash(component);
    }
}
