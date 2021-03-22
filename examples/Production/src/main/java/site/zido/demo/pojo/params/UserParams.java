package site.zido.demo.pojo.params;

import lombok.Data;
import site.zido.coffee.core.utils.InputConverter;
import site.zido.coffee.core.validations.Phone;
import site.zido.demo.entity.User;

import javax.validation.constraints.Size;

@Data
public class UserParams implements InputConverter<User> {
    @Size(min = 6, max = 10)
    private String username;
    @Size(min = 6, max = 30)
    private String password;
    @Size(min = 11, max = 11)
    @Phone
    private String mobile;
    private Integer sex = 0;
    private String card;
    private String vip;
    private Boolean enabled = true;
}
