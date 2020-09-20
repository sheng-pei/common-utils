package ppl.common.utils;

public interface SubstringFinder {
    String getPattern();
    Substring find(String input);
    Substring find(String input, int start);
    Substring find(String input, int start, int end);
    Substring find(char[] input, int start, int end);
//    int find(char[] input);
//    int find(char[] input, int start);
//    int find(char[] input, int start, int end);
}
