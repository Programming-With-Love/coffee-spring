package site.zido.demo.api;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 一些简单接口使用
 *
 * @author zido
 */
@RestController
public class IndexController {

    @GetMapping("/global-result")
    public String testGlobalResult() {
        return "someString";
    }

    @PreAuthorize("hasAuthority('ROLE_user')")
    @RequestMapping("/hello")
    public String index(@AuthenticationPrincipal UserDetails user) {
        return "hello world : " + user.getUsername();
    }

    @PreAuthorize("hasAuthority('ROLE_admin')")
    @RequestMapping("/admin")
    public String admin(@AuthenticationPrincipal UserDetails user) {
        return "hello world : " + user.getUsername();
    }

    @GetMapping("/runtime-err")
    public String throwNewRuntimeError() {
        throw new RuntimeException();
    }
}
