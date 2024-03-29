package ppl.common.utils.string.substring;

public interface SubstringFinder {
    Substring find(String input);
    Substring find(String input, int start);
    Substring find(String input, int start, int end);
    Substring find(char[] input);
    Substring find(char[] input, int start);
    Substring find(char[] input, int start, int end);
}
