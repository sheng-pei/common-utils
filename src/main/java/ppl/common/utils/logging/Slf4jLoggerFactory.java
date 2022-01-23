package ppl.common.utils.logging;

import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.NOPLoggerFactory;

final class Slf4jLoggerFactory extends ppl.common.utils.logging.LoggerFactory {

    private static final Slf4jLoggerFactory INSTANCE = newInstance();

    static Slf4jLoggerFactory getInstance() {
        return INSTANCE;
    }

    private final ILoggerFactory loggerFactory;

    private Slf4jLoggerFactory(ILoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    private static Slf4jLoggerFactory newInstance() {
        try {
            ILoggerFactory factory = LoggerFactory.getILoggerFactory();
            if (factory instanceof NOPLoggerFactory) {
                return null;
            }
            return new Slf4jLoggerFactory(factory);
        } catch (LinkageError error) {
            //dependency error.
            return null;
        }
    }

    @Override
    public Logger newInstance(String name) {
        return new Slf4jLogger(loggerFactory.getLogger(name));
    }
}
