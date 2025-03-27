package ppl.common.utils.attire.proxy.server.param;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface FormData {
    String value() default "";
}
