package ppl.common.utils.enumerate;

import ppl.common.utils.string.Strings;

@SuppressWarnings("unused")
public class UnknownEnumException extends IllegalArgumentException {

    @SuppressWarnings("rawtypes")
    private Class<? extends Enum> enumClass;
    private Object key;

    public UnknownEnumException() {
        super();
    }

    @SuppressWarnings("rawtypes")
    public UnknownEnumException(Class<? extends Enum> enumClass, Object key) {
        this.enumClass = enumClass;
        this.key = key;
    }

    @SuppressWarnings("rawtypes")
    public UnknownEnumException(Class<? extends Enum> enumClass, Object key, Throwable cause) {
        super(cause);
        this.enumClass = enumClass;
        this.key = key;
    }

    @Override
    public String getMessage() {
        return Strings.format("Unknown enum: '{}' of '{}'", key.toString(), enumClass.getCanonicalName());
    }

}
