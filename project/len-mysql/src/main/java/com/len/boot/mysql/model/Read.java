package com.len.boot.mysql.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * @author: jone
 * @date: Created in 2020/9/11 4:26 下午
 * @description:
 */
@Data
@Entity
@ToString
@Table(name = "read_artile")
@JsonIgnoreProperties(value={"hibernateLazyInitializer","handler","fieldHandler"})
public class Read implements Serializable {

    private static final long serialVersionUID = -2411091733419702279L;

    @Id
    private String rid;

    private String title;

    private String content;

    private String author;

    private Integer type;

    private Integer readNum;

    private Date createTime;

    private Date updateTime;
}
