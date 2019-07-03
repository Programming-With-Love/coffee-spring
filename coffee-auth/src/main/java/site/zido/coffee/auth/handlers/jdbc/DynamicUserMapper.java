package site.zido.coffee.auth.handlers.jdbc;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.ReflectionUtils;
import site.zido.coffee.auth.entity.IUser;
import site.zido.coffee.auth.utils.FieldUtils;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 动态的用户mapper
 *
 * @author zido
 */
public class DynamicUserMapper implements RowMapper<IUser> {
    private Class<? extends IUser> userClass;
    private final Collection<Field> fields;

    public DynamicUserMapper(Class<? extends IUser> userClass) {
        this.userClass = userClass;
        try {
            instanceUser(userClass);
        } catch (Throwable ex) {
            throw new RuntimeException("预先检查用户实例化失败", ex);
        }
        List<Field> list = new LinkedList<>();
        ReflectionUtils.doWithFields(userClass, list::add);
        fields = Collections.unmodifiableCollection(list);
    }

    protected IUser instanceUser(Class<? extends IUser> userClass) {
        try {
            return userClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("用户类实例化失败", e);
        }
    }

    @Override
    public IUser mapRow(ResultSet rs, int rowNum) throws SQLException {
        IUser user = instanceUser(userClass);
        for (Field field : fields) {
            String name = field.getName();
            Object value;
            if ((value = rs.getObject(name, field.getType())) != null) {
                FieldUtils.injectFieldBySetter(field, user, value);
            }
        }
        return user;
    }
}
