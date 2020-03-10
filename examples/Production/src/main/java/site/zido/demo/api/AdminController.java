package site.zido.demo.api;

import site.zido.demo.entity.User;
import site.zido.demo.service.IUserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {
    private final IUserService userService;

    public AdminController(IUserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public List<User> users() {
        return userService.getUsers(Arrays.asList(1, 2, 3, 4));
    }
}
