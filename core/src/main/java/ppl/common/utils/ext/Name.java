package ppl.common.utils.ext;

import ppl.common.utils.Arrays;
import ppl.common.utils.string.Strings;

public class Name {

    private final String[] parts;
    private final int baseIndex;

    public Name(String base) {
        this(new String[] {base}, 0);
    }

    public Name(String[] parts, int baseIndex) {
        if (Arrays.isEmpty(parts) || parts.length > 2) {
            throw new IllegalArgumentException("");
        }
        if (baseIndex < 0 || baseIndex > parts.length) {
            throw new IndexOutOfBoundsException("");
        }
        this.parts = parts.clone();
        this.baseIndex = baseIndex;
    }

    public String getBase() {
        return parts[baseIndex];
    }

    @Override
    public String toString() {
        return Strings.join("", parts);
    }
}
