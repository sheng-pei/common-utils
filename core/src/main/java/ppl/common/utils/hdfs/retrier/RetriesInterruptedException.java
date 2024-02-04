package ppl.common.utils.hdfs.retrier;

public class RetriesInterruptedException extends RetryException {
    public RetriesInterruptedException(String message) {
        super(message);
    }
}
