package ppl.common.utils.logging;

import org.slf4j.event.Level;
import org.slf4j.spi.LocationAwareLogger;
import ppl.common.utils.StringUtils;

import static org.slf4j.spi.LocationAwareLogger.DEBUG_INT;
import static org.slf4j.spi.LocationAwareLogger.WARN_INT;

public class LocationAwareSlf4jLogger implements Logger {

    private static final String FQCN = LocationAwareSlf4jLogger.class.getName();

    private final LocationAwareLogger logger;

    LocationAwareSlf4jLogger(LocationAwareLogger logger) {
        this.logger = logger;
    }

    @Override
    public void debug(String msg) {
        if (logger.isDebugEnabled()) {
            log(DEBUG_INT, msg);
        }
    }

    @Override
    public void debug(String format, Object arg) {
        if (logger.isDebugEnabled()) {
            log(DEBUG_INT, format, arg);
        }
    }

    @Override
    public void debug(String format, Object argA, Object argB) {
        if (logger.isDebugEnabled()) {
            log(DEBUG_INT, format, argA, argB);
        }
    }

    @Override
    public void debug(String format, Object... arguments) {
        if (logger.isDebugEnabled()) {
            log(DEBUG_INT, format, arguments);
        }
    }

    @Override
    public void debug(String msg, Throwable t) {
        if (logger.isDebugEnabled()) {
            log(DEBUG_INT, msg, t);
        }
    }

    @Override
    public void warn(String msg) {
        if (logger.isWarnEnabled()) {
            log(WARN_INT, msg);
        }
    }

    @Override
    public void warn(String format, Object arg) {
        if (logger.isWarnEnabled()) {
            log(WARN_INT, format, arg);
        }
    }

    @Override
    public void warn(String format, Object argA, Object argB) {
        if (logger.isWarnEnabled()) {
            log(WARN_INT, format, argA, argB);
        }
    }

    @Override
    public void warn(String format, Object... arguments) {
        if (logger.isWarnEnabled()) {
            log(WARN_INT, format, arguments);
        }
    }

    @Override
    public void warn(String msg, Throwable t) {
        if (logger.isWarnEnabled()) {
            log(WARN_INT, msg, t);
        }
    }

    private void log(int level, String msg) {
        logger.log(null, FQCN, level, msg, null,null);
    }

    private void log(int level, String msg, Throwable t) {
        logger.log(null, FQCN, level, msg, null, t);
    }

    private void log(int level, String format, Object... arguments) {
        LoggingArguments args = new LoggingArguments(arguments);
        log(level, StringUtils.format(format, args));
    }

}
