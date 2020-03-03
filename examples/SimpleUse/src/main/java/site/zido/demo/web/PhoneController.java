package site.zido.demo.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import site.zido.coffee.security.authentication.phone.PhoneCodeCache;

@RestController
public class PhoneController {
    private PhoneCodeCache codeCache;

    public PhoneController(PhoneCodeCache codeCache) {
        this.codeCache = codeCache;
    }

    @GetMapping("/phone/code")
    public String getCode(String phone) {
        return codeCache.getCode(phone);
    }

}
