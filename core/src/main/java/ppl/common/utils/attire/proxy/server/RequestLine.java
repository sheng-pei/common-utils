package ppl.common.utils.attire.proxy.server;

import ppl.common.utils.reflect.annotation.Alias;
import ppl.common.utils.http.request.Method;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RequestLine {
    @Alias("uri")
    String value() default "";
    Method method() default Method.GET;
    @Alias("value")
    String uri() default "";
}
