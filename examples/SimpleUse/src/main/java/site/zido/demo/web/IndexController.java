package site.zido.demo.web;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.zido.coffee.extra.limiter.Limiter;

/**
 * @author zido
 */
@RestController
public class IndexController {
    @PreAuthorize("hasRole('user')")
    @RequestMapping("/hello")
    public String index(@AuthenticationPrincipal UserDetails user) {
        return "hello world : " + user.getUsername();
    }

    @Limiter(key = "limit-test")
    @RequestMapping("/limit")
    public String limit(){
        return "limit content";
    }
    @PreAuthorize("hasRole('admin')")
    @RequestMapping("/admin")
    public String admin(@AuthenticationPrincipal UserDetails user) {
        return "hello world : " + user.getUsername();
    }
}
