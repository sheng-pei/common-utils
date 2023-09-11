package ppl.common.utils.hdfs;

public class FailedRetryException extends RuntimeException {
    public FailedRetryException(String message) {
        super(message);
    }
}
