package ppl.common.utils.compress.impl;

import org.slf4j.Logger;
import ppl.common.utils.compress.Archive;
import ppl.common.utils.compress.ArchiveException;
import ppl.common.utils.os.OsInfo;
import ppl.common.utils.os.Platform;
import ppl.common.utils.pair.Pair;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractCCmprss implements Archive {
    private final Logger logger;

    protected AbstractCCmprss(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void decompressTo(File toDir) {
        Path toPath = Paths.get(toDir.toURI());
        try {
            List<Pair<Path, Path>> symlink = new ArrayList<>();
            while (next()) {
                String name = entryName();
                Path save = toPath.resolve(name);
                if (isSymbolicLink()) {
                    if (supportUnixSymlink()) {
                        Path target = Paths.get(linkName());
                        if (requiredRelative(target) && targetInSameZip(target)) {
                            symlink.add(Pair.create(save, target));
                        } else {
                            logger.warn("Unknown symbolic link. Ignore: " + name);
                        }
                    } else {
                        logger.warn("Symbolic link is not supported. Ignore: " + name);
                    }
                } else if (isDirectory()) {
                    ensureDirectory(save);
                } else {
                    ensureDirectory(save.getParent());
                    try (OutputStream os = Files.newOutputStream(save)) {
                        copy(os);
                    }
                }
            }

            for (Pair<Path, Path> p : symlink) {
                Files.createSymbolicLink(p.getFirst(), p.getSecond());
            }
        } catch (IOException e) {
            throw new ArchiveException("Couldn't extract file.", e);
        }
    }

    protected abstract boolean next() throws IOException;

    protected abstract String entryName();

    protected abstract boolean isDirectory();

    protected abstract void copy(OutputStream os) throws IOException;

    protected boolean isSymbolicLink() {
        return false;
    }

    protected abstract String linkName() throws IOException;

    private boolean supportUnixSymlink() {
        if (OsInfo.instance.getOS() == Platform.WINDOWS ||
                OsInfo.instance.getOS() == Platform.OTHERS) {
            logger.warn("Unix symlink is not supported in current os, ignore!");
            return false;
        }
        return true;
    }

    private boolean requiredRelative(Path target) {
        if (target.isAbsolute()) {
            logger.warn("The target of unix symlink entry must be relative, ignore!");
            return false;
        }
        return true;
    }

    private boolean targetInSameZip(Path target) throws IOException {
        Path link = Paths.get(linkName());
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
            throw new ArchiveException("Failed to create directory: " + dir);
        }
    }
}
