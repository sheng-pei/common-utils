package ppl.common.utils.http.header.known;

import ppl.common.utils.http.Name;
import ppl.common.utils.http.header.Header;
import ppl.common.utils.http.header.HeaderValue;
import ppl.common.utils.http.header.value.StringValue;

@Name("Referer")
public class Referer implements Header<StringValue> {

    private final StringValue value;

    private Referer(String value) {
        this.value = StringValue.create(value);
    }

    @Override
    public HeaderValue value() {
        return value;
    }

    @Override
    public StringValue knownValue() {
        return value;
    }
}
