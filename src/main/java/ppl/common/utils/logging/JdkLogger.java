package ppl.common.utils.logging;

import java.util.logging.Level;
import java.util.logging.LogRecord;

class JdkLogger implements Logger {

    private final java.util.logging.Logger logger;

    JdkLogger(java.util.logging.Logger logger) {
        this.logger = logger;
    }

    @Override
    public void warn(String msg) {
        logger.warning(msg);
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

    private void log(Level level, String msg, Throwable t) {
        LogRecord record = new LogRecord(level, msg);
        record.setThrown(t);
        record.setLoggerName(logger.getName());
//        record.set
    }

}
