package ppl.common.utils.reflect.resolvable;

import ppl.common.utils.exception.UnreachableCodeException;

import java.lang.reflect.*;

public class TypeVariableResolvable implements Resolvable, InitializingResolvable {

    private final TypeVariable<?> type;
    private volatile Resolvable declaration;
    private volatile BoundKind boundKind;
    private volatile Resolvable[] bounds;

    private TypeVariableResolvable(TypeVariable<?> type) {
        this.type = type;
    }

    static TypeVariableResolvable createVariableResolvable(TypeVariable<?> variable) {
        return new TypeVariableResolvable(variable);
    }

    public Resolvable getBound(int idx) {
        return null;
    }

    public Resolvable[] getBounds() {
        return null;
    }

    @Override
    public void init() {
        Resolvable d;
        GenericDeclaration declaration = type.getGenericDeclaration();
        if (declaration instanceof Class) {
            d = Resolvables.getClassResolvable((Class<?>) declaration);
//        } else if (declaration instanceof Method) {
//
//        } else if (declaration instanceof Constructor) {

        } else {
            throw new UnreachableCodeException("Unsupported type variable declaration.");
        }
        this.declaration = d;
        this.boundKind = BoundKind.UPPER;
        Type[] boundTypes = type.getBounds();
        if (boundTypes.length == 0) {
            this.bounds = Resolvables.ZERO_RESOLVABLE;
        } else {
            Resolvable[] bounds = new Resolvable[boundTypes.length];
            for (int i = 0; i < boundTypes.length; i++) {
                Type type = boundTypes[i];
                if (type instanceof Class) {
                    bounds[i] = Resolvables.getClassResolvable((Class<?>) type);
                } else if (type instanceof TypeVariable) {
                    bounds[i] = Resolvables.getTypeVariableResolvable((TypeVariable<?>) type);
                } else if (type instanceof ParameterizedType) {
                    bounds[i] = Resolvables.getParameterizedTypeResolvable((ParameterizedType) type);
                } else if (type instanceof GenericArrayType) {

                } else {
                    throw new UnreachableCodeException("Unsupported type variable bound.");
                }
            }
            this.bounds = bounds;
        }
    }

    @Override
    public Resolvable resolve() {
        return null;
    }

    @Override
    public Resolvable resolveVariables(VariableResolver<Resolvable> variableResolver) {
        return null;
    }
}
