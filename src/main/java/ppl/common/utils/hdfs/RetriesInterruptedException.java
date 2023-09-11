package ppl.common.utils.hdfs;

public class RetriesInterruptedException extends RuntimeException {
    public RetriesInterruptedException(String message) {
        super(message);
    }
}
