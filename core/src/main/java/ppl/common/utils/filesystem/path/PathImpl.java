package ppl.common.utils.filesystem.path;

import ppl.common.utils.character.ascii.AsciiGroup;
import ppl.common.utils.string.Strings;

import java.nio.file.InvalidPathException;
import java.util.Iterator;
import java.util.Objects;
import java.util.Stack;

class PathImpl implements Path {
    static final PathImpl ROOT = new PathImpl(Path.C_ROOT_DIR);

    private final String path;
    private volatile String[] names;

    PathImpl(String path) {
        this.path = normalizeAndCheck(path);
    }

    @Override
    public boolean isAbsolute() {
        return path.startsWith(Path.C_ROOT_DIR);
    }

    @Override
    public Path getFileName() {
        int count = getNameCount();
        if (count == 0) {
            return null;
        }

        if (!isAbsolute()) {
            count--;
        }
        return new PathImpl(this.names[count]);
    }

    @Override
    public Path getParent() {
        int count = getNameCount();
        if (count == 0 || count == 1 && !isAbsolute()) {
            return null;
        }
        if (count == 1 && isAbsolute()) {
            return ROOT;
        }

        if (!isAbsolute()) {
            count--;
        }
        return new PathImpl(Strings.join(Path.C_SEPARATOR.toString(),
                this.names, 0, count));
    }

    @Override
    public int getNameCount() {
        initNames();
        if (isAbsolute()) {
            return this.names.length - 1;
        }
        return this.names.length;
    }

    @Override
    public Path getName(int index) {
        int count = getNameCount();
        if (index < 0 || index >= count) {
            throw new IllegalArgumentException(Strings.format(
                    "Index '{}' out of range '[0, {})'", index, count));
        }

        if (isAbsolute()) {
            index++;
        }
        return new PathImpl(this.names[index]);
    }

    @Override
    public Path subpath(int beginIndex, int endIndex) {
        if (beginIndex < 0 || endIndex < 0) {
            throw new IllegalArgumentException("Begin and end index must not be negative.");
        }
        if (beginIndex > endIndex) {
            throw new IllegalArgumentException("Begin index is greater than or equal to end index.");
        }
        initNames();
        if (isAbsolute()) {
            beginIndex++;
            endIndex++;
        }
        try {
            return new PathImpl(Strings.join(Path.C_SEPARATOR.toString(),
                    this.names, beginIndex, endIndex - beginIndex));
        } catch (Exception e) {
            throw new IllegalArgumentException("Begin or end index is out of range.");
        }
    }

    @Override
    public boolean startsWith(Path other) {
        if (Objects.isNull(other) || !(other instanceof PathImpl)) {
            return false;
        }
        if (isAbsolute() ^ other.isAbsolute()) {
            return false;
        }
        int thisCount = getNameCount();
        int otherCount = other.getNameCount();
        if (otherCount > thisCount) {
            return false;
        }

        PathImpl o = (PathImpl) other;
        for (int i = 0; i < o.names.length; i++) {
            if (!names[i].equals(o.names[i])) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean startsWith(String other) {
        if (Objects.isNull(other)) {
            return false;
        }
        return startsWith(new PathImpl(other));
    }

    @Override
    public boolean endsWith(Path other) {
        if (Objects.isNull(other) || !(other instanceof PathImpl)) {
            return false;
        }

        boolean thisAbsolute = isAbsolute();
        boolean otherAbsolute = other.isAbsolute();
        if (!thisAbsolute && otherAbsolute) {
            return false;
        }

        int thisCount = getNameCount();
        int otherCount = other.getNameCount();
        if (otherCount > thisCount) {
            return false;
        }

        if (thisAbsolute && otherAbsolute && otherCount < thisCount) {
            return false;
        }

        PathImpl o = (PathImpl) other;
        for (int i = 0; i < otherCount; i++) {
            if (!names[names.length - 1 - i].equals(o.names[o.names.length - 1 - i])) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean endsWith(String other) {
        if (Objects.isNull(other)) {
            return false;
        }
        return endsWith(new PathImpl(other));
    }

    @Override
    public Path resolve(Path other) {
        if (Objects.isNull(other)) {
            throw new NullPointerException();
        }
        if (!(other instanceof PathImpl)) {
            throw new IllegalArgumentException("Mismatch path class.");
        }
        if (other.isAbsolute() || path.isEmpty()) {
            return other;
        }
        PathImpl o = (PathImpl) other;
        if (o.path.isEmpty()) {
            return this;
        }
        if (ROOT == this) {
            return new PathImpl(path + o.path);
        } else {
            return new PathImpl(path + "/" + o.path);
        }
    }

    @Override
    public Path resolve(String other) {
        if (Objects.isNull(other)) {
            throw new NullPointerException();
        }
        return resolve(new PathImpl(other));
    }

    @Override
    public Path resolveSibling(Path other) {
        if (Objects.isNull(other)) {
            throw new NullPointerException();
        }
        if (!(other instanceof PathImpl)) {
            throw new IllegalArgumentException("Mismatch path class.");
        }
        Path parent = getParent();
        if (parent == null) {
            return other;
        }
        return parent.resolve(other);
    }

    @Override
    public Path resolveSibling(String other) {
        if (Objects.isNull(other)) {
            throw new NullPointerException();
        }
        return resolveSibling(new PathImpl(other));
    }

    @Override
    public Path relativize(Path other) {
        if (Objects.isNull(other)) {
            throw new NullPointerException();
        }
        if (!(other instanceof PathImpl)) {
            throw new IllegalArgumentException("Mismatch path class.");
        }
        if (isAbsolute() ^ other.isAbsolute()) {
            throw new IllegalArgumentException("Different absolute type.");
        }

        PathImpl o = (PathImpl) other;
        initNames();
        o.initNames();
        int thisLength = names.length;
        int otherLength = o.names.length;
        int length = Math.min(thisLength, otherLength);
        int i;
        for (i = 0; i < length; i++) {
            if (!names[i].equals(o.names[i])) {
                break;
            }
        }

        StringBuilder builder = new StringBuilder();
        for (int j = 0; j < thisLength - i; j++) {
            builder.append(Path.C_PARENT_DIR).append(Path.C_SEPARATOR);
        }
        for (int j = i; j < otherLength; j++) {
            builder.append(o.names[j]).append(Path.C_SEPARATOR);
        }
        if (builder.length() > 0) {
            builder.setLength(builder.length() - 1);
        }
        return new PathImpl(builder.toString());
    }

    @Override
    public Path normalize() {
        initNames();
        Stack<String> res = new Stack<>();
        for (String name : names) {
            if (name.equals(Path.C_PARENT_DIR)) {
                if (res.isEmpty() || res.peek().equals(Path.C_PARENT_DIR)) {
                    res.push(Path.C_PARENT_DIR);
                } else if (!res.peek().equals(Path.C_PARENT_DIR) && !res.peek().isEmpty()) {
                    res.pop();
                }
            } else if (!name.equals(Path.C_CURRENT_DIR)) {
                res.push(name);
            }
        }
        if (res.size() == 1 && res.peek().isEmpty()) {
            return ROOT;
        }
        return new PathImpl(Strings.join(Path.C_SEPARATOR.toString(), res.toArray(new String[0])));
    }

    private void initNames() {
        if (names == null) {
            this.names = Strings.split(path, "/");
        }
    }

    @Override
    public Iterator<Path> iterator() {
        return new Iterator<Path>() {
            private int index = isAbsolute() ? 1 : 0;

            {
                initNames();
            }

            @Override
            public boolean hasNext() {
                return index < names.length;
            }

            @Override
            public Path next() {
                return new PathImpl(names[index++]);
            }
        };
    }

    @Override
    public int compareTo(Path o) {
        if (!(o instanceof PathImpl)) {
            throw new IllegalArgumentException("The paths are different type Path.");
        }
        PathImpl other = (PathImpl) o;
        return this.path.compareTo(other.path);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PathImpl path = (PathImpl) o;
        return Objects.equals(this.path, path.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }

    @Override
    public String toString() {
        return path;
    }

    static String normalizeAndCheck(String path) {
        StringBuilder builder = new StringBuilder();
        char[] chars = path.toCharArray();
        char b = 0;
        for (char c : chars) {
            if (AsciiGroup.NUL.test(c)) {
                throw new InvalidPathException(path, "Nul character is not allowed.");
            }
            if (c != Path.C_SEPARATOR || b != Path.C_SEPARATOR) {
                b = c;
                builder.append(c);
            }
        }
        if (builder.length() > 1 && builder.charAt(builder.length() - 1) == Path.C_SEPARATOR) {
            builder.setLength(builder.length() - 1);
        }
        return builder.toString();
    }
}
