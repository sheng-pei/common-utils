//package ppl.common.utils.reflect.resolvable;
//
//import java.lang.reflect.ParameterizedType;
//import java.lang.reflect.Type;
//import java.util.Arrays;
//
//public class ClassResolvable implements Resolvable {
//
//    private final Class<?> type;
//    private final TypeVariableResolvable[] variables;
//    private volatile Resolvable parent;
//    private volatile Resolvable[] interfaces;
//    private volatile Resolvable owner;
//
//    private ClassResolvable(Class<?> type) {
//        this.type = type;
//        this.variables = Arrays.stream(type.getTypeParameters())
//                .map(TypeVariableResolvable::createResolvable)
//                .toArray(TypeVariableResolvable[]::new);
//    }
//
//    static ClassResolvable createClassResolvable(Class<?> clazz) {
//        executableOwnerInnerClassNotAllowed(clazz);
//        return new ClassResolvable(clazz);
//    }
//
//    private static void executableOwnerInnerClassNotAllowed(Class<?> clazz) {
//        if (clazz.getEnclosingMethod() != null || clazz.getEnclosingConstructor() != null) {
//            throw new IllegalArgumentException("Executable owner inner class is not supported.");
//        }
//    }
//
//    public Class<?> getType() {
//        return this.type;
//    }
//
//    public Resolvable getParent() {
//        Type base = type.getGenericSuperclass();
//        if (base instanceof ParameterizedType) {
//            return Resolvables.getParameterizedTypeResolvable((ParameterizedType) base);
//        }
//        return this.parent;
//    }
//
//    public Resolvable[] getInterfaces() {
//        return this.interfaces;
//    }
//
//    public Resolvable getOwner() {
//        return this.owner;
//    }
//
//    public Resolvable getGeneric(TypeVariableResolvable variable) {
//        return variable;
//    }
//
//    public Resolvable getGeneric(int idx) {
//        return variables[idx];
//    }
//
//    public Resolvable[] getGenerics() {
//        Resolvable[] ret = new Resolvable[this.variables.length];
//        System.arraycopy(this.variables, 0, ret, 0, this.variables.length);
//        return ret;
//    }
//
//    public int index(TypeVariableResolvable variable) {
//        for (int i = 0; i < variables.length; i++) {
//            if (variables[i].equals(variable)) {
//                return i;
//            }
//        }
//        return -1;
//    }
//
////    @Override
////    public void init() {
////        this.variables = Arrays.stream(type.getTypeParameters())
////                .map(Resolvables::getTypeVariableResolvable)
////                .toArray(Resolvable[]::new);
////        ClassResolvable owner = ownerType(type.getEnclosingClass());
////        Resolvable parent = inheritedType(type.getGenericSuperclass());
////        Resolvable[] interfaces = Arrays.stream(type.getGenericInterfaces())
////                .map(ClassResolvable::inheritedType)
////                .toArray(Resolvable[]::new);
////        this.parent = parent;
////        this.interfaces = interfaces;
////        this.owner = owner;
////    }
//
////    private static ClassResolvable ownerType(Class<?> owner) {
////        ClassResolvable ret = null;
////        if (owner != null) {
////            ret = Resolvables.getClassResolvable(owner);
////        }
////        return ret;
////    }
////
////    private static Resolvable inheritedType(Type type) {
////        Resolvable ret = null;
////        if (type != null) {
////            if (type instanceof ParameterizedType) {
////                ret = Resolvables.getParameterizedTypeResolvable((ParameterizedType) type);
////            } else if (type instanceof Class) {
////                ret = Resolvables.getClassResolvable((Class<?>) type).resolve();
////            } else {
////                throw new UnreachableCodeException("Unsupported inherited type.");
////            }
////        }
////        return ret;
////    }
//
//    @Override
//    public Resolvable resolve(VariableResolver variableResolver) {
//        return null;
//    }
//
//
////    public Resolvable getGeneric(int idx) {
////        return generics[idx];
////    }
////
////    public Resolvable[] getGenerics() {
////        Resolvable[] ret = new Resolvable[this.generics.length];
////        System.arraycopy(this.generics, 0, ret, 0, this.generics.length);
////        return ret;
////    }
//}
