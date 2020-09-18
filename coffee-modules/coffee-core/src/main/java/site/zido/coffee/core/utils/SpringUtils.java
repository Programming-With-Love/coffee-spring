package site.zido.coffee.core.utils;

import org.springframework.context.ApplicationContext;

public final class SpringUtils {
    private SpringUtils() {
    }

    public static <T> T getBeanOrNull(ApplicationContext context, Class<T> type) {
        String[] userDetailsBeanNames = context.getBeanNamesForType(type);
        if (userDetailsBeanNames.length != 1) {
            return null;
        }

        return context.getBean(userDetailsBeanNames[0], type);
    }
}
