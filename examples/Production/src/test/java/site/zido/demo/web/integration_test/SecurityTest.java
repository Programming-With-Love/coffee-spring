package site.zido.demo.web.integration_test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@AutoConfigureTestEntityManager
@AutoConfigureMockMvc
public class SecurityTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private TestEntityManager manager;

//    @Before
//    @Transactional
//    public void setUp() {
//        User user = User.registerBuilder().username("user").password("user").build();
//        manager.persist(user);
//        Admin admin = Admin.builder().username("admin").password("admin").build();
//        manager.persist(admin);
//    }

    @Test
    public void testAnonymous() throws Exception {
        mvc.perform(get("/rooms"))
                .andExpect(status().isOk());
    }

    @Test
    public void testAuthenticated() throws Exception {
        mvc.perform(post("/admin/sessions")
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
