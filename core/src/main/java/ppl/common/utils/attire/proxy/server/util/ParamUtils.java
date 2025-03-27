package ppl.common.utils.attire.proxy.server.util;

import ppl.common.utils.attire.proxy.server.param.Param;
import ppl.common.utils.attire.proxy.server.param.ParamPojo;
import ppl.common.utils.string.Strings;

import java.lang.reflect.Parameter;

public class ParamUtils {
    public static void completeBodyParam(Parameter[] parameters, int[] body) {
        for (int i = 0; i < body.length; i++) {
            if (body[i] < 0 && parameters[i].isAnnotationPresent(Param.class)) {
                body[i] = i;
            }
        }
    }

    public static ParamPojo[] parseBodyParam(Parameter[] parameters, int[] body) {
        ParamPojo[] pojo = new ParamPojo[parameters.length];
        for (int i : body) {
            if (i >= 0) {
                if (parameters[i].isAnnotationPresent(Param.class)) {
                    pojo[i] = new ParamPojo(parameters[i].getAnnotation(Param.class));
                } else {
                    pojo[i] = ParamPojo.DEFAULT_PARAM;
                }

                ParamPojo p = pojo[i].changeNameIfAbsent(parameters[i].getName());
                if (p != null) {
                    if (!parameters[i].isNamePresent()) {
                        throw new IllegalArgumentException(Strings.format(
                                "Name for param argument of position [{}] not specified, " +
                                        "and parameter name information not available via reflection. " +
                                        "Ensure that the compiler uses the '-parameters' flag.", i));
                    }
                    pojo[i] = p;
                }
            }
        }
        return pojo;
    }
}
