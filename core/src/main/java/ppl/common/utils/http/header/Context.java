package ppl.common.utils.http.header;

import ppl.common.utils.http.header.value.parameter.ParameterParser;

public class Context {
    public static final Context DEFAULT = new Context();
    private final ParameterParser parameterParser;

    private Context() {
        this.parameterParser = ParameterParser.DEFAULT;
    }

    private Context(ParameterParser parameterParser) {
        this.parameterParser = parameterParser;
    }

    public ParameterParser getParameterParser() {
        return parameterParser;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        private ParameterParser parameterParser;

        private Builder() {}

        public Builder parameterParser(ParameterParser parameterParser) {
            this.parameterParser = parameterParser;
            return this;
        }

        public Context build() {
            return new Context(parameterParser);
        }

    }

}
