package ppl.common.utils.attire.proxy.server;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Repeatable(Headers.class)
public @interface Header {
    String value();
}
