package ppl.common.utils.http.header;

import java.util.Arrays;

public enum Line {
    AUTO(""),
    CR("\r"),
    LF("\n"),
    CR_LF("\r\n");

    private final String line;

    Line(String line) {
        this.line = line;
    }

    public String getLine() {
        return line;
    }

    public static Line enumOf(String string) {
        Line[] lines = Line.values();
        return Arrays.stream(lines)
                .filter(l -> l.line.equals(string))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format(
                        "No enum constant: '%s' of enum class: '%s'",
                        string, Line.class.getCanonicalName())));
    }

}
