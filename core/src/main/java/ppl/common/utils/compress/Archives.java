package ppl.common.utils.compress;

import ppl.common.utils.compress.impl.*;
import ppl.common.utils.exception.UnreachableCodeException;

import java.io.InputStream;
import java.nio.file.Path;

public final class Archives {
    private Archives() {}

    public static Archive open(Path path) {
        String fileName = path.getFileName().toString();
        String format = "";
        int i = fileName.lastIndexOf(".");
        if (i >= 0) {
            format = fileName.substring(i+1);
        }
        return open(path, format);
    }

    public static Archive open(Path path, String format) {
        Format f = Format.enumOf(format);
        switch (f) {
            case ZIP:
                return Zip.create(path.toFile());
            case RAR:
                return Rar.create(path.toFile());
            case SEVEN_ZIP:
                return SvnZip.create(path.toFile());
            case TAR:
                return Tar.create(path.toFile());
        }
        throw new UnreachableCodeException();
    }

    public static Archive open(InputStream is, String format) {
        Format f = Format.enumOf(format);
        if (!f.isStreamSupport()) {
            throw new IllegalArgumentException("InputStream is not support with: " + f.name());
        }

        switch (f) {
            case ZIP:
                return InputStreamZip.create(is);
            case TAR:
                return Tar.create(is);
        }
        throw new UnreachableCodeException();
    }

}
