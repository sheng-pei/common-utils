package ppl.common.utils.http.header;

public class UnknownHeaderValue implements HeaderValue {
    private final String value;

    public static HeaderValue create(String value) {
        return new UnknownHeaderValue(value);
    }

    protected UnknownHeaderValue(String value) {
        this.value = value;
    }

    @Override
    public String toCanonicalString() {
        return value;
    }
}
