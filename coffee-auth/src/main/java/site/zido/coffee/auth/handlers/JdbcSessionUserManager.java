package site.zido.coffee.auth.handlers;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.ReflectionUtils;
import site.zido.coffee.auth.entity.IUser;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class JdbcSessionUserManager extends AbstractSessionUserManager {
    private JdbcTemplate template;
    private static final Map<Class<? extends IUser>, TableVal> tableCache =
            new ConcurrentHashMap<>(3);

    public JdbcSessionUserManager(JdbcTemplate template) {
        this.template = template;
    }

    @Override
    protected IUser getUserByKey(Object fieldValue, String fieldName, Class<? extends IUser> userClass) {
        //TODO
//        tableCache.computeIfAbsent(userClass, new Function<Class<? extends IUser>, TableVal>() {
//            @Override
//            public TableVal apply(Class<? extends IUser> clazz) {
//
//            }
//        })
//        template.query("select * from " + fieldName + " where id = ?", new Object[]{fieldName}, new RowMapper<Object>() {
//            @Override
//            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
//                return null;
//            }
//        });
//        ReflectionUtils.doWithFields(userClass, new ReflectionUtils.FieldCallback() {
//            @Override
//            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
//
//            }
//        });
        return null;
    }


    private class TableVal {
        private String tableName;
        private String columnName;
    }
}
