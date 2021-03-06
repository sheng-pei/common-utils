package ppl.common.utils;

import javax.persistence.AttributeConverter;

/**
 * @author Sheng Pei
 * @date 2020/3/28 23:11
 */
public abstract class EnumConverter<A extends Enum<A>, D> implements AttributeConverter<A, D> {

    private Class<A> enumClazz;
    private Class<D> keyClazz;

    public EnumConverter(Class<A> enumClazz, Class<D> keyClazz) {
        if (!EnumUtils.isEncodeSupport(enumClazz)) {
            throw new IllegalArgumentException(
                    StringUtils.format(
                            "The encode protocol is not implemented in the enum({}), so it does not support converter({})",
                            enumClazz.getCanonicalName(),
                            EnumConverter.class.getCanonicalName()
                    )
            );
        }
        if (!TypeCompatibleUtils.box(EnumUtils.getKeyType(enumClazz)).equals(keyClazz)) {
            throw new IllegalArgumentException(
                    StringUtils.format(
                            "The type of key of enum({}) is not {}",
                            enumClazz.getCanonicalName(),
                            keyClazz.getCanonicalName()
                    )
            );
        }
        this.enumClazz = enumClazz;
        this.keyClazz = keyClazz;
    }

    @Override
    @SuppressWarnings("unchecked")
    public final D convertToDatabaseColumn(A attribute) {
        return EnumUtils.encode(attribute, keyClazz);
    }

    @Override
    public final A convertToEntityAttribute(D dbData) {
        return EnumUtils.enumOf(enumClazz, dbData);
    }

}
