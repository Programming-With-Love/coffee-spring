package site.zido.demo.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import site.zido.coffee.common.model.BaseEntity;

import javax.persistence.*;
import java.util.Date;

/**
 * 入住记录
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Record extends BaseEntity {
    @Id
    @GeneratedValue
    private Integer id;

    /**
     * 入住用户id
     */
    private Integer userId;

    /**
     * 本次入住人数
     */
    private Integer count;

    /**
     * 入住结束时间
     */
    @Column(columnDefinition = "timestamp default CURRENT_TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endTime;
}
