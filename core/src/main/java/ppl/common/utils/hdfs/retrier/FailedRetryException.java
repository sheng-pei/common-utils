package ppl.common.utils.hdfs.retrier;

public class FailedRetryException extends RetryException {
    public FailedRetryException(String message) {
        super(message);
    }
}
