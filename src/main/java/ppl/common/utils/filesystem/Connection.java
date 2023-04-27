package ppl.common.utils.filesystem;

import java.io.File;
import java.time.Instant;
import java.util.List;

public interface Connection extends AutoCloseable {
    Path pwd();
    void cd(String working);
    void store(String remote, File local);
    //    List<String> listFiles(Instant day);
    List<String> listFiles(Instant day, boolean isDirectory);
    void download(String remote, File local);
}
