package ppl.common.utils.logging;

import org.slf4j.spi.LocationAwareLogger;

public class LocationAwareSlf4jLogger implements Logger {

    private static final String FQCN = LocationAwareSlf4jLogger.class.getName();

    private final LocationAwareLogger logger;

    LocationAwareSlf4jLogger(LocationAwareLogger logger) {
        this.logger = logger;
    }

    @Override
    public void warn(String msg) {

    }

    @Override
    public void warn(String format, Object arg) {

    }

    @Override
    public void warn(String format, Object argA, Object argB) {

    }

    @Override
    public void warn(String format, Object... arguments) {

    }

    @Override
    public void warn(String msg, Throwable t) {

    }

    private void log(int level, String msg) {
        logger.log(null, FQCN, level, msg, null,null);
    }

    private void log(int level, String msg, Throwable t) {
        logger.log(null, FQCN, level, msg, null, t);
    }

    private void log(int level, String format, Object... arguments) {

    }

}
