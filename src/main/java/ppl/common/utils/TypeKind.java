package ppl.common.utils;

import java.lang.reflect.*;

public enum TypeKind {
    CLASS(Class.class, "class"),
    PARAMETERIZED_TYPE(ParameterizedType.class, "parameterized type"),
    WILDCARD_TYPE(WildcardType.class, "wildcard type"),
    TYPE_VARIABLE(TypeVariable.class, "type variable"),
    GENERIC_ARRAY_TYPE(GenericArrayType.class, "generic array type");

    private Class<?> clazz;
    private String desc;

    TypeKind(Class<?> clazz, String desc) {
        this.clazz = clazz;
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    @Override
    public String toString() {
        return this.desc;
    }

    public static TypeKind kindOf(Type type) {
        for (TypeKind kind : TypeKind.class.getEnumConstants()) {
            if (kind.clazz.isInstance(type)) {
                return kind;
            }
        }
        throw new IllegalArgumentException(StringUtils.format("Couldn't classify the specified type {}", type.getClass().getCanonicalName()));
    }

}
