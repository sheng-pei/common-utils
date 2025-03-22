package ppl.common.utils.ext;

import ppl.common.utils.Arrays;
import ppl.common.utils.pair.Pair;
import ppl.common.utils.string.Strings;

import java.util.ArrayList;
import java.util.List;

public class Name {

    private final String[] parts;
    private final int baseIndex;

    public Name(String base) {
        this(new String[] {base}, 0);
    }

    public Name(String[] parts, int baseIndex) {
        if (Arrays.isEmpty(parts) || parts.length > 2) {
            throw new IllegalArgumentException("Error extension partition.");
        }
        if (baseIndex < 0 || baseIndex > parts.length) {
            throw new IndexOutOfBoundsException();
        }
        this.parts = parts.clone();
        this.baseIndex = baseIndex;
    }

    public String getBase() {
        return parts[baseIndex];
    }

    public Pair<List<String>, Integer> baseReplaced(List<String> baseParts) {
        List<String> ret = new ArrayList<>();
        for (int i = 0; i < parts.length; i++) {
            if (i != baseIndex) {
                ret.add(parts[i]);
            } else {
                ret.addAll(baseParts);
            }
        }
        return Pair.create(ret, baseIndex);
    }

    public Pair<List<String>, Integer> baseReplaced(String[] baseParts) {
        return baseReplaced(Arrays.asList(baseParts));
    }

    @Override
    public String toString() {
        return Strings.join("", parts);
    }
}
