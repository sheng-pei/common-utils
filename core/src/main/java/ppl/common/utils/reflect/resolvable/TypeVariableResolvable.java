package ppl.common.utils.reflect.resolvable;

import java.lang.reflect.*;
import java.util.Objects;

public class TypeVariableResolvable implements Resolvable {

    private final TypeVariable<?> type;
    private volatile BoundKind boundKind;
    private volatile Resolvable[] bounds;

    private TypeVariableResolvable(TypeVariable<?> type) {
        this.type = type;
    }

    static TypeVariableResolvable createResolvable(TypeVariable<?> variable) {
        return new TypeVariableResolvable(variable);
    }

    public String getName() {
        return type.getName();
    }

    public Resolvable getBound(int idx) {
        return null;
    }

    public Resolvable[] getBounds() {
        return null;
    }

//    @Override
//    public void init() {
//        Resolvable d;
//        GenericDeclaration declaration = type.getGenericDeclaration();
//        if (declaration instanceof Class) {
//            d = Resolvables.getClassResolvable((Class<?>) declaration);
////        } else if (declaration instanceof Method) {
////
////        } else if (declaration instanceof Constructor) {
//
//        } else {
//            throw new UnreachableCodeException("Unsupported type variable declaration.");
//        }
//        this.declaration = d;
//        this.boundKind = BoundKind.UPPER;
//        Type[] boundTypes = type.getBounds();
//        if (boundTypes.length == 0) {
//            this.bounds = Resolvables.ZERO_RESOLVABLE;
//        } else {
//            Resolvable[] bounds = new Resolvable[boundTypes.length];
//            for (int i = 0; i < boundTypes.length; i++) {
//                Type type = boundTypes[i];
//                if (type instanceof Class) {
//                    bounds[i] = Resolvables.getClassResolvable((Class<?>) type);
//                } else if (type instanceof TypeVariable) {
//                    bounds[i] = Resolvables.getTypeVariableResolvable((TypeVariable<?>) type);
//                } else if (type instanceof ParameterizedType) {
//                    bounds[i] = Resolvables.getParameterizedTypeResolvable((ParameterizedType) type);
//                } else if (type instanceof GenericArrayType) {
//
//                } else {
//                    throw new UnreachableCodeException("Unsupported type variable bound.");
//                }
//            }
//            this.bounds = bounds;
//        }
//    }

    @Override
    public Resolvable resolve(VariableResolver variableResolver) {
        return variableResolver.resolve(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypeVariableResolvable that = (TypeVariableResolvable) o;
        return Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }
}
