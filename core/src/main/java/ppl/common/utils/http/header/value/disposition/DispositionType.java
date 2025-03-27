package ppl.common.utils.http.header.value.disposition;

import ppl.common.utils.http.header.Context;
import ppl.common.utils.http.header.HeaderValue;
import ppl.common.utils.http.header.UnknownHeaderValue;
import ppl.common.utils.http.header.value.UnknownParameterTargetException;
import ppl.common.utils.http.header.value.parameter.ParameterizedHeaderValue;

public class DispositionType extends ParameterizedHeaderValue<Disposition, DispositionType> {
    private DispositionType(String value, Context context) {
        super(value, Disposition::create, context);
    }

    public static HeaderValue create(String dispositionType) {
        return create(dispositionType, null);
    }

    public static HeaderValue create(String dispositionType, Context context) {
        try {
            return new DispositionType(dispositionType, context);
        } catch (UnknownParameterTargetException e) {
            return UnknownHeaderValue.create(dispositionType);
        }
    }

    public static DispositionType ensureKnown(String dispositionType) {
        return new DispositionType(dispositionType, null);
    }
}
