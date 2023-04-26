package ppl.common.utils.filesystem;

import ppl.common.utils.string.Strings;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BasePath implements Path {
    private final Connection connection;
    private final String path;
    private volatile String[] names;

    public static BasePath get(Connection connection, String first, String... more) {
        List<String> list = new ArrayList<>();
        if (!first.isEmpty()) {
            list.add(first);
        }
        for (String m : more) {
            if (m != null && !m.isEmpty()) {
                list.add(m);
            }
        }
        if (list.isEmpty()) {
            return new BasePath(connection, "");
        } else {
            return new BasePath(connection, String.join(FileSystem.C_SEPARATOR.toString(), list));
        }
    }

    public BasePath(Connection connection, String path) {
        this.connection = connection;
        this.path = normalizeAndCheck(path);
    }

    private static String normalizeAndCheck(String path) {
        StringBuilder builder = new StringBuilder();
        int l = path.length();
        char[] chars = path.toCharArray();
        char b = 0;
        for (char c : chars) {
            if (c == 0) {
                throw new InvalidPathException(Strings.format("Nul character is not allowed: {}", path));
            }
            if (c != FileSystem.C_SEPARATOR || b != FileSystem.C_SEPARATOR) {
                b = c;
                builder.append(c);
            }
        }
        return builder.toString();
    }

    @Override
    public boolean isAbsolute() {
        return path.startsWith(FileSystem.C_ROOT_DIR);
    }

    @Override
    public Path getRoot() {
        return connection.root();
    }

    @Override
    public Path getFileName() {
        initNames();
        return new BasePath(this.connection, this.names[this.getNameCount() - 1]);
    }

    @Override
    public Path getParent() {
        initNames();
//        return BasePath.get(connection, );
        return null;
    }

    @Override
    public int getNameCount() {
        initNames();
        return this.names.length;
    }

    @Override
    public Path getName(int index) {
        return null;
    }

    @Override
    public Path subpath(int beginIndex, int endIndex) {
        return null;
    }

    @Override
    public boolean startsWith(Path other) {
        return false;
    }

    @Override
    public boolean startsWith(String other) {
        return false;
    }

    @Override
    public boolean endsWith(Path other) {
        return false;
    }

    @Override
    public boolean endsWith(String other) {
        return false;
    }

    @Override
    public Path normalize() {
        return this;
    }

    @Override
    public Path resolve(Path other) {
        return null;
    }

    @Override
    public Path resolve(String other) {
        return null;
    }

    @Override
    public Path resolveSibling(Path other) {
        return null;
    }

    @Override
    public Path resolveSibling(String other) {
        return null;
    }

    @Override
    public Path relativize(Path other) {
        return null;
    }

    @Override
    public Path toAbsolutePath() {
        return null;
    }

    @Override
    public Iterator<Path> iterator() {
        return null;
    }

    private void initNames() {
        if (names == null) {
            if (path.isEmpty()) {
                this.names = new String[0];
            } else {
                this.names = path.split(FileSystem.C_SEPARATOR.toString());
            }
        }
    }

    @Override
    public int compareTo(Path o) {
//        if (this.fileSystem != o.getFileSystem() || !(o instanceof BasePath)) {
//            throw new IllegalArgumentException("The paths are associated with different filesystem.");
//        }
//        BasePath other = (BasePath) o;
//        return this.path.compareTo(other.path);
        return 0;
    }

    @Override
    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        BasePath path = (BasePath) o;
//        return Objects.equals(fileSystem, path.fileSystem) && Objects.equals(this.path, path.path);
        return false;
    }

    @Override
    public int hashCode() {
//        return Objects.hash(fileSystem, path);
        return 0;
    }

    @Override
    public String toString() {
        return path;
    }
}
