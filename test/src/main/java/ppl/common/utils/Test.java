package ppl.common.utils;

import ppl.common.utils.attire.proxy.server.RequestLine;
import ppl.common.utils.attire.proxy.server.Server;
import ppl.common.utils.attire.proxy.server.param.Json;
import ppl.common.utils.attire.proxy.server.param.Query;
import ppl.common.utils.http.request.Method;

@Json
@Server(name = "test", version = "1.0")
public interface Test {
    @RequestLine(method = Method.POST, uri = "/myapp/request-parameter/json1")
    String json1(@Query String test, Vo vo);
}
