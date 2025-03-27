package ppl.common.utils.http.header.value.disposition;

import ppl.common.utils.argument.argument.Arguments;
import ppl.common.utils.argument.argument.value.ValuedArgument;
import ppl.common.utils.argument.argument.value.map.Mappers;
import ppl.common.utils.character.ascii.CaseIgnoreString;
import ppl.common.utils.http.header.BaseArguments;
import ppl.common.utils.http.header.value.UnknownParameterTargetException;
import ppl.common.utils.http.header.value.parameter.ParameterValuedArgument;
import ppl.common.utils.http.symbol.Lexer;
import ppl.common.utils.string.Strings;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class Disposition implements Arguments<String, ValuedArgument<Object>> {

    private static final ValuedArgument<String> NAME_ARGUMENT =
            ParameterValuedArgument.newBuilder("name")
                    .map(Mappers.required())
                    .map(v -> new String(v.getBytes(StandardCharsets.ISO_8859_1),
                            StandardCharsets.UTF_8))
                    .collect()
                    .build(v -> new String(v.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1));

    private static final ValuedArgument<String> FILENAME_ARGUMENT =
            ParameterValuedArgument.newBuilder("filename")
                    .map(Mappers.required())
                    .map(v -> new String(v.getBytes(StandardCharsets.ISO_8859_1),
                            StandardCharsets.UTF_8))
                    .collect()
                    .build(v -> new String(v.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1));

    private static final Map<CaseIgnoreString, Disposition> DISPOSITIONS;

    public static final Disposition FORM_DATA;
    public static final Disposition INLINE;
    public static final Disposition ATTACHMENT;

    static {
        Map<CaseIgnoreString, Disposition> dispositions = new HashMap<>();
        FORM_DATA = register(dispositions, "form-data", Arrays.asList(NAME_ARGUMENT, FILENAME_ARGUMENT));
        INLINE = register(dispositions, "inline", Collections.singletonList(FILENAME_ARGUMENT));
        ATTACHMENT = register(dispositions, "attachment", Collections.singletonList(FILENAME_ARGUMENT));
        DISPOSITIONS = Collections.unmodifiableMap(dispositions);
    }

    private static Disposition register(
            Map<CaseIgnoreString, Disposition> dispositions,
            String disposition) {
        Disposition ret = new Disposition(disposition);
        dispositions.put(ret.name, ret);
        return ret;
    }

    private static Disposition register(
            Map<CaseIgnoreString, Disposition> dispositions,
            String disposition,
            List<? extends ValuedArgument<?>> arguments) {
        Disposition ret = new Disposition(disposition, arguments);
        dispositions.put(ret.name, ret);
        return ret;
    }

    private final CaseIgnoreString name;
    private final BaseArguments arguments;

    private Disposition(String name) {
        this(name, Collections.emptyList());
    }

    private Disposition(String name, List<? extends ValuedArgument<?>> arguments) {
        name = Strings.emptyIfNull(name).trim();
        if (!Lexer.isToken(name)) {
            throw new IllegalArgumentException("Disposition type is not token.");
        }
        this.name = CaseIgnoreString.create(name);
        this.arguments = arguments.isEmpty() ? BaseArguments.EMPTY : new BaseArguments(arguments);
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Disposition m = (Disposition) o;
        return Objects.equals(name, m.name);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name.toString();
    }

    public static Disposition create(String name) {
        if (name == null) {
            return null;
        }

        Disposition m = DISPOSITIONS.get(CaseIgnoreString.create(name));
        if (m == null) {
            throw new UnknownParameterTargetException("Unknown disposition type: " + name);
        }
        return m;
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
