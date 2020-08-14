package ppl.common.utils;

public class EscapableSubstringSplitter {

    private final char escape;
    private final char[] delimiter;

    private EscapableSubstringSplitter(String delimiter, char escape) {
        this.escape = escape;
        this.delimiter = delimiter.toCharArray();
    }

    public static EscapableSubstringSplitter compile(String delimiter, char escape) {

        if (StringUtils.isEmpty(delimiter)) {
            throw new IllegalArgumentException(StringUtils.format("Delimiter is empty or null"));
        }

        if (delimiter.indexOf(escape) > 0) {
            throw new IllegalArgumentException(StringUtils.format("Delimiter: {} contains escape character: {}", delimiter, escape));
        }

        return new EscapableSubstringSplitter(delimiter, escape);
    }

//    public EscapableSundaySubstringFinder matcher(String input) {
//        return new EscapableSundaySubstringFinder(this, input.toCharArray());
//    }

    public int firstUnescape(char[] input, int pos, int end) {
        return StringUtils.indexOfNot(escape, input, pos, end);
    }

    public int length() {
        return delimiter.length;
    }

}
