package ppl.common.utils.http.header.value;

import ppl.common.utils.http.header.SingleLineHeaderValue;

import java.util.Objects;

public class StringValue extends SingleLineHeaderValue {
    public static StringValue create(String value) {
        Objects.requireNonNull(value, "Value is required.");
        return new StringValue(value);
    }

    private final String value;

    private StringValue(String value) {
        super(value);
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toCanonicalString() {
        return value;
    }
}
