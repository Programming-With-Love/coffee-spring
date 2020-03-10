package site.zido.demo.config;

import site.zido.coffee.security.RestSecurityConfigurationAdapter;
import site.zido.demo.entity.User;
import site.zido.demo.pojo.AuthUser;
import site.zido.demo.repository.AdminRepository;
import site.zido.demo.repository.UserRepository;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.RestHttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableRestSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 认证配置类，restful风格，使用jwt方案
 *
 * @author zido
 */
@EnableRestSecurity(debug = true)
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class AuthConfig extends RestSecurityConfigurationAdapter {
    private static final String PHONE_PATTERN = "^1[123456789][\\d]{9}";
    private static final String adminLoginUrl = "/admin/sessions";
    private static final AntPathRequestMatcher adminMatcher = new AntPathRequestMatcher(adminLoginUrl, "POST");

    private UserRepository userRepository;

    private HttpServletRequest request;

    private AdminRepository adminRepository;

    public AuthConfig(UserRepository userRepository, HttpServletRequest request, AdminRepository adminRepository) {
        this.userRepository = userRepository;
        this.request = request;
        this.adminRepository = adminRepository;
    }

    @Override
    protected void configure(RestHttpSecurity http) throws Exception {
        http
                //权限管理将管理所有的请求
                .authorizeRequests().anyRequest().permitAll()
                .and()
                //帐号密码登录
                .formLogin().and()
                //手机号验证码登录
                .phoneCodeLogin().phoneCodeService((phone, code) -> {
            System.out.printf("phone:%s,code:%s\n", phone, code);
        }).and()
                //自定义jwt的超时时间
                .securityContext().jwt().jwtExpiration(1, TimeUnit.HOURS);
    }

    /**
     * 注意手机号也通过此接口查询,如果使用手机号登录，务必保证能够查出来
     * <p>
     * 实际上这也同时开启了手机号+密码的方式登录（关于这一点实现需要进行讨论）
     *
     * @return userDetailsService
     */
    @Override
    protected UserDetailsService userDetailsService() {
        return username -> {
            //这里只是示范，可以根据request猜测用户登录目标
            if (adminMatcher.matches(request)) {
                return adminRepository.findById(username)
                        .orElseThrow(() -> new UsernameNotFoundException("帐号或密码错误"));
            }
            User user = userRepository.findByUsername(username).orElseGet(() -> {
                //猜测是手机号登录，如果手机号不存在数据库，则自动注册一个帐号
                if (PHONE_PATTERN.matches(username)) {
                    return userRepository.findByPhone(username).orElseGet(() -> User.registerBuilder()
                            .username("自动生成-" + UUID.randomUUID())
                            .password(UUID.randomUUID().toString())
                            .build());
                }
                //如果不是手机号登录，则需要用户自行注册
                return null;
            });

            if (user == null) {
                throw new UsernameNotFoundException("帐号或密码错误");
            }
            return new AuthUser(user);
        };
    }

}
