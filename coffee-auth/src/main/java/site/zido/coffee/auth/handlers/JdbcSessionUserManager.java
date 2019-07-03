package site.zido.coffee.auth.handlers;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.ReflectionUtils;
import site.zido.coffee.auth.entity.IUser;

import java.lang.reflect.Field;

public class JdbcSessionUserManager extends AbstractSessionUserManager {
    private JdbcTemplate template;

    public JdbcSessionUserManager(JdbcTemplate template) {
        this.template = template;
    }

    @Override
    protected IUser getUserByKey(Object key, Class<? extends IUser> userClass) {

        ReflectionUtils.doWithFields(userClass, new ReflectionUtils.FieldCallback() {
            @Override
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {

            }
        });
        return null;
    }
}
