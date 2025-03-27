package ppl.common.utils.http.header.known;

import ppl.common.utils.http.Name;
import ppl.common.utils.http.header.Context;
import ppl.common.utils.http.header.Header;
import ppl.common.utils.http.header.HeaderValue;
import ppl.common.utils.http.header.UnknownHeaderValue;
import ppl.common.utils.http.header.value.disposition.DispositionType;

@Name("Content-Disposition")
public class ContentDisposition implements Header<DispositionType> {

    private final HeaderValue value;

    private ContentDisposition(String value, Context context) {
        this.value = DispositionType.create(value, context);
    }

    public ContentDisposition(DispositionType dispositionType) {
        this.value = dispositionType;
    }

    public ContentDisposition(String dispositionType) {
        this(DispositionType.ensureKnown(dispositionType));
    }

    @Override
    public HeaderValue value() {
        return value;
    }

    @Override
    public DispositionType knownValue() {
        if (value instanceof UnknownHeaderValue) {
            return null;
        }
        return (DispositionType) value;
    }

}
