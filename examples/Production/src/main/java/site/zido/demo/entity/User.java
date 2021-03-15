package site.zido.demo.entity;

import lombok.Builder;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

/**
 * 用户
 */
@Entity
@Data
public class User {
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

    private Date createTime = new Date();

    @Builder(builderMethodName = "registerBuilder")
    private User(String username, String phone, Integer sex, String password, String card, Boolean vip, Date createTime) {
        this.username = username;
        this.phone = phone;
        this.sex = sex;
        this.password = password;
        this.card = card;
        this.vip = vip;
        this.createTime = createTime;
    }
}
