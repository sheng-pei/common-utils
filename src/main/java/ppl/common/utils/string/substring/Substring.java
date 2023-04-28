package ppl.common.utils.string.substring;

/**
 * An object that is a subsequence of a string.
 *
 * <p>This substring begins at {@link #start() startIndex}, inclusive, and extends to {@link #end() endIndex},
 * exclusive, of the source string. The {@link #start() startIndex} must not be greater than the {@link #end()
 * endIndex}.</p>
 *
 * <p>The {@link #length() length} of this substring is the number of the characters of the subsequence. If
 * there is no character in this substring then this is empty.</p>
 *
 */
public interface Substring {
    boolean isEmpty();
    int start();
    int end();
    int length();
    String string();
    String string(int offset);
    String string(int offset, int length);
}
