package site.zido.coffee.extra.security;


import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * 防重放攻击解决方案
 *
 * @author zido
 */
public abstract class AbstractSecurity {
    private static final String[] HEX_DIG_ITS = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};
    private static Charset defaultCharset = Charset.forName("utf-8");
    private String token;
    private long timeDifference = TimeUnit.MINUTES.toMillis(1);
    private long lastModifyTime = System.currentTimeMillis();

    public AbstractSecurity(String token) {
        this.token = token;
    }

    private static String bytesToStr(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(byteToHex(b));
        }
        return sb.toString();
    }

    private static String byteToHex(byte b) {
        int n = (int) b;
        if (n < 0) {
            n += 256;
        }
        final int div = n / 16;
        final int mod = n % 16;
        return HEX_DIG_ITS[div] + HEX_DIG_ITS[mod];
    }

    /**
     * 校验签名
     *
     * @param timeStamp 时间戳
     * @param sign      签名串
     * @param params    待验证的参数
     * @return 是否验证通过
     */
    public boolean validate(long timeStamp, String sign, Object... params) {
        return encode(timeStamp, params).equalsIgnoreCase(sign);
    }

    /**
     * 检测重放
     *
     * @param nonce     nonce字段
     * @param timestamp 时间戳
     * @return false为重复/超时的请求，true为合理请求
     */
    public boolean checkNonce(Object nonce, long timestamp) {
        long currentTimeMillis = System.currentTimeMillis();
        //判断请求是否超时
        if (currentTimeMillis - timestamp > timeDifference) {
            return false;
        }
        //清空60s前的nonce集合
        if (currentTimeMillis - lastModifyTime > timeDifference) {
            clearNonce();
        }
        //判断nonce是否存在
        if (addNonce(timestamp + "" + nonce)) {
            lastModifyTime = currentTimeMillis;
            return true;
        }
        return false;
    }

    protected abstract void clearNonce();

    protected abstract boolean addNonce(String nonce);

    /**
     * 加密数据
     *
     * @param timeStamp 时间戳
     * @param params    参数
     * @return 加密串
     */
    public String encode(long timeStamp, Object... params) {
        String originValue = token + handleParams(params) + timeStamp + token;
        byte[] secretBytes;
        try {
            secretBytes = MessageDigest.getInstance("md5").digest(
                    originValue.getBytes(defaultCharset));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return bytesToStr(secretBytes);
    }

    private String handleParams(Object... params) {
        return Arrays.stream(params)
                .map(Object::toString)
                .sorted()
                .reduce((String target, String current) -> target + current)
                .orElseThrow(() -> new IllegalArgumentException("params error"));
    }

    public void setTimeDifference(long timeDifference) {
        this.timeDifference = timeDifference;
    }
}
