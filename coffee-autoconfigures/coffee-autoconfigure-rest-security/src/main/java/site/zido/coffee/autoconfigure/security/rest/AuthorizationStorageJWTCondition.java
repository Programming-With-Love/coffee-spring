package site.zido.coffee.autoconfigure.security.rest;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class AuthorizationStorageJWTCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String storeType = context.getEnvironment().getProperty("spring.security.secureStoreType", "JWT");
        return SecureStoreType.JWT.name().equalsIgnoreCase(storeType);
    }
}
