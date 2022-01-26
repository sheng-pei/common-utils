package ppl.common.utils.config;

/**
 * Marker interface used to denote accesors for value node.
 */
public interface Value {

    /**
     * Method for accessing binary data which is converted from the data this node contains.
     * @param def the default binary data.
     * @return Returns, by default, binary data as it is if this node is a binary node. Returns, by default,
     * decoded base64 data if it is a text node. The specified default value will be returned if this node is
     * a null or missing node.
     * @throws ConvertException if no conversion is possible from the data this node contains to binary data.
     */
    byte[] binaryValue(byte[] def);

    /**
     * Method similar to {@link #binaryValue(byte[])} except that the default value is always null.
     * @return binary data as described at the "Returns" paragraph of method {@link #binaryValue(byte[])}
     * @throws ConvertException if no conversion is possible from the data this node contains to binary data.
     */
    byte[] binaryValue();

    /**
     * Method for accessing text data which is converted from the data this node contains.
     * @param def the default text data.
     * @return Returns, by default, text data as it is if this node is a text node. Returns, by default, binary data
     * encoded in base64 this node contains if it is a binary node. The specified default value will be returned
     * if this node is a null or missing node.
     * @throws ConvertException if no conversion is possible from the data this node contains to text data.
     */
    String textValue(String def);

    /**
     * Method similar to {@link #textValue(String)} except that the default value is always null.
     * @return text data as described at the "Returns" paragraph of method {@link #textValue()}
     * @throws ConvertException if no conversion is possible from the data this node contains to text data.
     */
    String textValue();

    /**
     * Method for accessing byte data that converted from the data this node contains.
     * @param def, the default byte data.
     * @return Returns, by default, byte data if this node contains primitive integral number or its wrapper
     * ranging from -256 to 255. The specified default value will be returned if this node is a null or missing node.
     * @throws ConvertException if no conversion is possible from the data this node contains to byte data.
     */
    Byte byteValue(Byte def);

    /**
     * Method similar to {@link #byteValue(Byte)} except that the default value is always null.
     * @return byte data as described at the "Returns" paragraph of method {@link #byteValue(Byte)}.
     * @throws ConvertException if no conversion is possible from the data this node contains to byte data.
     */
    Byte byteValue();

    /**
     * Method for accessing short Value that converted from the data this node contains.
     * @param def the default short data.
     * @return Returns, by default, short data if this node contains primitive integral number or its wrapper
     * ranging from -32768 to 32767. The specified default value will be returned if this node is a null or missing
     * node.
     * @throws ConvertException if no conversion is possible from the data this node contains to short data.
     */
    Short shortValue(Short def);

    /**
     * Method similar to {@link #shortValue(Short)} except that the default value is always null.
     * @return short data as described at the "Returns" paragraph of method {@link #shortValue(Short)}
     * @throws ConvertException if no conversion is possible from the data this node contains to short data.
     */
    Short shortValue();

    /**
     * Method for accessing int data that is converted from the data this node contains.
     * @param def the default int data.
     * @return Returns, by default, int data if this node contains primitive integral number or its wrapper ranging
     * from -2147483648 to 2147483647. The specified default value will be returned if this node is a null or missing
     * node.
     * @throws ConvertException if no conversion is possible from the data this node contains to int data.
     */
    Integer intValue(Integer def);

    /**
     * Method similar to {@link #intValue(Integer)} except that the default value is always null.
     * @return int data as described at the "Returns" paragraph of method {@link #intValue(Integer)}.
     * @throws ConvertException if no conversion is possible from the data this node contains to int data.
     */
    Integer intValue();

    /**
     * Method for accessing long data that is converted from the data this node contains.
     * @param def the default long data.
     * @return Returns, by default, long data if this node contains primitive integral number or its wrapper. The
     * specified default value will be returned if this node is a null or missing node.
     * @throws ConvertException if no conversion is possible from the data this node contains to long data.
     */
    Long longValue(Long def);

    /**
     * Method similar to {@link #longValue(Long)} except that the default value is always null.
     * @return long data as described at the "Returns" paragraph of method {@link #longValue(Long)}.
     * @throws ConvertException if no conversion is possible from the data this node contains to long data.
     */
    Long longValue();

    /**
     * Method for accessing bool data that is converted from the data this node contains.
     * @param def the default bool data.
     * @return Returns, by default, bool data as it is if this node is a bool node. The specified default value will
     * be returned if this node is a null or missing node.
     * @throws ConvertException if no conversion is possible from the data this node contains to bool data.
     */
    Boolean boolValue(Boolean def);

    /**
     * Method similar to {@link #boolValue(Boolean)} except that the default value is always null.
     * @return bool data as described at the "Returns" paragraph of method {@link #boolValue(Boolean)}.
     * @throws ConvertException if no conversion is possible from the data this node contains to bool data.
     */
    Boolean boolValue();

    /**
     * Method for accessing double data that is converted from the data this node contains.
     * @param def the default double data.
     * @return Returns, by default, double data if this node contains primitive float number or its wrapper
     * (float or double). The specified default value will be returned if this node is a null or missing node.
     * @throws ConvertException if no conversion is possible from the data this node contains to double data.
     */
    Double doubleValue(Double def);

    /**
     * Method similar to {@link #doubleValue(Double)} except that the default value is always null.
     * @return double data as described at the "Returns" paragraph of method {@link #doubleValue(Double)}.
     * @throws ConvertException if no conversion is possible from the data this node contains to double data.
     */
    Double doubleValue();

    /**
     * Method for accessing enum data that is converted from the data this node contains.
     * @param enumClass the enum class needed.
     * @return Returns, by default, enum data that matches the specified class. The data this node contains is
     * deserialized using {@link ppl.common.utils.enumerate.EnumEncoder} first. If the specified class is not support
     * {@link ppl.common.utils.enumerate.EnumEncoder}, using named or ordinal instead.
     * @throws ConvertException if no conversion is possible from the data this node contains to enum data.
     */
    <E extends Enum<E>> E enumValue(Class<E> enumClass);

}
