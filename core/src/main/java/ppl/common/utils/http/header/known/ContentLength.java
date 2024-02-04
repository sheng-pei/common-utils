package ppl.common.utils.http.header.known;

import ppl.common.utils.http.Name;
import ppl.common.utils.http.header.Header;
import ppl.common.utils.http.header.HeaderValue;
import ppl.common.utils.http.header.value.LongValue;

@Name("Content-Length")
public class ContentLength implements Header<LongValue> {

    private final LongValue value;

    private ContentLength(String value) {
        LongValue v = LongValue.create(value);
        if (v.getValue() < 0) {
            throw new IllegalArgumentException("Length couldn't be negative.");
        }
        this.value = v;
    }

    public ContentLength(long value) {
        if (value < 0) {
            throw new IllegalArgumentException("Length couldn't be negative.");
        }
        this.value = LongValue.create(value);
    }

    @Override
    public HeaderValue value() {
        return value;
    }

    @Override
    public LongValue knownValue() {
        return value;
    }
}
