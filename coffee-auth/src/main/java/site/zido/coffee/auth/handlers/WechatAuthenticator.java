package site.zido.coffee.auth.handlers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import site.zido.coffee.auth.configurations.AuthProperties;
import site.zido.coffee.auth.entity.annotations.AuthColumnWechatOpenId;
import site.zido.coffee.auth.entity.annotations.AuthColumnWechatUnionId;
import site.zido.coffee.auth.exceptions.AuthenticationException;
import site.zido.coffee.auth.exceptions.InternalAuthenticationException;
import site.zido.coffee.auth.exceptions.NoSuchUserException;
import site.zido.coffee.auth.utils.WxMiniUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.lang.reflect.Field;

public class WechatAuthenticator<T, ID extends Serializable> implements Authenticator<T, ID>, InitializingBean {
    private static final String DEFAULT_WECHAT_OPEN_ID_FIELD_NAME = "wechatOpenId";
    private static final String DEFAULT_WECHAT_UNION_ID_FIELD_NAME = "wechatUnionId";
    private Field wechatOpenIdField;
    private Field wechatUnionIdField;
    private Class<T> userClass;
    private JpaRepository<T, ID> repository;
    private ObjectMapper mapper;
    private String appId;
    private String appSecret;
    private NoSuchUserHandler<T> noSuchUserHandler;

    public WechatAuthenticator() {
    }

    public WechatAuthenticator(AuthProperties properties, ObjectMapper mapper) {
        this.appId = properties.getWechatAppId();
        this.appSecret = properties.getWechatAppSecret();
        this.mapper = mapper;
    }

    @Override
    public boolean prepare(Class<T> userClass, JpaRepository<T, ID> repository) {
        if (appId == null || appSecret == null) {
            return false;
        }
        this.repository = repository;
        this.userClass = userClass;
        Field[] fields = userClass.getDeclaredFields();
        for (Field field : fields) {
            if (AnnotatedElementUtils.findMergedAnnotation(field, AuthColumnWechatOpenId.class) != null) {
                wechatOpenIdField = field;
            } else if (wechatOpenIdField != null && field.getName().equals(DEFAULT_WECHAT_OPEN_ID_FIELD_NAME)) {
                wechatOpenIdField = field;
            }
            if (AnnotatedElementUtils.findMergedAnnotation(field, AuthColumnWechatUnionId.class) != null) {
                wechatUnionIdField = field;
            } else if (wechatUnionIdField == null && field.getName().equals(DEFAULT_WECHAT_UNION_ID_FIELD_NAME)) {
                wechatUnionIdField = field;
            }
        }
        return wechatUnionIdField != null || wechatOpenIdField != null;
    }

    @Override
    public T auth(HttpServletRequest request) {
        String encryptedData = request.getParameter("encryptedData");
        String iv = request.getParameter("iv");
        String code = request.getParameter("code");
        if (StringUtils.hasText(encryptedData)
                && StringUtils.hasText(iv)
                && StringUtils.hasText(code)) {
            String dataJsonStr;
            JsonNode jsonNode;
            try {
                dataJsonStr = WxMiniUtil.decryptEncryptedData(code, appId, appSecret, encryptedData, iv);
                jsonNode = mapper.readTree(dataJsonStr);

            } catch (Exception e) {
                throw new InternalAuthenticationException("微信解密错误", e);
            }

            String unionId = jsonNode.get("unionId").asText();
            String openId = jsonNode.get("openId").asText();
            T tempUser;
            if (StringUtils.hasText(unionId) && wechatUnionIdField != null) {
                try {
                    tempUser = userClass.newInstance();
                    ReflectionUtils.setField(wechatUnionIdField, tempUser, unionId);
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new InternalAuthenticationException("加载用户发生错误", e);
                }
            } else {
                try {
                    tempUser = userClass.newInstance();
                    ReflectionUtils.setField(wechatOpenIdField, tempUser, openId);
                } catch (IllegalAccessException | InstantiationException e) {
                    throw new InternalAuthenticationException("加载用户发生错误", e);
                }
            }
            String nickName = jsonNode.get("nickName").asText();
            String avatarUrl = jsonNode.get("avatarUrl").asText();
            Integer gender = jsonNode.get("gender").asInt();
            T user = repository.findOne(Example.of(tempUser));
            if (user == null) {
                if (noSuchUserHandler != null) {
                    user = noSuchUserHandler.handle(nickName, avatarUrl, gender, openId, unionId);
                }
                if (user == null) {
                    throw new NoSuchUserException();
                }
            }
            return user;
        }
        return null;
    }


    public void setMapper(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public void setNoSuchUserHandler(NoSuchUserHandler<T> noSuchUserHandler) {
        this.noSuchUserHandler = noSuchUserHandler;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(mapper, "json object mapper can't be null");
    }
}
