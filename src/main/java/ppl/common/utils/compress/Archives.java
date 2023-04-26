package ppl.common.utils.compress;

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
        switch (format) {
            case "zip":
                return Zip.create(path.toFile());
            case "rar":
                return Rar.create(path.toFile());
            case "7z":
                return SvnZip.create(path.toFile());
            default:
                throw new CompressResolveException("Unknown compress format: " + format);
        }
    }

}
