package site.zido.demo.api.integration_test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.ReflectionUtils;
import site.zido.coffee.autoconfigure.security.rest.SpringBootRestSecurityConfiguration;
import site.zido.coffee.security.authentication.phone.PhoneCodeCache;
import site.zido.demo.DemoApplication;

import java.lang.reflect.Method;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = DemoApplication.class)
@AutoConfigureMockMvc
public class SecurityTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ApplicationContext context;

    private PhoneCodeCache getCache() {
        Method getHttp = ReflectionUtils.findMethod(SpringBootRestSecurityConfiguration.DefaultRestSecurityConfigurerAdapter.class, "getHttp");
        Assertions.assertNotNull(getHttp);
        ReflectionUtils.makeAccessible(getHttp);
        SpringBootRestSecurityConfiguration.DefaultRestSecurityConfigurerAdapter configure = context.getBean(SpringBootRestSecurityConfiguration.DefaultRestSecurityConfigurerAdapter.class);
        HttpSecurity http = (HttpSecurity) ReflectionUtils.invokeMethod(getHttp, configure);
        Assertions.assertNotNull(http);
        return http.getSharedObject(PhoneCodeCache.class);
    }

    @Test
    public void testAnonymous() throws Exception {
        mvc.perform(get("/hello").accept(MediaType.TEXT_PLAIN))
                .andExpect(status().is(401))
                .andExpect(content().string(""));
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
                            .accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isOk())
                            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                            .andExpect(content().json("{\"result\":\"hello world : user\",\"code\":0}"));
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
                            .accept(MediaType.APPLICATION_JSON))
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
                            .accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isOk())
                            .andExpect(content().json("{\"result\":\"hello world : 13512341235\",\"code\":0}"));
                });
    }

    @Test
    public void testPhoneCodeLogin() throws Exception {
        String phone = "13512341235";
        mvc.perform(post("/users/sms/code").
                contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("phone", phone))
                .andExpect(status().isOk());
        String code = getCache().getCode(phone);
        mvc.perform(post("/users/sms/sessions")
                .param("phone", "13512341235")
                .param("code", code))
                .andExpect(status().isOk())
                .andExpect(header().exists("Authorization"))
                .andDo(result -> {
                    MockHttpServletResponse response = result.getResponse();
                    String authorization = response.getHeader("Authorization");
                    this.mvc.perform(get("/admin")
                            .header("Authorization", authorization)
                            .accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isOk())
                            .andExpect(content().json("{\"result\":\"hello world : 13512341235\",\"code\":0}"));
                });
    }


}
