package ppl.common.utils.filesystem;

import java.util.Iterator;

public interface Path extends Comparable<Path>, Iterable<Path> {

    /**
     * Tells whether this path is absolute.
     *
     * <p> An absolute path is complete in that it doesn't need to be combined
     * with other path information in order to locate a file.
     *
     * @return  {@code true} if, and only if, this path is absolute
     */
    boolean isAbsolute();

    /**
     * Returns the name of the file or directory denoted by this path as a
     * {@code Path} object. The file name is the <em>farthest</em> element from
     * the root in the directory hierarchy.
     *
     * @return  a path representing the name of the file or directory, or
     *          {@code null} if this path has zero elements
     */
    Path getFileName();

    /**
     * Returns the <em>parent path</em>, or {@code null} if this path does not
     * have a parent.
     *
     * <p>If this path has one or more elements, and root component, then
     * the parent of this path object consists of this path's root component
     * and each element in the path except for the <em>farthest</em> from
     * the root in the directory hierarchy. This method does not eliminate
     * special names such as "." and ".." that may be used in some implementations.
     * This method may be used with the {@link #normalize normalize} method,
     * to eliminate redundant names.</p>
     *
     * <p>If this path has one or more elements, and no root component, then
     * this method is equivalent to evaluating the expression:
     * <blockquote><pre>
     * subpath(0,&nbsp;getNameCount()-1);
     * </pre></blockquote></p>
     *
     * <p>Otherwise returns {@code null}.</p>
     *
     * @return a path representing the path's parent
     */
    Path getParent();

    /**
     * Returns the number of name elements in this path.
     *
     * @return the number of elements in this path, or {@code 0} if this path
     *         only represents a root component
     */
    int getNameCount();

    /**
     * Returns a name element of this path as a relative {@code Path} object.
     * <p>The {@code index} parameter is the index of the name element to return.
     * The element that is <em>closest</em> to the root in the directory hierarchy
     * has index {@code 0}. The element that is <em>farthest</em> from the root
     * has index {@link #getNameCount count}{@code -1}.
     * @param  index
     *         the index of the element
     * @return the name element
     */
    Path getName(int index);

    /**
     * Returns a relative {@code Path} that is a subsequence of the name elements of this path.
     * <p>The {@code beginIndex} and {@code endIndex} parameters specify the
     * subsequence of name elements. The name that is <em>closest</em> to the root
     * in the directory hierarchy has index {@code 0}. The name that is
     * <em>farthest</em> from the root has index {@link #getNameCount
     * count}{@code -1}. The returned {@code Path} object has the name elements
     * that begin at {@code beginIndex} and extend to the element at index {@code
     * endIndex-1}.</p>
     * @param beginIndex
     *        the index of the first element, inclusive
     * @param endIndex
     *        the index of the last element, exclusive
     * @return a new {@code Path} object that is a subsequence of the name
     *         elements in this {@code Path}
     */
    Path subpath(int beginIndex, int endIndex);

    /**
     * Tests if this path starts with the given path.
     * <p><ol>
     *     <li>If this path and the given path both are absolute or not absolute then
     *     this path starts with the given path if this path starts with the same name
     *     elements as the given path and the given path's name elements are less than
     *     this path's.</li>
     *     <li>Otherwise this path does not starts with the given path.</li>
     * </ol></p>
     * @param  other
     *         the given path
     * @return {@code true} if this path ends with the given path; otherwise
     *         {@code false}
     */
    boolean startsWith(Path other);

    /**
     * Tests if this path starts with a path, constructed by converting then given path string,
     * in exactly the manner specified by the {@link #startsWith(Path) startsWith(Path)} method.
     * @param  other
     *         the given path string
     * @return {@code true} if this path starts with the given path; otherwise {@code false}
     */
    boolean startsWith(String other);

    /**
     * Tests if this path ends with the given path.
     * <p><ol>
     *     <li>If the given path is an absolute path then this path ends with
     *     the given path if this path is an absolute path and the corresponding
     *     elements of both paths are equal.</li>
     *     <li>If the given path is not an absolute path then this path ends with
     *     the given path if the given path has <em>N</em> elements and this path
     *     has <em>N</em> or more elements and the last <em>N</em> elements of
     *     each path, starting at the element farthest from the root, are equal.</li>
     * </ol></p>
     * @param  other
     *         the given path
     * @return {@code true} if this path ends with the given path; otherwise
     *         {@code false}
     */
    boolean endsWith(Path other);

    /**
     * Tests if this path ends with a path, constructed by converting the given path string,
     * in exactly the manner specified by the {@link #endsWith(Path) endsWith(Path)} method.
     * @param other
     *        the given path string.
     * @return {@code true} if this path ends with the given path; otherwise
     *         {@code false}
     */
    boolean endsWith(String other);

    /**
     * Resolves the given path against this path.
     * <p><ol>
     * <li>If the {@code other} parameter is an {@link #isAbsolute() absolute}
     * path then this method trivially returns {@code other}.</li>
     * <li>If {@code other} is an <i>empty path</i> then this method trivially returns this path.</li>
     * <li>If this path is an <i>empty path</i> then this method trivially returns the {@code other} path.</li>
     * <li>Otherwise this method considers this path to be a directory and resolves
     * the given path against this path. This method <em>joins</em> the given path
     * to this path and returns a resulting path that {@link #endsWith ends} with the given path.</li>
     * </ol></p>
     *
     * @param   other
     *          the path to resolve against this path
     *
     * @return  the resulting path
     *
     * @see #relativize
     */
    Path resolve(Path other);

    /**
     * Converts a given path string to a {@code path} and resolves it against this path
     * in exactly the manner specified by the
     * {@link #resolve(Path) resolve} method.
     *
     * @param  other
     *         the path to resolve against this path
     *
     * @return the resulting path
     */
    Path resolve(String other);

    /**
     * Resolves the given path against this path's {@link #getParent parent}
     * path. <p><ol>
     * <li>If this path does not have a parent path, then this method
     * returns {@code other}.</li>
     * <li>Otherwise this method resolves the given path against
     * this path's {@link #getParent parent} path in exactly the manner
     * specified by the
     * {@link #resolve(Path) resolve} method.</li>
     * </ol></p>
     * @param   other
     *          the path to resolve against this path's parent
     *
     * @return  the resulting path
     *
     * @see #resolve(Path)
     */
    Path resolveSibling(Path other);

    /**
     * Converts a given path string to a {@code Path} and resolves it against
     * this path's {@link #getParent parent} path in exactly the manner
     * specified by the
     * {@link #resolveSibling(Path) resolveSibling} method.
     *
     * @param   other
     *          the path string to resolve against this path's parent
     *
     * @return  the resulting path
     */
    Path resolveSibling(String other);

    /**
     * Constructs a relative path between this path and a given path.
     *
     * <p>Relativization is the inverse of {@link #resolve(Path) resolution}.
     * This method attempts to construct a {@link #isAbsolute relative} path
     * that when {@link #resolve(Path) resolved} against this path, yields a
     * path that locates the same file as the given path. A relative path cannot be
     * constructed if only one of the paths have a root component. If this path
     * and the given path are {@link #equals equal} then an <i>empty path</i> is
     * returned.</p>
     *
     * @param   other
     *          the path to relativize against this path
     *
     * @return  the resulting relative path, or an empty path if both paths are
     *          equal
     */
    Path relativize(Path other);

    /**
     * Returns a path that is this path with redundant name elements eliminated.
     *
     * <p> The "{@link FileSystem#C_CURRENT_DIR}" and "{@link FileSystem#C_PARENT_DIR}"
     * are special names used to indicate the current directory and parent directory.
     * All occurrences of "{@link FileSystem#C_CURRENT_DIR}" are considered redundant.
     * If a "{@link FileSystem#C_PARENT_DIR}" is preceded by a non-"{@link
     * FileSystem#C_PARENT_DIR}" name then both names are considered redundant
     * (the process to identify such names is repeated until it is no longer applicable).
     *
     * @return  the resulting path or this path if it does not contain
     *          redundant name elements; an empty path is returned if this path
     *          does have a root component and all name elements are redundant
     */
    Path normalize();

    /**
     * Compares two abstract paths lexicographically.
     *
     * <p> This method may not be used to compare paths that are associated
     * with different file system providers.
     *
     * @param   other  the path compared to this path.
     *
     * @return  zero if the argument is {@link #equals equal} to this path, a
     *          value less than zero if this path is lexicographically less than
     *          the argument, or a value greater than zero if this path is
     *          lexicographically greater than the argument
     */
    @Override
    int compareTo(Path other);

    @Override
    int hashCode();

    /**
     * Tests this path for equality with the given object.
     *
     * <p> If the given object is not a Path, or is a different type Path, then
     * this method returns {@code false}.
     *
     * @param   other
     *          the object to which this object is to be compared
     *
     * @return  {@code true} if, and only if, the given object is a {@code Path}
     *          that is identical to this {@code Path}
     */
    @Override
    boolean equals(Object other);

    /**
     * Returns an iterator over the name elements of this path.
     *
     * <p> The first element returned by the iterator represents the name
     * element that is closest to the root in the directory hierarchy, the
     * second element is the next closest, and so on. The last element returned
     * is the name of the file or directory denoted by this path.
     *
     * @return  an iterator over the name elements of this path.
     */
    @Override
    Iterator<Path> iterator();

    /**
     * Returns the string representation of this path.
     *
     * <p> the path string returned by this method may differ from the original String
     * used to create the path.
     *
     * <p> The returned path string uses the default name {@link FileSystem#C_SEPARATOR
     * separator} to separate names in the path.
     *
     * @return  the string representation of this path
     */
    @Override
    String toString();

    interface Creator {
        Path create(String path);
    }

    interface MoreCreator {
        Path create(String first, String... more);
    }

}
