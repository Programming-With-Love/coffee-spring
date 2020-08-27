package site.zido.demo.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
@EqualsAndHashCode
@ToString(callSuper = true)
public class Room {
    /**
     * 房间号
     */
    @Id
    private String no;

    /**
     * 楼层
     */
    private Integer floor;

    /**
     * 当前房间是否入住
     */
    private Boolean status;
}
