package ppl.common.utils.filesystem;

public interface FileSystem extends AutoCloseable {
    Connection getConnection() throws Exception;
}
