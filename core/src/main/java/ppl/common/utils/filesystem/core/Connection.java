package ppl.common.utils.filesystem.core;

import ppl.common.utils.filesystem.path.Path;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Predicate;

/**
 * A connection object to a {@link FileSystem filesystem}
 *
 * <p>This connection is not guarantee to be thread-safe.
 * Please don't publish a connection to two or more thread
 * at the same time.</p>
 */
@SuppressWarnings("unused")
public interface Connection extends AutoCloseable {

    String actualPath(String path);

    String actualPath(Path path);

    /**
     * Returns current working directory as a {@code Path} object.
     *
     * @return a path representing current directory.
     */
    Path pwd();

    /**
     * Changes current working directory to the given working path.
     *
     * @param path the working path string to which the current working directory is changed
     */
    void cd(String path);

    /**
     * Changes current working directory to the given working path.
     *
     * @param path the working path to which the current working directory is changed
     */
    void cd(Path path);

    void store(String remote, File local);

    void download(String remote, File local);

    void mkdir(Path path);

    void mkdirs(Path path);

    List<CFile> listFiles();

    List<CFile> listFiles(LocalDateTime day, boolean isDirectory);

    List<CFile> listFiles(Predicate<CFile> predicate);

    @Override
    void close();
}
