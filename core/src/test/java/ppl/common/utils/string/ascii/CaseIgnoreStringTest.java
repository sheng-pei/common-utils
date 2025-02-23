package ppl.common.utils.string.ascii;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ppl.common.utils.character.ascii.CaseIgnoreString;
import ppl.common.utils.helper.EqualsTester;

class CaseIgnoreStringTest {
    @Test
    void testEquals() {
        EqualsTester tester = new EqualsTester();
        tester.addGroup(CaseIgnoreString.create("aAl;"), CaseIgnoreString.create("aal;"));
        tester.test();
    }

    @Test
    void create() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> CaseIgnoreString.create("中a"));
    }
}