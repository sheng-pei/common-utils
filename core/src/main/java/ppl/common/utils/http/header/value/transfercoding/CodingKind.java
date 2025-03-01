package ppl.common.utils.http.header.value.transfercoding;

import ppl.common.utils.argument.argument.Arguments;
import ppl.common.utils.argument.argument.value.ValuedArgument;
import ppl.common.utils.http.header.BaseArguments;
import ppl.common.utils.http.header.value.UnknownParameterTargetException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum CodingKind implements Arguments<String, ValuedArgument<Object>> {
    CHUNKED("chunked"),
    COMPRESS("compress"),
    DEFLATE("deflate"),
    GZIP("gzip");

    private final String name;
    private final BaseArguments arguments;

    CodingKind(String name) {
        this(name, Collections.emptyList());
    }

    CodingKind(String name, List<ValuedArgument<?>> arguments) {
        this.name = name;
        this.arguments = arguments.isEmpty() ? BaseArguments.EMPTY : new BaseArguments(arguments);
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    public static CodingKind enumOf(String name) {
        if (name == null) {
            return null;
        }

        CodingKind[] codingKinds = CodingKind.values();
        return Arrays.stream(codingKinds)
                .filter(codingKind -> codingKind.name.equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new UnknownParameterTargetException(
                        "No enum constant " + CodingKind.class.getCanonicalName() + "." + name));
    }

    @Override
    public ValuedArgument<Object> getByKey(String s) {
        return arguments.getByKey(s);
    }

    @Override
    public ValuedArgument<Object> getByName(String name) {
        return arguments.getByName(name);
    }
}
