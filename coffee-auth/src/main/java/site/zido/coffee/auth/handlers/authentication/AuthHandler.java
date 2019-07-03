package site.zido.coffee.auth.handlers.authentication;

import site.zido.coffee.auth.entity.IUser;
import site.zido.coffee.auth.exceptions.AuthenticationException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface AuthHandler {

    IUser attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException;
}
