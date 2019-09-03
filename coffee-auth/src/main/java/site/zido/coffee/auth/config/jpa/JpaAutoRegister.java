package site.zido.coffee.auth.config.jpa;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.orm.jpa.persistenceunit.MutablePersistenceUnitInfo;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitPostProcessor;
import site.zido.coffee.auth.config.AuthClassAutoRegistrar;
import site.zido.coffee.auth.user.annotations.AuthEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * unit info由spring boot data jpa注册，优先使用spring boot data jpa的扫描结果，
 * 以尽量兼容spring boot data jpa的各种规范
 */
public class JpaAutoRegister implements PersistenceUnitPostProcessor,
        AuthClassAutoRegistrar {
    private static final Logger LOGGER = LoggerFactory.getLogger(JpaAutoRegister.class);
    private List<String> authClassNames;

    @Override
    public void postProcessPersistenceUnitInfo(MutablePersistenceUnitInfo pui) {
        authClassNames = new ArrayList<>();
        List<String> entityClassNames = pui.getManagedClassNames();
        for (String entityClassName : entityClassNames) {
            Class<?> clazz;
            try {
                clazz = JpaAutoRegister.class.getClassLoader().loadClass(entityClassName);
            } catch (ClassNotFoundException e) {
                LOGGER.error("can't load " + entityClassName, e);
                continue;
            }
            AuthEntity authEntity = AnnotatedElementUtils.findMergedAnnotation(clazz, AuthEntity.class);
            if (authEntity != null) {
                authClassNames.add(entityClassName);
            }
        }
    }

    @Override
    public List<String> getAuthClassNames() {
        return authClassNames;
    }

}
