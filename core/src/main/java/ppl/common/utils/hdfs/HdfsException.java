package ppl.common.utils.hdfs;

public class HdfsException extends RuntimeException {
    public HdfsException() {
    }

    public HdfsException(String message) {
        super(message);
    }

    public HdfsException(String message, Throwable cause) {
        super(message, cause);
    }
}
