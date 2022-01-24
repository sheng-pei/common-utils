package ppl.common.utils.logging;

import ppl.common.utils.string.substring.impl.ToStringArguments;

public class LoggingArguments extends ToStringArguments {

    public LoggingArguments(Object... targets) {
        super(targets);
    }

    @Override
    protected String convert(Object obj) {
        if (obj != null) {
            Class<?> clazz = obj.getClass();
            if (clazz.isArray() && clazz.getComponentType().isPrimitive()) {
                StringBuilder builder = new StringBuilder();
                builder.append('[');
                if (clazz == boolean[].class) {
                    booleanArrayConvert(builder, (boolean[]) obj);
                } else if (clazz == byte[].class) {
                    byteArrayConvert(builder, (byte[]) obj);
                } else if (clazz == short[].class) {
                    shortArrayConvert(builder, (short[]) obj);
                } else if (clazz == int[].class) {
                    intArrayConvert(builder, (int[]) obj);
                } else if (clazz == long[].class) {
                    longArrayConvert(builder, (long[]) obj);
                } else if (clazz == char[].class) {
                    charArrayConvert(builder, (char[]) obj);
                } else if (clazz == float[].class) {
                    floatArrayConvert(builder, (float[]) obj);
                } else if (clazz == double[].class) {
                    doubleArrayConvert(builder, (double[]) obj);
                }
                builder.append(']');
                return builder.toString();
            }
        }
        return super.convert(obj);
    }

    private void booleanArrayConvert(StringBuilder builder, boolean[] bools) {
        if (bools.length > 0) {
            builder.append(bools[0]);
            for (int i = 1; i < bools.length; i++) {
                builder.append(", ").append(bools[i]);
            }
        }
    }

    private void byteArrayConvert(StringBuilder builder, byte[] bytes) {
        if (bytes.length > 0) {
            builder.append(bytes[0]);
            for (int i = 1; i < bytes.length; i++) {
                builder.append(", ").append(bytes[i]);
            }
        }
    }

    private void shortArrayConvert(StringBuilder builder, short[] shorts) {
        if (shorts.length > 0) {
            builder.append(shorts[0]);
            for (int i = 1; i < shorts.length; i++) {
                builder.append(", ").append(shorts[i]);
            }
        }
    }

    private void intArrayConvert(StringBuilder builder, int[] ints) {
        if (ints.length > 0) {
            builder.append(ints[0]);
            for (int i = 1; i < ints.length; i++) {
                builder.append(", ").append(ints[i]);
            }
        }
    }

    private void longArrayConvert(StringBuilder builder, long[] longs) {
        if (longs.length > 0) {
            builder.append(longs[0]);
            for (int i = 1; i < longs.length; i++) {
                builder.append(", ").append(longs[i]);
            }
        }
    }

    private void charArrayConvert(StringBuilder builder, char[] chars) {
        if (chars.length > 0) {
            builder.append(chars[0]);
            for (int i = 1; i < chars.length; i++) {
                builder.append(", ").append(chars[i]);
            }
        }
    }

    private void floatArrayConvert(StringBuilder builder, float[] floats) {
        if (floats.length > 0) {
            builder.append(floats[0]);
            for (int i = 1; i < floats.length; i++) {
                builder.append(", ").append(floats[i]);
            }
        }
    }

    private void doubleArrayConvert(StringBuilder builder, double[] doubles) {
        if (doubles.length > 0) {
            builder.append(doubles[0]);
            for (int i = 1; i < doubles.length; i++) {
                builder.append(", ").append(doubles[i]);
            }
        }
    }

}
