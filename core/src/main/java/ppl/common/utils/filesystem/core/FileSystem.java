package ppl.common.utils.filesystem.core;

public interface FileSystem extends AutoCloseable {
    Connection getConnection();

    @Override
    void close();
}
