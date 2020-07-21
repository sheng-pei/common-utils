package ppl.common.utils.exception;

import ppl.common.utils.EnumUtils;
import ppl.common.utils.StringUtils;

@SuppressWarnings("rawtypes")
public class InvalidEncoderImplementException extends RuntimeException {

    private final EnumUtils.ERROR error;
    private final Class<? extends Enum> enumClass;

    public InvalidEncoderImplementException(EnumUtils.ERROR error, Class<? extends Enum> enumClass) {
        super();
        this.error = error;
        this.enumClass = enumClass;
    }

    public InvalidEncoderImplementException(EnumUtils.ERROR error, Class<? extends Enum> enumClass, Throwable cause) {
        super(cause);
        this.error = error;
        this.enumClass = enumClass;
    }

    @Override
    public String getMessage() {
        return StringUtils.format(this.error.causeOf(this.enumClass));
    }

}
