package ppl.common.utils.command;

public interface Validator<V> {
    @SuppressWarnings("rawtypes")
    Validator ALWAYS_TRUE = new Validator() {
        @Override
        public String comment() {
            return "return as is";
        }

        @Override
        public boolean isValid(Object value) {
            return true;
        }
    };

    static <V> Validator<V> alwaysTrue() {
        @SuppressWarnings("unchecked")
        Validator<V> res = (Validator<V>) ALWAYS_TRUE;
        return res;
    }

    String comment();
    boolean isValid(V value);
}
