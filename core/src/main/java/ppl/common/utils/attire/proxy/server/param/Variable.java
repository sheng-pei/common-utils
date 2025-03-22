package ppl.common.utils.attire.proxy.server.param;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Variable {
    String value() default "";
}
