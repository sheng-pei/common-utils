package ppl.common.utils.compress;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import ppl.common.utils.IOUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class Tar implements Archive {

    private static final int BUF_LENGTH = 10240;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    private final String path;
    private final TarArchiveInputStream is;

    private Tar(File file) throws IOException {
        this.path = file.getAbsolutePath();
        this.is = new TarArchiveInputStream(Files.newInputStream(file.toPath()));
    }

    private void putIntoTarStream(TarArchiveOutputStream tarStream, File file, String name) {
        TarArchiveEntry tarEntry = new TarArchiveEntry(name);
        tarEntry.setSize(file.length());
        openArchiveEntry(tarStream, tarEntry);
        copyToTar(file, tarStream);
        closeArchiveEntry(tarStream);
    }

    private void openArchiveEntry(TarArchiveOutputStream tarOutputStream, ArchiveEntry entry) {
        try {
            tarOutputStream.putArchiveEntry(entry);
        } catch (IOException e) {
            throw new RuntimeException("File operation exception", e);
        }
    }

    private void closeArchiveEntry(TarArchiveOutputStream tarOutputStream) {
        try {
            tarOutputStream.closeArchiveEntry();
        } catch (IOException e) {
            throw new RuntimeException("File operation exception", e);
        }
    }

    private void copyToTar(File file, TarArchiveOutputStream tarOutputStream) {
        InputStream is = null;
        try {
            is = new BufferedInputStream(Files.newInputStream(file.toPath()));
            IOUtils.copy(is, tarOutputStream);
        } catch (IOException e) {
            throw new RuntimeException("file operation exception", e);
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    @Override
    public void decompressTo(File toDir) {
        Path toPath = Paths.get(toDir.toURI());
        try {
            TarArchiveEntry entry = is.getNextTarEntry();
            while (entry != null) {
                Path save = toPath.resolve(entry.getName());
                if (entry.isDirectory()) {
                    ensureDirectory(save);
                } else {
                    try (OutputStream os = Files.newOutputStream(save)) {
                        copy(is, os);
                    }
                }
                entry = is.getNextTarEntry();
            }
        } catch (IOException e) {
            throw new CompressResolveException("Couldn't extract file from compressed file: " + this.path, e);
        }
    }

    private void ensureDirectory(Path path) {
        File dir = path.toFile();
        if (!dir.exists() && !dir.mkdirs()) {
            throw new CompressResolveException("Failed to create directory: " + dir);
        }
    }

    private void copy(InputStream is, OutputStream os) throws IOException {
        byte[] buf = new byte[BUF_LENGTH];
        int size = is.read(buf);
        while (size >= 0) {
            os.write(buf, 0, size);
            size = is.read(buf);
        }
    }

    @Override
    public void close() throws CompressResolveException {
        try {
            is.close();
        } catch (IOException e) {
            throw new CompressResolveException("Failed to close tar file: " + this.path);
        }
    }

    public static Tar create(File tarFile) {
        try {
            return new Tar(tarFile);
        } catch (IOException e) {
            throw new CompressResolveException("Failed to open file: " + tarFile, e);
        }
    }
}
