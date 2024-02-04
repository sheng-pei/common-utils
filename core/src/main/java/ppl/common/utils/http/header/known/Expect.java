package ppl.common.utils.http.header.known;

import ppl.common.utils.http.Name;
import ppl.common.utils.http.header.Header;
import ppl.common.utils.http.header.HeaderValue;
import ppl.common.utils.http.header.value.StringValue;

@Name("Expect")
public class Expect implements Header<StringValue> {
    private final StringValue value;

    private Expect(String value) {
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
