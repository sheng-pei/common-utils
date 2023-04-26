package ppl.common.utils.compress;

import java.io.File;

public interface Archive extends AutoCloseable {
    void decompressTo(File toDir);
    void close() throws CompressResolveException;
}
