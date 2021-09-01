package ppl.common.utils.enumerate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ppl.common.utils.enumerate.EnumEncoder;
import ppl.common.utils.enumerate.EnumUtils;

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
