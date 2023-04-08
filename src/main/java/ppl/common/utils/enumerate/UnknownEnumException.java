package ppl.common.utils.enumerate;

import ppl.common.utils.StringUtils;

@SuppressWarnings("unused")
public class UnknownEnumException extends RuntimeException {

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
        return StringUtils.format("Unknown enum: '{}' of '{}'", enumClass.getSimpleName(), key.toString());
    }

}
