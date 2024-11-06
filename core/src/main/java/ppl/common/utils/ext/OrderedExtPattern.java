package ppl.common.utils.ext;

class OrderedExtPattern {
    
    private final ExtPattern pattern;
    private final int order;

    OrderedExtPattern(ExtPattern pattern, int order) {
        this.pattern = pattern;
        this.order = order;
    }

    public ExtPattern getPattern() {
        return pattern;
    }

    public int getOrder() {
        return order;
    }
}
