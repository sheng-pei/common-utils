package ppl.common.utils.os;

import ppl.common.utils.character.ascii.AsciiGroup;
import ppl.common.utils.filesystem.path.Path;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.OpenOption;
import java.nio.file.Paths;

public final class Files {

    private static final Platform CURRENT_OS = OsInfo.instance.getOS();

    private Files() {
    }

    public static File newFile(Path path) {
        String p = path.toString();
        if (CURRENT_OS == Platform.WINDOWS) {
            p = windowsPath(path);
        }
        return new File(p);
    }

    public static InputStream newInputStream(Path path, OpenOption... options) throws IOException {
        String p = path.toString();
        if (CURRENT_OS == Platform.WINDOWS) {
            p = windowsPath(path);
        }
        return java.nio.file.Files.newInputStream(Paths.get(p), options);
    }

    public static OutputStream newOutputStream(Path path, OpenOption... options) throws IOException {
        String p = path.toString();
        if (CURRENT_OS == Platform.WINDOWS) {
            p = windowsPath(path);
        }
        return java.nio.file.Files.newOutputStream(Paths.get(p), options);
    }

    private static String windowsPath(Path path) {
        if (path.isAbsolute()) {
            if (path.getNameCount() == 0) {
                throw new IllegalArgumentException("Path is absolute but no driver.");
            }
            String driver = path.getName(0).toString();
            if (driver.length() > 1 || !AsciiGroup.ALPHA.test(driver.charAt(0))) {
                throw new IllegalArgumentException("Invalid driver.");
            }
            StringBuilder ret = new StringBuilder(driver + ":");
            for (int i = 1; i < path.getNameCount(); i++) {
                String name = path.getName(i).toString();
                if (name.contains("\\")) {
                    throw new IllegalArgumentException("Invalid filename character '\\'.");
                }
                ret.append("\\").append(name);
            }
            return ret.toString();
        } else {
            if (path.getNameCount() == 0) {
                return "";
            }
            StringBuilder ret = new StringBuilder();
            for (int i = 0; i < path.getNameCount(); i++) {
                String name = path.getName(i).toString();
                if (name.contains("\\")) {
                    throw new IllegalArgumentException("Invalid filename character '\\'.");
                }
                ret.append(name).append("\\");
            }
            if (ret.length() > 0) {
                ret.setLength(ret.length() - 1);
            }
            return ret.toString();
        }
    }
}
