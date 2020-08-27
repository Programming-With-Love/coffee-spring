package site.zido.demo.entity;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import site.zido.coffee.data.model.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * 用户
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class User extends BaseEntity {
    @GeneratedValue
    @Id
    private Integer id;
    @Column(nullable = false, length = 30)
    private String username;
    @Column(nullable = false, length = 20)
    private String phone;
    /**
     * 性别，0:男，1:女
     */
    private Integer sex = 0;
    /**
     * 密码
     */
    private String password;

    /**
     * 卡号
     */
    private String card;

    /**
     * vip
     */
    private Boolean vip;

    private Boolean enabled = true;

    @Builder(builderMethodName = "registerBuilder")
    private User(String username, String phone, Integer sex, String password, String card, Boolean vip) {
        this.username = username;
        this.phone = phone;
        this.sex = sex;
        this.password = password;
        this.card = card;
        this.vip = vip;
    }
}
