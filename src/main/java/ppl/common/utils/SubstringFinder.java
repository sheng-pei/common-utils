package ppl.common.utils;

public interface SubstringFinder {
    String getPattern();
    Substring find(String input);
    Substring find(String input, int start);
    Substring find(String input, int start, int end);
}
