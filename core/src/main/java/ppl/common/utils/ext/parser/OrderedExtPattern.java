package ppl.common.utils.ext.parser;

public class OrderedExtPattern extends ExtPattern implements Ordered {
    
    private final int order;

    OrderedExtPattern(ExtPattern pattern, int order) {
        super(pattern);
        this.order = order;
    }

    @Override
    public int getOrder() {
        return order;
    }

}
