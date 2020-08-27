package site.zido.coffee.autoconfigure.security.rest;

import org.springframework.boot.autoconfigure.AutoConfigurationImportFilter;
import org.springframework.boot.autoconfigure.AutoConfigurationMetadata;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class WebSecurityAutoFilter implements AutoConfigurationImportFilter {
    private static final Set<String> SHOULD_SKIP = new HashSet<>(
            Arrays.asList("org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration",
                    "org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration")
    );

    @Override
    public boolean[] match(String[] classNames, AutoConfigurationMetadata metadata) {
        boolean[] matches = new boolean[classNames.length];

        for (int i = 0; i < classNames.length; i++) {
            matches[i] = !SHOULD_SKIP.contains(classNames[i]);
        }
        return matches;
    }
}
