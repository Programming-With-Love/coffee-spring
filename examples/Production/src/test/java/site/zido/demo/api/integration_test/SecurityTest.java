package site.zido.demo.api.integration_test;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import site.zido.demo.entity.Admin;
import site.zido.demo.entity.User;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureTestEntityManager
@AutoConfigureMockMvc
@Transactional
public class SecurityTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private TestEntityManager manager;
    @Autowired
    private PasswordEncoder encoder;

    @Before
    public void setUp() {
        User user = User.registerBuilder()
                .username("user")
                .password(encoder.encode("user"))
                .phone("13512341234")
                .build();
        manager.persist(user);
        Admin admin = Admin.builder()
                .username("admin")
                .password(encoder.encode("admin"))
                .build();
        manager.persist(admin);
    }

    @Test
    public void testAnonymous() throws Exception {
        mvc.perform(get("/rooms"))
                .andExpect(status().isOk());
    }

    @Test
    public void testAuthenticated() throws Exception {
        mvc.perform(post("/users/sessions")
                .header("role", "admin")
                .param("username", "admin")
                .param("password", "admin"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Authorization"))
                .andDo(result -> {
                    MockHttpServletResponse response = result.getResponse();
                    String authorization = response.getHeader("Authorization");
                    this.mvc.perform(get("/admin/users")
                            .header("Authorization", authorization)
                            .accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isOk())
                            .andDo(print());
                });
    }
}
