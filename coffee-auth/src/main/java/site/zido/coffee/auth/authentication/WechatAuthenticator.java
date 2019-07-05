package site.zido.coffee.auth.authentication;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import site.zido.coffee.auth.entity.IUser;
import site.zido.coffee.auth.entity.annotations.AuthColumnWechatOpenId;
import site.zido.coffee.auth.entity.annotations.AuthColumnWechatUnionId;
import site.zido.coffee.auth.exceptions.AbstractAuthenticationException;
import site.zido.coffee.auth.exceptions.InternalAuthenticationException;
import site.zido.coffee.auth.exceptions.NoSuchUserException;
import site.zido.coffee.auth.handlers.NoSuchUserHandler;
import site.zido.coffee.auth.utils.WxMiniUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 微信登陆认证器
 * <p>
 * 默认使用 <code>wechatOpenId</code>或者<code>wechatUnionId</code>作为默认登陆凭证
 *
 * @author zido
 */
public class WechatAuthenticator implements Authenticator, InitializingBean {
    private static final String DEFAULT_WECHAT_OPEN_ID_FIELD_NAME = "wechatOpenId";
    private static final String DEFAULT_WECHAT_UNION_ID_FIELD_NAME = "wechatUnionId";
    private Map<Class<? extends IUser>, WechatClassProps> propsCache =
            new HashMap<>();
    /**
     * 微信appId
     */
    private String appId;
    /**
     * 微信appSecret
     */
    private String appSecret;
    /**
     * 用户未查询到处理器
     */
    private NoSuchUserHandler<? extends IUser> noSuchUserHandler;
    private ObjectMapper mapper;

    public WechatAuthenticator() {
    }

    @Override
    public boolean prepare(Class<? extends IUser> userClass,
                           JpaRepository<? extends IUser, ? extends Serializable> repository) {
        if (propsCache.containsKey(userClass)) {
            return true;
        }
        if (appId == null || appSecret == null) {
            return false;
        }
        WechatClassProps props = new WechatClassProps();
        props.setRepository(repository);
        props.setUserClass(userClass);
        ReflectionUtils.doWithFields(userClass, field -> {
            if (AnnotatedElementUtils.findMergedAnnotation(field, AuthColumnWechatOpenId.class) != null) {
                props.setWechatOpenIdField(field);
            } else if (props.getWechatOpenIdField() != null && field.getName().equals(DEFAULT_WECHAT_OPEN_ID_FIELD_NAME)) {
                props.setWechatOpenIdField(field);
            }
            if (AnnotatedElementUtils.findMergedAnnotation(field, AuthColumnWechatUnionId.class) != null) {
                props.setWechatUnionIdField(field);
            } else if (props.getWechatOpenIdField() == null && field.getName().equals(DEFAULT_WECHAT_UNION_ID_FIELD_NAME)) {
                props.setWechatUnionIdField(field);
            }
        });
        props.setNoSuchUserHandler(noSuchUserHandler);
        props.setAppId(appId);
        props.setAppSecret(appSecret);
        return (props.getWechatOpenIdField() != null || props.getWechatUnionIdField() != null)
                && propsCache.put(userClass, props) == null;
    }

    /**
     * 认证请求，优先使用unionId，目前只支持application/x-www-form-urlencoded
     *
     * @param request 请求
     * @return 用户
     * @throws AbstractAuthenticationException 当认证失败时会抛出{@link NoSuchUserException}
     */
    @Override
    public IUser auth(HttpServletRequest request) throws AbstractAuthenticationException {
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
            for (WechatClassProps props : propsCache.values()) {
                IUser tempUser;
                if (StringUtils.hasText(unionId) && props.getWechatUnionIdField() != null) {
                    try {
                        tempUser = props.getUserClass().newInstance();
                        ReflectionUtils.setField(props.getWechatUnionIdField(), tempUser, unionId);
                    } catch (InstantiationException | IllegalAccessException e) {
                        throw new InternalAuthenticationException("加载用户发生错误", e);
                    }
                } else {
                    try {
                        tempUser = props.getUserClass().newInstance();
                        ReflectionUtils.setField(props.getWechatOpenIdField(), tempUser, openId);
                    } catch (IllegalAccessException | InstantiationException e) {
                        throw new InternalAuthenticationException("加载用户发生错误", e);
                    }
                }
                String nickName = jsonNode.get("nickName").asText();
                String avatarUrl = jsonNode.get("avatarUrl").asText();
                Integer gender = jsonNode.get("gender").asInt();
                IUser user = props.getRepository().findOne((Example) Example.of(tempUser));
                if (user != null) {
                    return user;
                }
                if (props.getNoSuchUserHandler() != null) {
                    user = props.getNoSuchUserHandler().handle(nickName, avatarUrl, gender, openId, unionId);
                    if (user == null) {
                        throw new NoSuchUserException();
                    }
                }
            }
            throw new NoSuchUserException();
        }
        return null;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    @Autowired(required = false)
    public void setNoSuchUserHandler(NoSuchUserHandler<? extends IUser> noSuchUserHandler) {
        this.noSuchUserHandler = noSuchUserHandler;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(mapper, "object mapper can't be null");
    }

    @Autowired
    public void setMapper(ObjectMapper mapper) {
        this.mapper = mapper;
    }
}
