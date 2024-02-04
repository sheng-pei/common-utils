package ppl.common.utils.http.header;

public final class UnknownHeader implements Header<HeaderValue> {
    private final HeaderName name;
    private final HeaderValue value;

    private UnknownHeader(HeaderName name, HeaderValue value) {
        this.name = name;
        this.value = value;
    }

    public static HeaderCreator getFactory(HeaderName name) {
        return (value, context) -> new UnknownHeader(name, new UnknownHeaderValue(value));
    }

    @Override
    public HeaderName name() {
        return name;
    }

    @Override
    public HeaderValue value() {
        return value;
    }

    @Override
    public HeaderValue knownValue() {
        return null;
    }

    @Override
    public String toCanonicalString() {
        return name.toString() + SEPARATOR + value.toCanonicalString();
    }
}
