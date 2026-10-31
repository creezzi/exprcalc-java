package io.github.creezzi.exprcalc;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import org.junit.jupiter.api.Test;

class ExprCalcTest {
    @Test
    void respectsPrecedenceAndParentheses() {
        assertEquals(7, ExprCalc.evaluate("1 + 2 * 3"));
        assertEquals(9, ExprCalc.evaluate("(1 + 2) * 3"));
        assertEquals(2.5, ExprCalc.evaluate("10 / 4"));
        assertEquals(2, ExprCalc.evaluate("10 % 4"));
    }

    @Test
    void powerIsRightAssociativeAndBindsTighterThanUnaryMinus() {
        assertEquals(512, ExprCalc.evaluate("2 ^ 3 ^ 2"));
        assertEquals(-4, ExprCalc.evaluate("-2 ^ 2"));
        assertEquals(0.5, ExprCalc.evaluate("2 ^ -1"));
    }

    @Test
    void variablesFunctionsAndConstants() {
        assertEquals(32, ExprCalc.evaluate("2 * (x + 3) ^ 2", Map.of("x", 1.0)));
        assertEquals(6, ExprCalc.evaluate("sqrt(16) + max(1, 2)"));
        assertEquals(1, ExprCalc.evaluate("min(3, 1, 2)"));
        assertEquals(Math.PI, ExprCalc.evaluate("pi"));
    }

    @Test
    void reportsErrors() {
        for (String bad : new String[] {"", "1 +", "(1", "1 2", "foo", "foo(1)", "1 / 0", "2 $ 3", "sqrt()", "1.2.3", "max()"}) {
            assertThrows(IllegalArgumentException.class, () -> ExprCalc.evaluate(bad), bad);
        }
    }
}
