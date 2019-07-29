package example;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;
import site.zido.coffee.auth.authentication.Auth;

@RestController
public class UserController {
    @RequestMapping
    public String index() {
        return "hello world";
    }

    @RequestMapping("/user")
    @Auth
    public String user(@SessionAttribute("user") User user) {
        return "hello user:" + user.getUsername();
    }
}
