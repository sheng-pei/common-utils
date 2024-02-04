package ppl.common.utils.http.header;

public interface HeaderCreator {
    Header<HeaderValue> create(String value, Context context);
}
