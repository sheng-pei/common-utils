package ppl.common.utils.attire.proxy.server.param;

import ppl.common.utils.reflect.annotation.Alias;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Form {
    @Alias("mime")
    String value() default "";
    @Alias("value")
    String mime() default "";
}
