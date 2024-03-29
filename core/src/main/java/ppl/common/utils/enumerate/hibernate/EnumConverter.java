package ppl.common.utils.enumerate.hibernate;

import ppl.common.utils.string.Strings;
import ppl.common.utils.reflect.TypeUtils;
import ppl.common.utils.enumerate.EnumUtils;

import javax.persistence.AttributeConverter;

/**
 * @author Sheng Pei
 * TODO, org.hibernate.usertype.UserType
 */
public abstract class EnumConverter<A extends Enum<A>, D> implements AttributeConverter<A, D> {

    private Class<A> enumClazz;
    private Class<D> keyClazz;

    public EnumConverter(Class<A> enumClazz, Class<D> keyClazz) {
        if (!EnumUtils.isEncodeSupport(enumClazz)) {
            throw new IllegalArgumentException(
                    Strings.format(
                            "The encode protocol is not implemented in the enum({}), so it does not support converter({})",
                            enumClazz.getCanonicalName(),
                            EnumConverter.class.getCanonicalName()
                    )
            );
        }
        if (!TypeUtils.box(EnumUtils.getKeyType(enumClazz)).equals(keyClazz)) {
            throw new IllegalArgumentException(
                    Strings.format(
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
    public final D convertToDatabaseColumn(A attribute) {
        return EnumUtils.encode(attribute, keyClazz);
    }

    @Override
    public final A convertToEntityAttribute(D dbData) {
        return EnumUtils.enumOf(enumClazz, dbData);
    }

}
