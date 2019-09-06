package site.zido.coffee.auth.config;

/**
 * 认证建造者接口
 */
public interface AuthBuilder<O> {
    /**
     * 构建对象
     * <p>
     * 返回值可以为空
     *
     * @return result obejct
     * @throws Exception ex
     */
    O build() throws Exception;
}
