package site.zido.coffee.auth.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

import java.io.IOException;

/**
 * 微信小程序工具类
 * <p>
 * 注意： 1.一个code只能使用一次，小程序可以wx.login获取新的code
 * 2.session_key有时效性，wx.login后也会失效
 */
public class WxMiniUtil {

    /**
     * 解密小程序加密信息
     *
     * @param sessionKey
     * @param encryptedData
     * @param iv
     * @return 返回解密后的json字符串
     * @throws Exception wx.getUserInfo的encryptedData解密后的json结构
     *                   {
     *                   "openId": "OPENID",
     *                   "nickName": "NICKNAME",
     *                   "gender": GENDER,
     *                   "city": "CITY",
     *                   "province": "PROVINCE",
     *                   "country": "COUNTRY",
     *                   "avatarUrl": "AVATARURL",
     *                   "unionId": "UNIONID",
     *                   "watermark":
     *                   {
     *                   "appid":"APPID",
     *                   "timestamp":TIMESTAMP
     *                   }
     *                   }
     */
    public static String decryptEncryptedData(String sessionKey, String encryptedData, String iv) throws Exception {
        String decryptedData = AesCbcUtil.decrypt(encryptedData, sessionKey, iv, "UTF-8");
        return decryptedData;
    }

    public static String decryptEncryptedData(String code, String appId, String appSecret, String encryptedData, String iv) throws Exception {
        String sessionKey = getSessionKey(code, appId, appSecret);
        String decryptedData = AesCbcUtil.decrypt(encryptedData, sessionKey, iv, "UTF-8");
        return decryptedData;
    }

    /**
     * 获取用户微信unionId
     *
     * @param code          一个只能使用一次
     * @param appId
     * @param appSecret
     * @param encryptedData
     * @param iv
     * @return
     * @throws Exception
     */
    public static String getUnionId(String code, String appId, String appSecret, String encryptedData, String iv) throws Exception {
        String sessionKey = getSessionKey(code, appId, appSecret);
        String unionId = getUnionId(sessionKey, encryptedData, iv);
        return unionId;
    }

    public static String getUnionId(String sessionKey, String encryptedData, String iv) throws Exception {
        String unionId = getEncryptedDataNodeValueAsStr(sessionKey, encryptedData, iv, "unionId");
        return unionId;
    }

    /**
     * 获取用户微信绑定的手机号（国外手机号会有区号，如中国86）
     *
     * @param code          一个只能使用一次
     * @param appId
     * @param appSecret
     * @param encryptedData
     * @param iv
     * @return
     * @throws Exception
     */
    public static String getPhoneNumber(String code, String appId, String appSecret, String encryptedData, String iv) throws Exception {
        String sessionKey = getSessionKey(code, appId, appSecret);
        String phoneNumber = getPhoneNumber(sessionKey, encryptedData, iv);
        return phoneNumber;
    }

    public static String getPhoneNumber(String sessionKey, String encryptedData, String iv) throws Exception {
        String phoneNumber = getEncryptedDataNodeValueAsStr(sessionKey, encryptedData, iv, "phoneNumber");
        return phoneNumber;
    }

    /**
     * 获取openid
     *
     * @param code      一个只能使用一次
     * @param appId
     * @param appSecret
     * @return
     * @throws Exception
     */
    public static String getOpenid(String code, String appId, String appSecret) throws Exception {
        String jsonEntity = login(code, appId, appSecret);
        String openid = getJsonNodeValueAsString(jsonEntity, "openid");
        return openid;
    }

    /**
     * 获取小程序session_key，有失效时间
     *
     * @param code      一个只能使用一次
     * @param appId
     * @param appSecret
     * @return
     * @throws Exception
     */
    public static String getSessionKey(String code, String appId, String appSecret) throws Exception {
        String jsonEntity = login(code, appId, appSecret);
        String sessionKey = getJsonNodeValueAsString(jsonEntity, "session_key");
        return sessionKey;
    }

    /**
     * 微信小程序login
     *
     * @param code      一个只能使用一次
     * @param appId
     * @param appSecret
     * @return 返回json格式的字符串（openid和session_key）
     * <p>
     * 正常返回的JSON数据包:
     * { "openid":"OPENID", "session_key": "SESSIONKEY", "unionid": "UNIONID" }
     * <p>
     * 错误时返回JSON数据包(示例为Code无效):
     * { "errcode": 40029, "errmsg": "invalidcode" }
     * @throws Exception
     */
    public static String login(String code, String appId, String appSecret) throws Exception {
        String url = getLoginURL(code, appId, appSecret);
        String jsonEntity = HttpsUtils.post(url, null, null, null);
        return jsonEntity;
    }

    /**
     * 解密EncryptedDataNode，并获得node节点的value
     *
     * @param sessionKey
     * @param encryptedData
     * @param iv
     * @return
     * @throws Exception
     */
    private static String getEncryptedDataNodeValueAsStr(String sessionKey, String encryptedData, String iv, String nodeName) throws Exception {
        String decryptedData = AesCbcUtil.decrypt(encryptedData, sessionKey, iv, "UTF-8");
        String phoneNumber = getJsonNodeValueAsString(decryptedData, nodeName);
        return phoneNumber;
    }

    /**
     * 解析json字符串
     *
     * @param jsonStr
     * @param nodeName json字符串节点名
     * @return 返回节点字符串值
     * @throws JsonProcessingException
     * @throws IOException
     */
    private static String getJsonNodeValueAsString(String jsonStr, String nodeName) throws JsonProcessingException, IOException {
        ObjectMapper jackson = new ObjectMapper();
        JsonNode entityNode = jackson.readTree(jsonStr);
        JsonNode node = entityNode.get(nodeName);
        String nodeValue = null;
        if (node != null) {
            nodeValue = node.asText();
        }
        return nodeValue;
    }

    /**
     * 微信小程序login接口
     *
     * @param code
     * @return
     */
    private static String getLoginURL(String code, String appId, String appSecret) {
        String URL = "https://api.weixin.qq.com/sns/jscode2session?appId=" + appId + "&secret=" + appSecret
                + "&js_code=" + code + "&grant_type=authorization_code";
        return URL;
    }

    public static String getAccessToken(String appid, String secret) throws Exception {
        String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + appid + "&secret=" + secret;
        String entity = HttpsUtils.post(url, null, null, null);
        String access_token = getJsonNodeValueAsString(entity, "access_token");
        return access_token;
    }

    public static String sendTemplateMsg(String jsonParam, String access_token) throws Exception {
        String url = "https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send?access_token=" + access_token;
        HttpEntity entity = new StringEntity(jsonParam, ContentType.APPLICATION_JSON);
        String result = HttpsUtils.post(url, null, null, entity);
        return result;
    }

    public static String sendTemplateMsg(String jsonParam, String appId, String appSecret) throws Exception {
        String access_token = getAccessToken(appId, appSecret);
        String url = "https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send?access_token=" + access_token;
        HttpEntity entity = new StringEntity(jsonParam, ContentType.APPLICATION_JSON);
        String result = HttpsUtils.post(url, null, null, entity);
        return result;
    }
}
