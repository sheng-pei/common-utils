package ppl.common.utils.filesystem;

public interface FileSystem extends AutoCloseable {
    String C_ROOT_DIR = "/";
    String C_CURRENT_DIR = ".";
    String C_PARENT_DIR = "..";
    Character C_SEPARATOR = '/';

    Connection getConnection();
}
