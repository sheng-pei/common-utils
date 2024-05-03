package ppl.common.utils.test.point;

public interface Point {
    /**
     * Comparing two points. If this point is after the given point, this method
     * will return true. <br>
     * For any points {@code x} and {@code y}, {@code x.after(y)} should not
     * throw {@code UnsupportedOperationException} if {@code x} could be compared
     * with {@code y}.
     * <p>
     * The {@code after} method implements an equivalence relation on non-null points:
     * <ul>
     * <li>It is <i>reflexive</i>: for any non-null point {@code x}, {@code x}
     *     could be compared with itself.
     * <li>It is <i>symmetric</i>: for any non-null points {@code x} and {@code y},
     *     {@code x} could be compared with {@code y} if and only if {@code y} could
     *     be compared with {@code x}.
     * <li>It is <i>transitive</i>: for any non-null points {@code x}, {@code y}, and
     *     {@code z}, if {@code x} could be compared with {@code y} and {@code y} could
     *     be compared with {@code z}, then {@code x} could be compared with {@code z}.
     * <li>It is <i>consistent</i>: for any non-null points {@code x} and {@code y},
     *     multiple invocations of {@code x.after(y)} consistently return {@code true},
     *     consistently return {@code false} or consistently throw
     *     {@code UnsupportedOperationException}, provided no information used in
     *     {@code after} comparisons on the points is modified.
     * <li>For any non-null point {@code x}, {@code x.after(null)} should throw
     *     {@code UnsupportedOperationException}.
     * </ul>
     * <p>
     * @param point the reference point with which to be compared.
     * @return true if this point is after the given point, false otherwise.
     * @throws UnsupportedOperationException if the given point could not
     * be compared with this point.
     */
    boolean after(Point point) throws UnsupportedOperationException;
}
