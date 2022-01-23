package ppl.common.utils.logging;

final class JdkLoggerFactory extends LoggerFactory {

    private static final JdkLoggerFactory INSTANCE = new JdkLoggerFactory();

    private JdkLoggerFactory() {}

    public static JdkLoggerFactory getInstance() {
        return INSTANCE;
    }

    @Override
    public Logger newInstance(String name) {
        return new JdkLogger(java.util.logging.Logger.getLogger(name));
    }
}
