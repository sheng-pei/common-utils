package ppl.common.utils.enumerate;

import ppl.common.utils.string.Strings;

import static ppl.common.utils.enumerate.EnumSupport.ERROR;

@SuppressWarnings("rawtypes")
public class EnumEncoderNotSupportedException extends RuntimeException {

    private final ERROR error;
    private final Class<? extends Enum> enumClass;

    public EnumEncoderNotSupportedException(ERROR error, Class<? extends Enum> enumClass) {
        super();
        this.error = error;
        this.enumClass = enumClass;
    }

    public EnumEncoderNotSupportedException(ERROR error, Class<? extends Enum> enumClass, Throwable cause) {
        super(cause);
        this.error = error;
        this.enumClass = enumClass;
    }

    @Override
    public String getMessage() {
        return Strings.format(this.error.causeOf(this.enumClass));
    }

}
