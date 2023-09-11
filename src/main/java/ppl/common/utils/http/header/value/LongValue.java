package ppl.common.utils.http.header.value;

import ppl.common.utils.http.header.SingleLineHeaderValue;

import java.util.Objects;

public class LongValue extends SingleLineHeaderValue {

    public static LongValue create(String value) {
        Objects.requireNonNull(value, "Value is required.");
        try {
            return LongValue.create(Long.parseLong(value));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Non-number", e);
        }
    }

    public static LongValue create(Long value) {
        Objects.requireNonNull(value, "Value is required.");
        return new LongValue(value);
    }

    private final long value;

    private LongValue(long value) {
        super("" + value);
        this.value = value;
    }

    public long getValue() {
        return value;
    }

    @Override
    public String toCanonicalString() {
        return value + "";
    }
}
