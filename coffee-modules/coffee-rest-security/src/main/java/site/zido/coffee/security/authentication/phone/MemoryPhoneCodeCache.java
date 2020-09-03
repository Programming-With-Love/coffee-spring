package site.zido.coffee.security.authentication.phone;

import site.zido.coffee.core.utils.maps.expire.ExpireMap;

public class MemoryPhoneCodeCache implements PhoneCodeCache {
    private final ExpireMap<String, String> expireMap;

    public MemoryPhoneCodeCache() {
        this(new ExpireMap<>(1000));
    }

    public MemoryPhoneCodeCache(ExpireMap<String, String> expireMap) {
        this.expireMap = expireMap;
    }

    @Override
    public void put(String phone, String code) {
        expireMap.set(phone, code, 60 * 1000);
    }

    @Override
    public String getCode(String phone) {
        return expireMap.get(phone);
    }
}
