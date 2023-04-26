package ppl.common.utils.filesystem;

import java.util.Iterator;

public interface Path extends Comparable<Path>, Iterable<Path> {
    boolean isAbsolute();
    Path getRoot();
    Path getFileName();
    Path getParent();
    int getNameCount();
    Path getName(int index);
    Path subpath(int beginIndex, int endIndex);
    boolean startsWith(Path other);
    boolean startsWith(String other);
    boolean endsWith(Path other);
    boolean endsWith(String other);
    Path normalize();
    Path resolve(Path other);
    Path resolve(String other);
    Path resolveSibling(Path other);
    Path resolveSibling(String other);
    Path relativize(Path other);
    Path toAbsolutePath();
    @Override
    int compareTo(Path o);
    @Override
    int hashCode();
    @Override
    boolean equals(Object other);
    @Override
    Iterator<Path> iterator();
    @Override
    String toString();

    interface Creator {
        Path create(Connection conn, String path);
    }

    interface MoreCreator {
        Path create(Connection conn, String first, String... more);
    }

}
