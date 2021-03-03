package ppl.common.utils.exception;

public class ConfigException extends RuntimeException {
    public ConfigException() {
        super();
    }

    public ConfigException(String message) {
        super(message);
    }

    public ConfigException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigException(Throwable cause) {
        super(cause);
    }

    public static ConfigException newExceptionForConversion(String key, Object value) {
        return newExceptionForConversion(key, value, null);
    }

    public static ConfigException newExceptionForConversion(String key, Object value, Throwable cause) {
        ConfigException result = new ConfigException(errMsg(key, value));
        if (cause != null) {
            result.initCause(cause);
        }
        return result;
    }

    private static String errMsg(String key, Object value) {
        return "Field '" + key + "': invalid type '" + value.getClass().getCanonicalName() + "'";
    }
}
