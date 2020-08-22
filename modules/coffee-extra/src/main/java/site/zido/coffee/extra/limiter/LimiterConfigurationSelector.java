package site.zido.coffee.extra.limiter;

import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.AdviceModeImportSelector;
import org.springframework.context.annotation.AutoProxyRegistrar;

public class LimiterConfigurationSelector extends AdviceModeImportSelector<EnableLimiter> {
    @Override
    protected String[] selectImports(AdviceMode adviceMode) {
        return new String[]{
                AutoProxyRegistrar.class.getName(),
                ProxyLimiterConfiguration.class.getName()
        };
    }
}
