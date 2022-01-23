package ppl.common.utils.config;

public interface Value {
    String textValue(String def);

    String textValue();

    Byte byteValue(Byte def);

    Byte byteValue();

    Short shortValue(Short def);

    Short shortValue();

    Integer intValue(Integer def);

    Integer intValue();

    Long longValue(Long def);

    Long longValue();

    Boolean boolValue(Boolean def);

    Boolean boolValue();

//    <E extends Enum<E>> E enumValue(Class<E> enumClass);
}
