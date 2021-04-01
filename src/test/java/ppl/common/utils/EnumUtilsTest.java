package ppl.common.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EnumUtilsTest {
    enum A {

        U(1);

        private int code;
        A(int code) {
            this.code = code;
        }

        @EnumEncoder
        private int getCode() {
            return this.code;
        }

    }
    @Test
    public void test() {
        Assertions.assertEquals(A.U, EnumUtils.enumOf(A.class, 1));
    }
}
