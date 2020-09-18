package site.zido.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.RequestHeaderRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import site.zido.coffee.security.configurers.RestSecurityConfigureAdapter;
import site.zido.coffee.security.configurers.PhoneCodeLoginConfigurer;
import site.zido.coffee.security.configurers.RestSecurityContextConfigurer;
import site.zido.demo.entity.Admin;
import site.zido.demo.entity.User;
import site.zido.demo.pojo.AuthUser;
import site.zido.demo.repository.AdminRepository;
import site.zido.demo.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.UUID;

/**
 * 认证配置类，restful风格，使用jwt方案
 *
 * @author zido
 */
@EnableWebSecurity(debug = true)
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class AuthConfig extends RestSecurityConfigureAdapter {
    private static final String PHONE_PATTERN = "^1[123456789][\\d]{9}";
    private static final RequestMatcher adminMatcher =
            new RequestHeaderRequestMatcher("role", "admin");
    private static final RequestMatcher userMatcher =
            new RequestHeaderRequestMatcher("role", "user");

    private UserRepository userRepository;

    private HttpServletRequest request;

    private AdminRepository adminRepository;

    public AuthConfig(UserRepository userRepository, HttpServletRequest request, AdminRepository adminRepository) {
        this.userRepository = userRepository;
        this.request = request;
        this.adminRepository = adminRepository;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                //权限管理将管理所有的请求
                .authorizeRequests().anyRequest().permitAll()
                .and()
                //帐号密码登录
                .formLogin().and()
                //手机号验证码登录
                .apply(new PhoneCodeLoginConfigurer<>()).phoneCodeService((phone, code) -> {
            System.out.printf("phone:%s,code:%s\n", phone, code);
        }).and()
                //自定义jwt的超时时间
                .apply(new RestSecurityContextConfigurer<>()).jwt();
    }

    /**
     * 注意手机号也通过此接口查询
     * <p>
     * 实际上这也同时开启了手机号+密码的方式登录（关于这一点实现需要进行讨论）
     * <p>
     * 注意，在jwt中，因为没有在后端进行任何数据存储，而是把用户名存储到jwt token中，所以每次接口调用都需要调用此接口进行用户查询
     * 如果需要缓存数据，也在这一层进行
     *
     * @return userDetailsService
     */
    @Bean
    @Override
    protected UserDetailsService userDetailsService() {
        return username -> {
            //这里只是示范，可以根据request猜测用户登录目标
            if (adminMatcher.matches(request)) {
                return getAdmin(username).orElseThrow(() -> new UsernameNotFoundException("用户名或密码错误"));
            } else if (userMatcher.matches(request)) {
                //普通通道
                return getUser(username);
            }

            Optional<Admin> admin = getAdmin(username);
            if (admin.isPresent()) {
                return admin.get();
            }
            return getUser(username);
        };
    }

    private Optional<Admin> getAdmin(String username) {
        return adminRepository.findById(username);
    }

    private AuthUser getUser(String username) {
        User user = userRepository.findByUsername(username).orElseGet(() -> {
            //猜测是手机号登录，如果手机号不存在数据库，则自动注册一个帐号
            if (PHONE_PATTERN.matches(username)) {
                return userRepository.findByPhone(username).orElseGet(() -> userRepository.save(User.registerBuilder()
                        .username("自动生成-" + UUID.randomUUID())
                        .password(UUID.randomUUID().toString())
                        .phone("13512341234")
                        .build()));
            }
            //如果不是手机号登录，则需要用户自行注册
            return null;
        });

        if (user == null) {
            throw new UsernameNotFoundException("帐号或密码错误");
        }
        return new AuthUser(user);
    }

    /**
     * 配置一个可保证迁移性的密码管理器,它主要用来兼容以前的密码，能够根据id切换算法。但是默认的加密仍然是使用{@link org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder}
     * <p>
     * 密码会以 "{算法}加密字符串" 的形式进行保存，例如：
     * <p>
     * {noop}123456: 明文保存
     * <p>
     * spring security会自行创建一个懒加载的密码管理器，扫描 passwordEncoder Bean
     *
     * @return passwordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

}
