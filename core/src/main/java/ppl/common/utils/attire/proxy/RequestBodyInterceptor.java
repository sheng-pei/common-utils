package ppl.common.utils.attire.proxy;

import java.io.InputStream;
import java.lang.reflect.Parameter;

public interface RequestBodyInterceptor {
    boolean accept(Parameter parameter, Object body);
    InputStream handle(Parameter parameter, Object body);
}
