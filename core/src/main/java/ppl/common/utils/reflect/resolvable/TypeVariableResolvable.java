package ppl.common.utils.reflect.resolvable;

import ppl.common.utils.exception.UnreachableCodeException;

import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.TypeVariable;

public class TypeVariableResolvable implements Resolvable, InitializingResolvable {

    private final TypeVariable<?> type;
    private volatile Resolvable declaration;
    private volatile BoundedType boundedType;
    private volatile Resolvable[] bounds;

    private TypeVariableResolvable(TypeVariable<?> type) {
        this.type = type;
    }

    public static TypeVariableResolvable createVariableResolvable(TypeVariable<?> variable) {
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
//            } else if (declaration instanceof Method) {
//
//            } else if (declaration instanceof Constructor) {

        } else {
            throw new UnreachableCodeException("Unsupported type variable declaration.");
        }
        this.declaration = d;
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
