package site.zido.coffee.security.authentication.phone;

import io.jsonwebtoken.lang.Assert;
import org.springframework.beans.factory.InitializingBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 默认验证码生成器
 * <p>
 * 具有一定可自定义功能,例如可控的动态长度，字符/数组组合
 *
 * @author zido
 */
public class CustomCodeGenerator implements CodeGenerator, InitializingBean {
    private static final char[] ARR_NUMBER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    private static final char[] ARR_LOWER_CHAR = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
            'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
    private static final char[] ARR_UPPER_CHAR = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
            'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
    private int minLength = 6;
    private int maxLength = 6;
    private final List<Character> arr = new ArrayList<>();
    private final Random random = new Random();

    public CustomCodeGenerator(Mode... modes) {
        if (modes == null || modes.length == 0) {
            this.setMode(Mode.NUMBER);
        } else {
            this.setMode(modes);
        }
    }

    @Override
    public String generateCode(String phone) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < maxLength; i++) {
            int index = random.nextInt(arr.size());
            sb.append(arr.get(index));
            if (sb.length() >= minLength) {
                if (random.nextBoolean()) {
                    break;
                }
            }
        }
        return sb.toString();
    }

    public void setMode(Mode... modes) {
        this.arr.clear();
        addMode(modes);
    }

    public void addMode(Mode... modes) {
        if (modes != null && modes.length > 0) {
            for (Mode mode : modes) {
                switch (mode) {
                    case NUMBER:
                        addArr(ARR_NUMBER);
                        break;
                    case LOWER_CHAR:
                        addArr(ARR_LOWER_CHAR);
                        break;
                    case UPPER_CHAR:
                        addArr(ARR_UPPER_CHAR);
                        break;
                    default:
                        throw new IllegalStateException("unreachable");
                }
            }
        }
    }

    public void addArr(char[] chars) {
        for (char c : chars) {
            this.arr.add(c);
        }
    }

    public void setMinLength(int minLength) {
        this.minLength = minLength;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notEmpty(this.arr, "char array cannot be empty");
    }

    /**
     * 验证码生成模式
     */
    public enum Mode {
        /**
         * 数组
         */
        NUMBER,
        /**
         * 字符
         */
        LOWER_CHAR,
        /**
         * 字符加数字
         */
        UPPER_CHAR,
    }
}
