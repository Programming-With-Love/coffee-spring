package site.zido.demo.api;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.zido.coffee.extra.limiter.Limiter;

/**
 * 注解限制频率
 *
 * @author zido
 */
@RequestMapping("/limit")
@RestController
public class LimiterController {

    @RequestMapping
    @Limiter
    public String limit() {
        return "limit content";
    }

    /**
     * 模拟发送短信的接口，能够根据手机号进行频率限制
     *
     * @param phone phone
     * @return content
     */
    @RequestMapping("/sms")
    @Limiter(key = "'content:' + #phone")
    public String limit(String phone) {
        return "limit content";
    }

    /**
     * restful api 限制频率
     *
     * @param phone phone
     * @return content
     */
    @RequestMapping("/{phone}/sms")
    @Limiter(key = "'content:path:' + #phone")
    public String inlineLimit(@PathVariable String phone) {
        return limit("inline");
    }
}
