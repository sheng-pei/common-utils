package ppl.common.utils.logging;

public abstract class LoggerFactory {

    private static final LoggerFactory INSTANCE;

    static {
        LoggerFactory factory = get();
        Logger selfLogger = factory.newInstance(LoggerFactory.class.getName());
        if (factory instanceof Slf4jLoggerFactory) {

        } else if (factory instanceof JdkLoggerFactory) {

        }
        INSTANCE = factory;
    }

    abstract Logger newInstance(String name);

    public static Logger getLogger(String name) {
        return INSTANCE.newInstance(name);
    }

    public static Logger getLogger(Class<?> clazz) {
        return getLogger(clazz.getName());
    }

    private static LoggerFactory get() {
        Slf4jLoggerFactory factory = Slf4jLoggerFactory.getInstance();
        if (factory != null) {
            return factory;
        }

        return JdkLoggerFactory.getInstance();
    }

}
