package ppl.common.utils.config;

public interface Key {
    String keyString();

    Integer keyInteger();

    Long keyLong();

    Boolean keyBoolean();

    <M> M key(Class<M> targetClass);
}
