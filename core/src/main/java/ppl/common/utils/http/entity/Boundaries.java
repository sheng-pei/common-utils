package ppl.common.utils.http.entity;

import ppl.common.utils.string.Strings;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Boundaries {

    private static final int DEFAULT_COUNT = 40;
    private static final byte[] CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-_"
            .getBytes(StandardCharsets.US_ASCII);

    private final int count;
    private transient final int maxBoundaryCount;
    private transient final Random random;
    private transient final Set<String> used = new HashSet<>();

    Boundaries() {
        this(DEFAULT_COUNT);
    }

    Boundaries(int count) {
        if (count <= 0 || count > 70) {
            throw new IllegalArgumentException("Boundary size must be 1 ~ 70.");
        }
        Random random = new Random(System.currentTimeMillis());
        this.count = count;
        this.random = random;
        int maxBits = Integer.bitCount(Integer.MAX_VALUE);
        int bits = maxBits - Integer.numberOfLeadingZeros(CHARS.length);
        this.maxBoundaryCount = 1 << Math.min(bits * count, maxBits - 1);
    }

    public String getBoundary() {
        String ret;
        do {
            if (used.size() == maxBoundaryCount) {
                throw new IllegalStateException(Strings.format("Beyond {} boundaries used.", maxBoundaryCount));
            }

            int[] ints = random.ints(count, 0, CHARS.length)
                    .map(i -> CHARS[i])
                    .toArray();
            ret = new String(ints, 0, ints.length);
        } while (!used.add(ret));
        return ret;
    }

}
