package ppl.common.utils.http.header.internal;

import ppl.common.utils.http.Name;
import ppl.common.utils.http.header.Header;
import ppl.common.utils.http.header.HeaderValue;
import ppl.common.utils.http.header.value.BooleanValue;

@Name("(No-Redirect)")
public class NoRedirect implements Header<BooleanValue> {
    private final BooleanValue value;

    private NoRedirect(String value) {
        this.value = BooleanValue.create(value);
    }

    public NoRedirect(boolean value) {
        this.value = BooleanValue.create(value);
    }

    @Override
    public HeaderValue value() {
        return value;
    }

    @Override
    public BooleanValue knownValue() {
        return value;
    }
}
