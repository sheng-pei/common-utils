package ppl.common.utils.http.header.value.transfercoding;

import ppl.common.utils.http.header.Context;
import ppl.common.utils.http.header.HeaderValue;
import ppl.common.utils.http.header.UnknownHeaderValue;
import ppl.common.utils.http.header.value.UnknownParameterTargetException;
import ppl.common.utils.http.header.value.parameter.ParameterizedHeaderValue;

public class Coding extends ParameterizedHeaderValue<CodingKind, Coding> {

    public static HeaderValue create(String transferCoding, Context context) {
        try {
            return new Coding(transferCoding, context);
        } catch (UnknownParameterTargetException e) {
            return UnknownHeaderValue.create(transferCoding);
        }
    }

    public static Coding create(CodingKind kind) {
        return new Coding(kind.getName(), null);
    }

    public static Coding create(CodingKind kind, Context context) {
        return new Coding(kind.getName(), context);
    }

    private Coding(String transferCoding, Context context) {
        super(transferCoding, CodingKind::enumOf, context);
    }

}
