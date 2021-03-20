package site.zido.coffee.security.authentication.phone;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CustomCodeGeneratorTest {
    @Test
    public void testGenerate() {
        CustomCodeGenerator generator = new CustomCodeGenerator(CustomCodeGenerator.Mode.NUMBER);
        String code = generator.generateCode("xxx");
        Assertions.assertEquals(6, code.length());
        String code2 = generator.generateCode("xxx");
        Assertions.assertNotEquals(code, code2);
    }

    @Test
    public void testMaxAndMinGenerate() {
        CustomCodeGenerator generator = new CustomCodeGenerator(CustomCodeGenerator.Mode.NUMBER);
        generator.setMaxLength(10);
        generator.setMinLength(1);
        String code = generator.generateCode("");
        Assertions.assertTrue(1 <= code.length() && code.length() <= 10);
    }
}
