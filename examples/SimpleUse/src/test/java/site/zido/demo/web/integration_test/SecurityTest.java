package site.zido.demo.web.integration_test;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.ReflectionUtils;
import site.zido.coffee.security.authentication.phone.PhoneCodeCache;
import site.zido.demo.DemoApplication;
import site.zido.demo.config.AuthConfig;

import java.lang.reflect.Method;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DemoApplication.class)
@AutoConfigureMockMvc
public class SecurityTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private AuthConfig config;

    private PhoneCodeCache getCache() {
        Method getHttp = ReflectionUtils.findMethod(AuthConfig.class, "getHttp");
        Assert.assertNotNull(getHttp);
        ReflectionUtils.makeAccessible(getHttp);
        HttpSecurity http = (HttpSecurity) ReflectionUtils.invokeMethod(getHttp, config);
        Assert.assertNotNull(http);
        return http.getSharedObject(PhoneCodeCache.class);
    }

    @Test
    public void testAnonymous() throws Exception {
        mvc.perform(get("/hello").accept(MediaType.TEXT_PLAIN))
                .andExpect(status().is(401));
    }

    @Test
    public void testAuthenticated() throws Exception {
        mvc.perform(post("/users/sessions")
                .param("username", "user")
                .param("password", "user"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Authorization"))
                .andDo(result -> {
                    MockHttpServletResponse response = result.getResponse();
                    String authorization = response.getHeader("Authorization");
                    this.mvc.perform(get("/hello")
                            .header("Authorization", authorization)
                            .accept(MediaType.TEXT_PLAIN))
                            .andExpect(status().isOk())
                            .andExpect(content().string("hello world : user"));
                });
    }

    @Test
    public void testUserRoleAccessAdminApi() throws Exception {
        mvc.perform(post("/users/sessions")
                .param("username", "user")
                .param("password", "user"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Authorization"))
                .andDo(result -> {
                    MockHttpServletResponse response = result.getResponse();
                    String authorization = response.getHeader("Authorization");
                    this.mvc.perform(get("/admin")
                            .header("Authorization", authorization)
                            .accept(MediaType.TEXT_PLAIN))
                            .andExpect(status().isForbidden())
                            .andExpect(content().string(""));
                });
    }

    @Test
    public void testAdminRoleAccessUserApi() throws Exception {
        mvc.perform(post("/users/sessions")
                .param("username", "13512341235")
                .param("password", "xxx"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Authorization"))
                .andDo(result -> {
                    MockHttpServletResponse response = result.getResponse();
                    String authorization = response.getHeader("Authorization");
                    this.mvc.perform(get("/admin")
                            .header("Authorization", authorization)
                            .accept(MediaType.TEXT_PLAIN))
                            .andExpect(status().isOk())
                            .andExpect(content().string("hello world : 13512341235"));
                });
    }

    @Test
    public void testPhoneCodeLogin() throws Exception {
        String phone = "13512341235";
        mvc.perform(post("/phone/code").
                contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("phone", phone))
                .andExpect(status().isOk());
        String code = getCache().getCode(phone);
        mvc.perform(post("/phone/sessions")
                .param("phone", "13512341235")
                .param("code", code))
                .andExpect(status().isOk())
                .andExpect(header().exists("Authorization"))
                .andDo(result -> {
                    MockHttpServletResponse response = result.getResponse();
                    String authorization = response.getHeader("Authorization");
                    this.mvc.perform(get("/admin")
                            .header("Authorization", authorization)
                            .accept(MediaType.TEXT_PLAIN))
                            .andExpect(status().isOk())
                            .andExpect(content().string("hello world : 13512341235"));
                });
    }


}
