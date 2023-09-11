package ppl.common.utils.http.header.known;

import ppl.common.utils.http.Name;
import ppl.common.utils.http.header.Header;
import ppl.common.utils.http.header.HeaderValue;
import ppl.common.utils.http.header.value.software.Software;

@Name("User-Agent")
public class UserAgent implements Header<Software> {

    private final Software value;

    public UserAgent(String value) {
        this.value = Software.create(value);
    }

    public UserAgent(Software software) {
        this.value = software;
    }

    @Override
    public HeaderValue value() {
        return value;
    }

    @Override
    public Software knownValue() {
        return value;
    }
}
