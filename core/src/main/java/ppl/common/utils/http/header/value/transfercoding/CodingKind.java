package ppl.common.utils.http.header.value.transfercoding;

import ppl.common.utils.argument.argument.Arguments;
import ppl.common.utils.argument.argument.value.ValueArgument;
import ppl.common.utils.http.header.BaseArguments;
import ppl.common.utils.http.header.value.UnknownParameterTargetException;
import ppl.common.utils.string.ascii.CaseIgnoreString;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum CodingKind implements Arguments<CaseIgnoreString, String, ValueArgument<CaseIgnoreString, Object>> {
    CHUNKED("chunked"),
    COMPRESS("compress"),
    DEFLATE("deflate"),
    GZIP("gzip");

    private final String name;
    private final Arguments<CaseIgnoreString, String, ValueArgument<CaseIgnoreString, Object>> arguments;

    CodingKind(String name) {
        this(name, Collections.emptyList());
    }

    CodingKind(String name, List<ValueArgument<CaseIgnoreString, ?>> arguments) {
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
    public List<ValueArgument<CaseIgnoreString, Object>> getArguments() {
        return arguments.getArguments();
    }

    @Override
    public ValueArgument<CaseIgnoreString, Object> get(String s) {
        return arguments.get(s);
    }

    @Override
    public ValueArgument<CaseIgnoreString, Object> getByName(CaseIgnoreString caseIgnoreString) {
        return arguments.getByName(caseIgnoreString);
    }
}
