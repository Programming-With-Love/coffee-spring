package site.zido.coffee.security.authentication.phone;

import org.junit.Assert;
import org.junit.Test;

public class CustomCodeGeneratorTest {
    @Test
    public void testGenerate() {
        CustomCodeGenerator generator = new CustomCodeGenerator(CustomCodeGenerator.Mode.NUMBER);
        String code = generator.generateCode("xxx");
        Assert.assertEquals(6, code.length());
        String code2 = generator.generateCode("xxx");
        Assert.assertNotEquals(code, code2);
    }

    @Test
    public void testMaxAndMinGenerate() {
        CustomCodeGenerator generator = new CustomCodeGenerator(CustomCodeGenerator.Mode.NUMBER);
        generator.setMaxLength(10);
        generator.setMinLength(1);
        String code = generator.generateCode("");
        Assert.assertTrue(1 <= code.length() && code.length() <= 10);
    }
}
