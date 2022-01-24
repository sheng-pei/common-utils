package ppl.common.utils.logging;

import ppl.common.utils.StringUtils;

import java.util.logging.Level;
import java.util.logging.LogRecord;

class JdkLogger implements Logger {

    private static final String FQCN = JdkLogger.class.getName();

    private final java.util.logging.Logger logger;

    JdkLogger(java.util.logging.Logger logger) {
        this.logger = logger;
    }

    @Override
    public void debug(String msg) {
        if (logger.isLoggable(Level.FINE)) {
            log(Level.FINE, msg, null);
        }
    }

    @Override
    public void debug(String format, Object arg) {
        if (logger.isLoggable(Level.FINE)) {
            LoggingArguments args = new LoggingArguments(arg);
            log(Level.FINE, StringUtils.format(format, args), null);
        }
    }

    @Override
    public void debug(String format, Object argA, Object argB) {
        if (logger.isLoggable(Level.FINE)) {
            LoggingArguments args = new LoggingArguments(argA, argB);
            log(Level.FINE, StringUtils.format(format, args), null);
        }
    }

    @Override
    public void debug(String format, Object... arguments) {
        if (logger.isLoggable(Level.FINE)) {
            LoggingArguments args = new LoggingArguments(arguments);
            log(Level.FINE, StringUtils.format(format, args), null);
        }
    }

    @Override
    public void debug(String msg, Throwable t) {
        if (logger.isLoggable(Level.FINE)) {
            log(Level.FINE, msg, t);
        }
    }

    @Override
    public void warn(String msg) {
        if (logger.isLoggable(Level.WARNING)) {
            log(Level.WARNING, msg, null);
        }
    }

    @Override
    public void warn(String format, Object arg) {
        if (logger.isLoggable(Level.WARNING)) {
            LoggingArguments args = new LoggingArguments(arg);
            log(Level.WARNING, StringUtils.format(format, args), null);
        }
    }

    @Override
    public void warn(String format, Object argA, Object argB) {
        if (logger.isLoggable(Level.WARNING)) {
            LoggingArguments args = new LoggingArguments(argA, argB);
            log(Level.WARNING, StringUtils.format(format, args), null);
        }
    }

    @Override
    public void warn(String format, Object... arguments) {
        if (logger.isLoggable(Level.WARNING)) {
            LoggingArguments args = new LoggingArguments(arguments);
            log(Level.WARNING, StringUtils.format(format, args), null);
        }
    }

    @Override
    public void warn(String msg, Throwable t) {
        if (logger.isLoggable(Level.WARNING)) {
            log(Level.WARNING, msg, t);
        }
    }

    private void log(Level level, String msg, Throwable t) {
        LogRecord record = new LogRecord(level, msg);
        record.setThrown(t);
        record.setLoggerName(logger.getName());
        fillCaller(record);
        logger.log(record);
    }

    private void fillCaller(LogRecord record) {
        StackTraceElement[] elements = new Throwable().getStackTrace();
        boolean found = false;
        for (int i = 0; i < elements.length; i++) {
            if (!found && elements[i].getClassName().equals(FQCN)) {
                found = true;
                continue;
            }

            if (found && !elements[i].getClassName().equals(FQCN)) {
                record.setSourceClassName(elements[i].getClassName());
                record.setSourceMethodName(elements[i].getMethodName());
            }
        }
    }

}
