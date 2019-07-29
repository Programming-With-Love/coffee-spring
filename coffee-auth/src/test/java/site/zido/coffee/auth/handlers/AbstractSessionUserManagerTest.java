package site.zido.coffee.auth.handlers;

import org.junit.Test;
import site.zido.coffee.auth.entity.IUser;
import site.zido.coffee.auth.handlers.entity.MockedAuthColumnUser;
import site.zido.coffee.auth.handlers.entity.MockedJavaxUser;
import site.zido.coffee.auth.handlers.entity.MockedSpringUser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static org.mockito.Mockito.*;
import static site.zido.coffee.auth.Constants.DEFAULT_SESSION_ATTRIBUTE_NAME;

public class AbstractSessionUserManagerTest {
    @Test
    public void testBindJavaxIdUser() {
        AbstractSessionUserManager userManager = new AbstractSessionUserManager() {
            @Override
            protected IUser getUserByKey(Object fieldValue, String fieldName, Class<? extends IUser> userClass) {
                return null;
            }
        };
        MockedJavaxUser user = new MockedJavaxUser();
        user.setId(100);
        user.setUsername("a");
        user.setPassword("b");
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(true)).thenReturn(session);
        userManager.bindUser(request, user);
        verify(session).setAttribute(DEFAULT_SESSION_ATTRIBUTE_NAME, user.getId());
        verify(session).setAttribute(DEFAULT_SESSION_ATTRIBUTE_NAME + ".class", user.getClass());
        verify(session).setAttribute(DEFAULT_SESSION_ATTRIBUTE_NAME + ".name", "id");
    }

    @Test
    public void testBindSpringIdUser() {
        AbstractSessionUserManager userManager = new AbstractSessionUserManager() {
            @Override
            protected IUser getUserByKey(Object fieldValue, String fieldName, Class<? extends IUser> userClass) {
                return null;
            }
        };
        MockedSpringUser user = new MockedSpringUser();
        user.setId(100);
        user.setUsername("a");
        user.setPassword("b");
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(true)).thenReturn(session);
        userManager.bindUser(request, user);
        verify(session).setAttribute(DEFAULT_SESSION_ATTRIBUTE_NAME, user.getId());
        verify(session).setAttribute(DEFAULT_SESSION_ATTRIBUTE_NAME + ".class", user.getClass());
        verify(session).setAttribute(DEFAULT_SESSION_ATTRIBUTE_NAME + ".name", "id");
    }

    @Test
    public void testBindAuthColumnUser() {
        AbstractSessionUserManager userManager = new AbstractSessionUserManager() {
            @Override
            protected IUser getUserByKey(Object fieldValue, String fieldName, Class<? extends IUser> userClass) {
                return null;
            }
        };
        MockedAuthColumnUser user = new MockedAuthColumnUser();
        user.setId(100);
        user.setUsername("a");
        user.setPassword("b");
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(true)).thenReturn(session);
        userManager.bindUser(request, user);
        verify(session).setAttribute(DEFAULT_SESSION_ATTRIBUTE_NAME, user.getUsername());
        verify(session).setAttribute(DEFAULT_SESSION_ATTRIBUTE_NAME + ".class", user.getClass());
        verify(session).setAttribute(DEFAULT_SESSION_ATTRIBUTE_NAME + ".name", "username");
    }
}
