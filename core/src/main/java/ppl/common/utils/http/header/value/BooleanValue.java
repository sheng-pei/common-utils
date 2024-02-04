package ppl.common.utils.http.header.value;

import ppl.common.utils.http.header.SingleLineHeaderValue;

import java.util.Objects;

public class BooleanValue extends SingleLineHeaderValue {

    public static BooleanValue create(String value) {
        Objects.requireNonNull(value, "Value is required.");
        try {
            return BooleanValue.create(Boolean.parseBoolean(value));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Non-boolean", e);
        }
    }

    public static BooleanValue create(boolean value) {
        return new BooleanValue(value);
    }

    private final boolean value;

    private BooleanValue(boolean value) {
        super("" + value);
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

    @Override
    public String toCanonicalString() {
        return value + "";
    }
}
