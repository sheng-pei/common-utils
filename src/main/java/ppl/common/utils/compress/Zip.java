package ppl.common.utils.compress;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppl.common.utils.os.OsInfo;
import ppl.common.utils.os.Platform;
import ppl.common.utils.string.kvpair.Pair;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Zip implements Archive {

    private static final Logger logger = LoggerFactory.getLogger(Zip.class);
    private static final int BUF_LENGTH = 10240;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    public static Zip create(File zipFile) {
        try {
            return new Zip(zipFile, DEFAULT_CHARSET);
        } catch (IOException e) {
            throw new CompressResolveException("Failed to open file: " + zipFile, e);
        }
    }

    private final String path;
    private final ZipArchiveInputStream is;
    private final Charset charset;

    private Zip(File zipFile, Charset charset) throws IOException {
        this.charset = charset;
        this.path = zipFile.getAbsolutePath();
        this.is = new ZipArchiveInputStream(Files.newInputStream(zipFile.toPath()), charset.name());
    }

    public void decompressTo(File toDir) {
        Path toPath = Paths.get(toDir.toURI());
        try {
            List<Pair<Path, Path>> symlink = new ArrayList<>();
            ZipArchiveEntry e = is.getNextZipEntry();
            while (e != null) {
                Path save = toPath.resolve(e.getName());
                if (e.isUnixSymlink() && supportUnixSymlink()) {
                    Path target = target();
                    if (requiredRelative(target) && targetInSameZip(e, target)) {
                        symlink.add(Pair.create(save, target));
                    }
                } else if (e.isDirectory()) {
                    ensureDirectory(save);
                } else {
                    try (OutputStream os = Files.newOutputStream(save)) {
                        copy(is, os);
                    }
                }
                e = is.getNextZipEntry();
            }

            for (Pair<Path, Path> p : symlink) {
                Files.createSymbolicLink(p.getFirst(), p.getSecond());
            }
        } catch (IOException e) {
            throw new CompressResolveException("Couldn't extract file from compressed file: " + this.path, e);
        }
    }

    private boolean supportUnixSymlink() {
        if (OsInfo.instance.getOS() == Platform.WINDOWS ||
                OsInfo.instance.getOS() == Platform.OTHERS) {
            logger.warn("Unix symlink is not supported, ignore!");
            return false;
        }
        return true;
    }

    private Path target() throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        copy(is, os);
        os.flush();
        String content = os.toString(charset.name());
        return Paths.get(content);
    }

    private boolean requiredRelative(Path target) {
        if (target.isAbsolute()) {
            logger.warn("The target of unix symlink entry must be relative, ignore!");
            return false;
        }
        return true;
    }

    private boolean targetInSameZip(ZipArchiveEntry e, Path target) {
        Path link = Paths.get(e.getName());
        Path current = link.getParent();
        target = current.resolve(target);
        if (target.normalize()
                .getName(0)
                .toString().equals("..")) {
            logger.warn("The target of unix symlink entry must be in the same zip, ignore!");
            return false;
        }
        return true;
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
            throw new CompressResolveException("Failed to close zip file: " + path);
        }
    }

    public static void main(String[] args) {
        try (Zip zip = Zip.create(new File(args[0]))) {
            zip.decompressTo(new File(args[1]));
        }
    }

}
