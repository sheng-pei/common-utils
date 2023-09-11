package ppl.common.utils.http.request;

//TODO, Use this to initialize cookie.
public interface RequestInitializer {
    void init(Request.Builder request);
}
