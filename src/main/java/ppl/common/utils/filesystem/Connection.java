package ppl.common.utils.filesystem;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.List;

/**
 * A connection object to a {@link FileSystem filesystem}
 *
 * <p>This connection is not guarantee to be thread-safe.
 * Please don't publish a connection to two or more thread
 * at the same time.</p>
 */
public interface Connection extends AutoCloseable {

    /**
     * Returns current working directory as a {@code Path} object.
     *
     * @return a path representing current directory.
     */
    Path pwd() throws IOException;

    /**
     * Changes current working directory to the given working path.
     * @param working
     *        the working path string to which the current working directory is changed
     */
    void cd(String working) throws IOException;

    void store(String remote, File local) throws IOException;
    //    List<String> listFiles(Instant day);
    List<String> listFiles(Instant day, boolean isDirectory) throws IOException;
    void download(String remote, File local) throws IOException;
}
