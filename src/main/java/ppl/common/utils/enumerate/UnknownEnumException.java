package ppl.common.utils.enumerate;

public class UnknownEnumException extends RuntimeException {

    private Class<? extends Enum<?>> enumClass;
    private Object key;

    public UnknownEnumException() {
        super();
    }

    public UnknownEnumException(Class<? extends Enum<?>> enumClass, Object key) {
        this.enumClass = enumClass;
        this.key = key;
    }

    public UnknownEnumException(Class<? extends Enum<?>> enumClass, Object key, Throwable cause) {
        super(cause);
        this.enumClass = enumClass;
        this.key = key;
    }

    @Override
    public String getMessage() {
        return String.format("Unknown enum: %s of %s", enumClass.getSimpleName(), key.toString());
    }

}
