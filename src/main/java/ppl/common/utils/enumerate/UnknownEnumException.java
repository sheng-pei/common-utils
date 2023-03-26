package ppl.common.utils.enumerate;

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
        return String.format("Unknown enum: %s of %s", enumClass.getSimpleName(), key.toString());
    }

}
