package site.zido.coffee.autoconfigure.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.util.StringUtils;
import site.zido.coffee.core.Coffee;

import java.util.HashMap;
import java.util.Map;

public class PropertiesRunListener implements SpringApplicationRunListener, Ordered {
    public PropertiesRunListener(SpringApplication application, String[] args) {
    }

    @Override
    public void environmentPrepared(ConfigurableEnvironment environment) {
        MutablePropertySources sources = environment.getPropertySources();
        Map<String, Object> props = new HashMap<>();
        props.put("coffee.version", Coffee.VERSION);

        String property = environment.getProperty("spring.application.name");
        if (StringUtils.hasText(property)) {
            property = "${AnsiStyle.NORMAL}${AnsiColor.BRIGHT_BLACK} design for ${AnsiStyle.BOLD}${AnsiColor.DEFAULT}"
                    + property +
                    "${AnsiStyle.NORMAL}${AnsiColor.BRIGHT_BLACK} power by ";
        }else{
            property = "";
        }
        props.put("coffee.project.name", property);
        sources.addLast(new MapPropertySource("coffee", props));
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

}
