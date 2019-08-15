package site.zido.coffee.auth.config;

/**
 * @author zido
 */
public interface ObjectPostProcessor<T> {

    <O extends T> O postProcess(O object);
}
