package ppl.common.utils.ordinal;

import java.util.function.Function;

public class Roman {
    enum Unit {
        I(1),
        V(5),
        X(10),
        L(50),
        C(100),
        D(500),
        M(1000);

        private final Integer value;

        Unit(Integer value) {
            this.value = value;
        }

        public Integer getValue() {
            return value;
        }
    }

    public static String roman(int j) {
        return roman(j, true);
    }

    public static String roman(int j, boolean upper) {
        return roman(j, upper ? Enum::name : u -> u.name().toLowerCase());
    }

    private static String roman(int j, Function<Unit, String> name) {
        if (j <= 0 || j >= 4000) {
            throw new IllegalArgumentException("Out of range: 1~3999");
        }

        StringBuilder builder = new StringBuilder();
        while (j > 0) {
            Unit i = Unit.I;
            Unit v = Unit.V;
            Unit x = Unit.X;
            if (j >= 10 && j < 100) {
                i = Unit.X;
                v = Unit.L;
                x = Unit.C;
            } else if (j >= 100 && j < 1000) {
                i = Unit.C;
                v = Unit.D;
                x = Unit.M;
            } else if (j >= 1000 && j < 4000) {
                i = Unit.M;
            }

            if (j >= 5 * i.getValue() && j < 9 * i.getValue()) {
                builder.append(name.apply(v));
                j = j - v.getValue();
            } else if (j >= 9 * i.getValue() && j < x.getValue()) {
                builder.append(name.apply(i));
                builder.append(name.apply(x));
                j = j - x.getValue() + i.getValue();
            }

            if (j >= i.getValue() && j < 4 * i.getValue()) {
                while (j >= i.getValue()) {
                    builder.append(name.apply(i));
                    j = j - i.getValue();
                }
            } else if (j >= 4 * i.getValue() && j < 5 * i.getValue()) {
                builder.append(name.apply(i));
                builder.append(name.apply(v));
                j = j - v.getValue() + i.getValue();
            }
        }
        return builder.toString();
    }
}
