package ppl.common.utils.http;

import ppl.common.utils.http.request.Request;

public interface Connector {
    Connection connect(Request request);
}
