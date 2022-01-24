package ppl.common.utils.logging;

import ppl.common.utils.StringUtils;

class Slf4jLogger implements Logger {

    private final org.slf4j.Logger logger;

    public Slf4jLogger(org.slf4j.Logger logger) {
        this.logger = logger;
    }

    @Override
    public void debug(String msg) {
        if (logger.isDebugEnabled()) {
            logger.debug(msg);
        }
    }

    @Override
    public void debug(String format, Object arg) {
        if (logger.isDebugEnabled()) {
            LoggingArguments args = new LoggingArguments(arg);
            logger.debug(StringUtils.format(format, args));
        }
    }

    @Override
    public void debug(String format, Object argA, Object argB) {
        if (logger.isDebugEnabled()) {
            LoggingArguments args = new LoggingArguments(argA, argB);
            logger.debug(StringUtils.format(format, args));
        }
    }

    @Override
    public void debug(String format, Object... arguments) {
        if (logger.isDebugEnabled()) {
            LoggingArguments args = new LoggingArguments(arguments);
            logger.debug(StringUtils.format(format, args));
        }
    }

    @Override
    public void debug(String msg, Throwable t) {
        if (logger.isDebugEnabled()) {
            logger.debug(msg, t);
        }
    }

    @Override
    public void warn(String msg) {
        if (logger.isWarnEnabled()) {
            logger.warn(msg);
        }
    }

    @Override
    public void warn(String format, Object arg) {
        if (logger.isWarnEnabled()) {
            LoggingArguments args = new LoggingArguments(arg);
            logger.warn(StringUtils.format(format, args));
        }
    }

    @Override
    public void warn(String format, Object argA, Object argB) {
        if (logger.isWarnEnabled()) {
            LoggingArguments args = new LoggingArguments(argA, argB);
            logger.warn(StringUtils.format(format, args));
        }
    }

    @Override
    public void warn(String format, Object... arguments) {
        if (logger.isWarnEnabled()) {
            LoggingArguments args = new LoggingArguments(arguments);
            logger.warn(StringUtils.format(format, args));
        }
    }

    @Override
    public void warn(String msg, Throwable t) {
        if (logger.isWarnEnabled()) {
            logger.warn(msg, t);
        }
    }

}
