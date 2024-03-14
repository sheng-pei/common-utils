package ppl.common.utils.http.header.value.parameter;

import ppl.common.utils.argument.analyzer.Analyzer;
import ppl.common.utils.argument.argument.Argument;
import ppl.common.utils.argument.argument.Arguments;
import ppl.common.utils.argument.argument.value.ArgumentValue;
import ppl.common.utils.argument.argument.value.ValueArgument;
import ppl.common.utils.http.header.Context;
import ppl.common.utils.http.header.SingleLineHeaderValue;
import ppl.common.utils.http.symbol.HttpCharGroup;
import ppl.common.utils.pair.Pair;
import ppl.common.utils.string.Strings;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public abstract class ParameterizedHeaderValue<
        AS extends Arguments<String, ValueArgument<Object>>,
        PHV extends ParameterizedHeaderValue<AS, PHV>> extends SingleLineHeaderValue {

    static final char DELIMITER = ';';
    public static final char SEPARATOR = '=';

    private final AS arguments;
    private final Map<Argument, ArgumentValue<Object>> parameters;
    private final String unknownParameters;

    protected ParameterizedHeaderValue(String value,
                                       Function<String, AS> argumentsCreator,
                                       Context context) {
        super(value);
        Pair<String, String> pair = partitionTargetAndArguments(value);
        AS arguments = createArguments(pair.getFirst(), argumentsCreator);
        Analyzer<String> analyzer = new Analyzer<>(arguments);
        ParameterParser parser = parser(context);
        List<Object> res = analyzer.analyse(parser.parse(pair.getSecond()));

        Map<Argument, ArgumentValue<Object>> ps = new HashMap<>();
        StringBuilder ups = new StringBuilder();
        res.forEach(o -> {
            if (o instanceof ArgumentValue) {
                @SuppressWarnings("unchecked")
                ArgumentValue<Object> av = (ArgumentValue<Object>) o;
                ps.put(av.getArgument(), av);
            } else if (o instanceof ValueArgument) {
                @SuppressWarnings("unchecked")
                ValueArgument<Object> a = (ValueArgument<Object>) o;
                ps.put(a, ArgumentValue.create(a, null));
            } else {
                ups.append(o).append(DELIMITER).append(" ");
            }
        });
        if (ups.length() > 0) {
            ups.setLength(ups.length() - 2);
        }

        this.arguments = arguments;
        this.parameters = ps;
        this.unknownParameters = ups.toString();
    }

    public AS getArguments() {
        return arguments;
    }

    private static <T extends Arguments<String, ValueArgument<Object>>>
    T createArguments(String target, Function<String, T> targetCreator) {
        required(target);
        return targetCreator.apply(target);
    }

    private static Pair<String, String> partitionTargetAndArguments(String string) {
        char[] chars = string.toCharArray();
        int start = 0;
        String first;
        do {
            int end = endOfTarget(chars, start);
            first = first(chars, start, end);
            start = next(end, chars.length);
            if (!first.isEmpty()) {
                break;
            }
        } while (start < chars.length);
        return Pair.create(first, new String(chars, start, chars.length - start));
    }

    private static int endOfTarget(char[] chars, int start) {
        int idx = Strings.indexOf(DELIMITER, chars, start, chars.length);
        if (idx < 0) {
            idx = chars.length;
        }
        return idx;
    }

    private static String first(char[] chars, int start, int end) {
        String first = new String(chars, start, end - start);
        return Strings.trim(first, HttpCharGroup.WS);
    }

    private static int next(int end, int length) {
        return end == length ? length : end + 1;
    }

    private static void required(String string) {
        if (string.isEmpty()) {
            throw new IllegalArgumentException("Target is required.");
        }
    }

    private static ParameterParser parser(Context context) {
        if (context != null && context != Context.DEFAULT) {
            return context.getParameterParser();
        }
        return ParameterParser.DEFAULT;
    }

    public final PHV appendLexicalParameter(String name, String value) {
        ensureKnown(name);
        ParameterParser parser = ParameterParser.DEFAULT;
        List<Object> res = new Analyzer<>(arguments)
                .analyse(parser.parse(name, value));
        @SuppressWarnings("unchecked")
        ArgumentValue<Object> av = (ArgumentValue<Object>) res.get(0);
        ArgumentValue<Object> exist = parameters.get(av.getArgument());
        parameters.put(av.getArgument(), exist == null ? av : exist.merge(av));
        return self();
    }

    public final PHV removeParameter(String name) {
        ensureKnown(name);
        parameters.remove(arguments.getByKey(name));
        return self();
    }

    private PHV self() {
        @SuppressWarnings("unchecked")
        PHV PHV = (PHV) this;
        return PHV;
    }

    public final PHV setParameter(String name, Object value) {
        ensureKnown(name);
        ValueArgument<Object> a = arguments.getByKey(name);
        ArgumentValue<Object> av = ArgumentValue.create(a, value);
        parameters.remove(a);
        return appendLexicalParameter(name, av.toString());
    }

    public final Object getParameter(String name) {
        ensureKnown(name);
        ArgumentValue<Object> p = parameters.get(arguments.getByKey(name));
        return p == null ? null : p.value();
    }

    public final boolean hasParameter(String name) {
        ensureKnown(name);
        return parameters.containsKey(arguments.getByKey(name));
    }

    private void ensureKnown(String name) {
        if (arguments.getByKey(name) == null) {
            throw new IllegalArgumentException(Strings.format(
                    "Unknown parameter: '{}' for: '{}'.", name, arguments));
        }
    }

    @Override
    public String toCanonicalString() {
        return toCanonicalString(parameters.values());
    }

    private String toCanonicalString(Collection<? extends ArgumentValue<?>> parameters) {
        String p = concatParameters(parameters);
        return arguments.toString() + (p.isEmpty() ? "" : DELIMITER + " " + concatParameters(parameters));
    }

    private String concatParameters(Collection<? extends ArgumentValue<?>> parameters) {
        StringBuilder pBuilder = new StringBuilder();
        appendParameters(parameters, pBuilder);
        if (!parameters.isEmpty() && unknownParameters.isEmpty()) {
            pBuilder.setLength(pBuilder.length() - 2);
        } else {
            pBuilder.append(unknownParameters);
        }
        return pBuilder.toString();
    }

    private void appendParameters(
            Collection<? extends ArgumentValue<?>> parameters,
            StringBuilder builder) {//TODO, av 规范化输出
        parameters.forEach(av -> {
            String k = av.keyString();
            String v = av.valueString();
            builder.append(k);
            if (v != null || !ignoreSeparatorIfNullValue()) {
                builder.append(SEPARATOR);
            }
            if (v != null) {
                builder.append(v);
            }
            builder.append(DELIMITER).append(" ");
        });
    }

    private boolean ignoreSeparatorIfNullValue() {
        return true;
    }

}
