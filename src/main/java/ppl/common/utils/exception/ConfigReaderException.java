package ppl.common.utils.exception;

import ppl.common.utils.StringUtils;

public class ConfigReaderException extends RuntimeException {
    public ConfigReaderException() {
        super();
    }

    public ConfigReaderException(String message) {
        super(message);
    }

    public ConfigReaderException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigReaderException(Throwable cause) {
        super(cause);
    }

    public static ConfigReaderException newExceptionForMember(String key, Object value) {
        return newExceptionForMember(key, value, null);
    }

    public static ConfigReaderException newExceptionForMember(String key, Object value, Throwable cause) {
        ConfigReaderException result = new ConfigReaderException(memberErrMsg(key, value));
        if (cause != null) {
            result.initCause(cause);
        }
        return result;
    }

    public static ConfigReaderException newException(Object value) {
        return newException(value, null);
    }

    public static ConfigReaderException newException(Object value, Throwable cause) {
        ConfigReaderException result = new ConfigReaderException(errMsg(value));
        if (cause != null) {
            result.initCause(cause);
        }
        return result;
    }

    private static String memberErrMsg(String key, Object value) {
        return StringUtils.format("Field '{}': invalid type '{}'", key, value.getClass().getCanonicalName());
    }

    private static String errMsg(Object value) {
        return StringUtils.format("Invalid type '{}'", value.getClass().getCanonicalName());
    }
}
