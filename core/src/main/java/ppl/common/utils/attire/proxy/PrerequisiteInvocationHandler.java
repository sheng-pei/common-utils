package ppl.common.utils.attire.proxy;

import java.lang.reflect.InvocationHandler;

public interface PrerequisiteInvocationHandler extends InvocationHandler {
    void assertInterfaces(Class<?>[] interfaces);
}
