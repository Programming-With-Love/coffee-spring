package site.zido.coffee.auth.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.jpa.repository.JpaRepository;
import site.zido.coffee.auth.user.IDUser;
import site.zido.coffee.auth.handlers.NoSuchUserHandler;

import java.io.Serializable;
import java.lang.reflect.Field;

/**
 * 微信登陆相关属性
 *
 * @author zido
 */
public class WechatClassProps {
    private Field wechatOpenIdField;
    private Field wechatUnionIdField;
    private Class<? extends IDUser> userClass;
    private JpaRepository<? extends IDUser, ? extends Serializable> repository;
    private ObjectMapper mapper;
    private String appId;
    private String appSecret;
    private NoSuchUserHandler<? extends IDUser> noSuchUserHandler;

    public Field getWechatOpenIdField() {
        return wechatOpenIdField;
    }

    public void setWechatOpenIdField(Field wechatOpenIdField) {
        this.wechatOpenIdField = wechatOpenIdField;
    }

    public Field getWechatUnionIdField() {
        return wechatUnionIdField;
    }

    public void setWechatUnionIdField(Field wechatUnionIdField) {
        this.wechatUnionIdField = wechatUnionIdField;
    }

    public Class<? extends IDUser> getUserClass() {
        return userClass;
    }

    public void setUserClass(Class<? extends IDUser> userClass) {
        this.userClass = userClass;
    }

    public JpaRepository<? extends IDUser, ? extends Serializable> getRepository() {
        return repository;
    }

    public void setRepository(JpaRepository<? extends IDUser, ? extends Serializable> repository) {
        this.repository = repository;
    }

    public ObjectMapper getMapper() {
        return mapper;
    }

    public void setMapper(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public NoSuchUserHandler<? extends IDUser> getNoSuchUserHandler() {
        return noSuchUserHandler;
    }

    public void setNoSuchUserHandler(NoSuchUserHandler<? extends IDUser> noSuchUserHandler) {
        this.noSuchUserHandler = noSuchUserHandler;
    }
}
