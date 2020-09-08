package site.zido.coffee.autoconfigure.security.rest;

import org.springframework.boot.autoconfigure.AutoConfigurationImportFilter;
import org.springframework.boot.autoconfigure.AutoConfigurationMetadata;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class WebSecurityAutoFilter implements AutoConfigurationImportFilter, EnvironmentAware {
    private static final Set<String> WEB_SECURITY_SKIP = new HashSet<>(
            Arrays.asList("org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration",
                    "org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration")
    );

    private static final Set<String> REST_SECURITY_SKIP = new HashSet<>(
            Arrays.asList("site.zido.coffee.autoconfigure.security.rest.RestSecurityFilterAutoConfiguration",
                    "site.zido.coffee.autoconfigure.security.rest.RestSecurityAutoConfiguration")
    );

    private boolean useRest = true;

    @Override
    public boolean[] match(String[] classNames, AutoConfigurationMetadata metadata) {
        boolean[] matches = new boolean[classNames.length];

        for (int i = 0; i < classNames.length; i++) {
            matches[i] = !(useRest ? WEB_SECURITY_SKIP : REST_SECURITY_SKIP).contains(classNames[i]);
        }
        return matches;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.useRest = "JWT".equalsIgnoreCase(environment.getProperty("spring.security.secureStoreType", "JWT"));
    }
}
