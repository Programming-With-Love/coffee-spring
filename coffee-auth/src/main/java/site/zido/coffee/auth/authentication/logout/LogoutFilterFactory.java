package site.zido.coffee.auth.authentication.logout;

import org.springframework.core.annotation.AnnotatedElementUtils;
import site.zido.coffee.auth.config.AuthenticationFilterFactory;
import site.zido.coffee.auth.config.ObjectPostProcessor;
import site.zido.coffee.auth.user.annotations.AuthEntity;

import javax.servlet.Filter;
import java.util.List;

public class LogoutFilterFactory implements AuthenticationFilterFactory {
    private List<LogoutHandler> logoutHandlers;
    private LogoutSuccessHandler logoutSuccessHandler;

    @Override
    public Filter createFilter(Class<?> userClass, ObjectPostProcessor<Object> objectObjectPostProcessor) {
        AuthEntity authEntity = AnnotatedElementUtils.findMergedAnnotation(userClass, AuthEntity.class);
        //TODO
        return null;
    }

    public void setLogoutSuccessHandler(LogoutSuccessHandler logoutSuccessHandler) {
        this.logoutSuccessHandler = logoutSuccessHandler;
    }

    public void setLogoutHandlers(List<LogoutHandler> logoutHandlers) {
        this.logoutHandlers = logoutHandlers;
    }
}
