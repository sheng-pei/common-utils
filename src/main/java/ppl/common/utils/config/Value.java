package ppl.common.utils.config;

public interface Value {
    String valueStringOrDefault(String def);

    String valueString();

    Integer valueIntegerOrDefault(Integer def);

    Integer valueInteger();

    Long valueLongOrDefault(Long def);

    Long valueLong();

    Boolean valueBooleanOrDefault(Boolean def);

    Boolean valueBoolean();

    <E extends Enum<E>> E valueEnum(Class<E> enumClass);

    <M> M valueOrDefault(Class<M> targetClass, M def);

    <M> M value(Class<M> targetClass);
}
