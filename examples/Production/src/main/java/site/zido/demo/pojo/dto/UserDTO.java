package site.zido.demo.pojo.dto;

import lombok.Data;
import site.zido.coffee.common.model.OutputConverter;
import site.zido.demo.entity.User;

@Data
public class UserDTO implements OutputConverter<UserDTO, User> {
    private Integer id;
    private String username;
    /**
     * 性别，0:男，1:女
     */
    private Integer sex = 0;
    /**
     * vip
     */
    private Boolean vip;

    private Boolean enabled = true;
}
