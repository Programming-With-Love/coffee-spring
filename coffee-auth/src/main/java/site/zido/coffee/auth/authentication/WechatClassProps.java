package site.zido.coffee.auth.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.jpa.repository.JpaRepository;
import site.zido.coffee.auth.entity.IUser;
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
    private Class<? extends IUser> userClass;
    private JpaRepository<? extends IUser, ? extends Serializable> repository;
    private ObjectMapper mapper;
    private String appId;
    private String appSecret;
    private NoSuchUserHandler<? extends IUser> noSuchUserHandler;

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

    public Class<? extends IUser> getUserClass() {
        return userClass;
    }

    public void setUserClass(Class<? extends IUser> userClass) {
        this.userClass = userClass;
    }

    public JpaRepository<? extends IUser, ? extends Serializable> getRepository() {
        return repository;
    }

    public void setRepository(JpaRepository<? extends IUser, ? extends Serializable> repository) {
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

    public NoSuchUserHandler<? extends IUser> getNoSuchUserHandler() {
        return noSuchUserHandler;
    }

    public void setNoSuchUserHandler(NoSuchUserHandler<? extends IUser> noSuchUserHandler) {
        this.noSuchUserHandler = noSuchUserHandler;
    }
}
