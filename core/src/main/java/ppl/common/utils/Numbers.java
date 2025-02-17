package ppl.common.utils;

import ppl.common.utils.order.Condition;

import java.math.BigInteger;

public final class Numbers {
    private Numbers() {}

    public static boolean inLong(BigInteger bi) {
        return Condition.in(bi, BigInteger.valueOf(Long.MIN_VALUE), BigInteger.valueOf(Long.MAX_VALUE));
    }
    public static boolean inInt(Long l) {
        return Condition.in(l, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }
    public static boolean inShort(Long l) {
        return Condition.in(l, Short.MIN_VALUE, Short.MAX_VALUE);
    }
    public static boolean inShort(Integer i) {
        return Condition.in(i, Short.MIN_VALUE, Short.MAX_VALUE);
    }
    public static boolean inByte(Long l) {
        return Condition.in(l, Byte.MIN_VALUE, Byte.MAX_VALUE);
    }
    public static boolean inByte(Integer i) {
        return Condition.in(i, Byte.MIN_VALUE, Byte.MAX_VALUE);
    }
    public static boolean inByte(Short s) {
        return Condition.in(s, Byte.MIN_VALUE, Byte.MAX_VALUE);
    }
}
