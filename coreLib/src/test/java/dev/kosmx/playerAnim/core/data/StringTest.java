package dev.kosmx.playerAnim.core.data;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class StringTest {

    @Test
    public void camelTest() {
        String str = "camel_case_string";
        String converted = "camelCaseString";
        Assertions.assertEquals(converted, GeckoLibSerializer.snake2Camel(str));
    }
}
