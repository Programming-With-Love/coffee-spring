package site.zido.demo.pojo.params;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import site.zido.coffee.common.model.InputConverter;
import site.zido.coffee.common.validations.Phone;
import site.zido.demo.entity.User;

@Data
public class UserParams implements InputConverter<User> {
    @Length(min = 6, max = 10)
    private String username;
    @Length(min = 6, max = 30)
    private String password;
    @Length(min = 11, max = 11)
    @Phone
    private String mobile;
    private Integer sex = 0;
    private String card;
    private String vip;
    private Boolean enabled = true;
}
