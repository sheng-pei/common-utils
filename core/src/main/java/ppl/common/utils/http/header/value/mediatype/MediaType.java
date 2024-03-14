package ppl.common.utils.http.header.value.mediatype;

import ppl.common.utils.http.header.Context;
import ppl.common.utils.http.header.HeaderValue;
import ppl.common.utils.http.header.UnknownHeaderValue;
import ppl.common.utils.http.header.value.UnknownParameterTargetException;
import ppl.common.utils.http.header.value.parameter.ParameterizedHeaderValue;

public class MediaType extends ParameterizedHeaderValue<Mime, MediaType> {

    public static HeaderValue create(String mediaType) {
        return create(mediaType, null);
    }

    public static HeaderValue create(String mediaType, Context context) {
        try {
            return new MediaType(mediaType, context);
        } catch (UnknownParameterTargetException e) {
            return UnknownHeaderValue.create(mediaType);
        }
    }

    private MediaType(String mediaType, Context context) {
        super(mediaType, Mime::create, context);
    }

}
