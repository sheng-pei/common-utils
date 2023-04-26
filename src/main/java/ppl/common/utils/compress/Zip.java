package ppl.common.utils.compress;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Zip implements Archive {

    private static final int BUF_LENGTH = 10240;

    public static Zip create(File zipFile) {
        try {
            return new Zip(zipFile);
        } catch (IOException e) {
            throw new CompressResolveException("Failed to open file: " + zipFile.toString(), e);
        }
    }

    private final ZipFile zipFile;

    private Zip(File zipFile) throws IOException {
        this.zipFile = new ZipFile(zipFile);
    }

    public void decompressTo(File toDir) {
        Path toPath = Paths.get(toDir.toURI());
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            if (!entry.isDirectory()) {
                File toFile = toPath.resolve(entry.getName()).toFile();
                ensureParent(toFile);
                extractEntry(entry, toFile);
            }
        }
    }

    private void ensureParent(File file) {
        File parent = file.getParentFile();
        if (!parent.exists() && !parent.mkdirs()) {
            throw new CompressResolveException("Failed to create some parent directory of " + file);
        }
    }

    private void extractEntry(ZipEntry entry, File toFile) {
        try (InputStream is = new BufferedInputStream(zipFile.getInputStream(entry));
             OutputStream os = new BufferedOutputStream(new FileOutputStream(toFile))) {
            byte[] buf = new byte[BUF_LENGTH];
            int size = is.read(buf);
            while (size >= 0) {
                os.write(buf, 0, size);
                size = is.read(buf);
            }
        } catch (IOException e) {
            throw new CompressResolveException("Failed to extract entry: " + entry.getName() +
                    " from zip: " + zipFile.getName(), e);
        }
    }

    @Override
    public void close() throws CompressResolveException {
        try {
            zipFile.close();
        } catch (IOException e) {
            throw new CompressResolveException("Failed to close zip file: " + zipFile.getName());
        }
    }

}
