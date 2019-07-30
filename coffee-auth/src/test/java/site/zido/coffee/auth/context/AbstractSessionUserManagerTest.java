package site.zido.coffee.auth.context;

import org.junit.Assert;
import org.junit.Test;
import site.zido.coffee.auth.context.pkg.MockedAuthColumnUser;
import site.zido.coffee.auth.context.pkg.MockedJavaxUser;
import site.zido.coffee.auth.context.pkg.MockedNoPKUser;
import site.zido.coffee.auth.context.pkg.MockedSpringUser;
import site.zido.coffee.auth.entity.IUser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.util.Collection;
import java.util.Collections;

import static org.mockito.Mockito.*;
import static site.zido.coffee.auth.Constants.DEFAULT_SESSION_ATTRIBUTE_NAME;

public class AbstractSessionUserManagerTest {
    @Test
    public void testBindJavaxIdUser() {
        AbstractSessionUserManager um = mock(AbstractSessionUserManager.class, CALLS_REAL_METHODS);
        MockedJavaxUser user = new MockedJavaxUser();
        user.setId(100);
        user.setUsername("a");
        user.setPassword("b");
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(true)).thenReturn(session);
        um.bindUser(request, user);
        verify(session).setAttribute(DEFAULT_SESSION_ATTRIBUTE_NAME, user.getId());
        verify(session).setAttribute(DEFAULT_SESSION_ATTRIBUTE_NAME + ".class", user.getClass());
        verify(session).setAttribute(DEFAULT_SESSION_ATTRIBUTE_NAME + ".name", "id");
    }

    @Test
    public void testBindSpringIdUser() {
        AbstractSessionUserManager um = mock(AbstractSessionUserManager.class, CALLS_REAL_METHODS);
        MockedSpringUser user = new MockedSpringUser();
        user.setId(100);
        user.setUsername("a");
        user.setPassword("b");
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(true)).thenReturn(session);
        um.bindUser(request, user);
        verify(session).setAttribute(DEFAULT_SESSION_ATTRIBUTE_NAME, user.getId());
        verify(session).setAttribute(DEFAULT_SESSION_ATTRIBUTE_NAME + ".class", user.getClass());
        verify(session).setAttribute(DEFAULT_SESSION_ATTRIBUTE_NAME + ".name", "id");
    }

    @Test
    public void testBindAuthColumnUser() {
        AbstractSessionUserManager um = mock(AbstractSessionUserManager.class, CALLS_REAL_METHODS);
        MockedAuthColumnUser user = new MockedAuthColumnUser();
        user.setId(100);
        user.setUsername("a");
        user.setPassword("b");
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(true)).thenReturn(session);
        um.bindUser(request, user);
        verify(session).setAttribute(DEFAULT_SESSION_ATTRIBUTE_NAME, user.getUsername());
        verify(session).setAttribute(DEFAULT_SESSION_ATTRIBUTE_NAME + ".class", user.getClass());
        verify(session).setAttribute(DEFAULT_SESSION_ATTRIBUTE_NAME + ".name", "username");
    }

    @Test
    public void TestBindNoPKUser_should_throw_ex() {
        AbstractSessionUserManager um = mock(AbstractSessionUserManager.class, CALLS_REAL_METHODS);
        MockedNoPKUser user = new MockedNoPKUser();
        user.setId(100);
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(true)).thenReturn(session);
        try {
            um.bindUser(request, user);
        } catch (IllegalStateException e) {
            String message = e.getMessage();
            Assert.assertEquals("deference", "the user entity should by annotated by javax.persistence.Id" +
                    " or org.springframework.data.annotation" +
                    " or site.zido.coffee.auth.entity.annotations.AuthColumnKey", message);
            return;
        }
        Assert.fail("unreachable");
    }


    @Test
    public void testGetCurrentUser() {
        Integer id = 100;
        MockedJavaxUser user = new MockedJavaxUser();
        //mock
        AbstractSessionUserManager um = mock(AbstractSessionUserManager.class, CALLS_REAL_METHODS);
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(false))
                .thenReturn(session);
        when(request.getSession(true))
                .thenThrow(new AssertionError("can't call getSession(true)"));
        when(request.getSession())
                .thenThrow(new AssertionError("can't call getSession()"));
        when(session.getAttribute(DEFAULT_SESSION_ATTRIBUTE_NAME))
                .thenReturn(id);
        when(session.getAttribute(DEFAULT_SESSION_ATTRIBUTE_NAME + ".name"))
                .thenReturn("id");
        when(session.getAttribute(DEFAULT_SESSION_ATTRIBUTE_NAME + ".class"))
                .thenReturn(MockedJavaxUser.class);
        when(um.getUserByKey(id, "id", MockedJavaxUser.class))
                .thenReturn(user);
        //call
        IUser currentUser = um.getCurrentUser(request);
        IUser currentUser2 = um.getCurrentUser(request);
        //verify
        verify(session,
                atMost(1)).getAttribute(DEFAULT_SESSION_ATTRIBUTE_NAME);
        verify(session,
                atMost(1)).getAttribute(DEFAULT_SESSION_ATTRIBUTE_NAME + ".name");
        verify(session,
                atMost(1)).getAttribute(DEFAULT_SESSION_ATTRIBUTE_NAME + ".class");
        verify(um,
                atMost(1)).getUserByKey(id, "id", MockedJavaxUser.class);
        Assert.assertEquals(user, currentUser);
        Assert.assertEquals(user, currentUser2);
    }

    @Test
    public void testGetRoles_with_default_roles() {
        MockedJavaxUser user = new MockedJavaxUser();
        AbstractSessionUserManager um = mock(AbstractSessionUserManager.class, CALLS_REAL_METHODS);
        Collection<String> roles = um.getRoles(user);
        Assert.assertEquals(Collections.singleton("user"), roles);
    }

    @Test
    public void testGetRoles_with_custom_roles() {
        MockedJavaxUser user = new MockedJavaxUser() {
            private static final long serialVersionUID = 3272081631333678692L;

            @Override
            public String role() {
                return "javax";
            }
        };
        AbstractSessionUserManager um = mock(AbstractSessionUserManager.class, CALLS_REAL_METHODS);
        Collection<String> roles = um.getRoles(user);
        Assert.assertEquals(Collections.singleton("javax"), roles);
    }
}
