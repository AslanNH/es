package com.nh.es.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Document(indexName ="lol" ,shards = 1,replicas = 0)
public class Lol implements Serializable {
    @Id
    private Long id;
    /**
     * 英雄游戏名字
     */
    @Field(type= FieldType.Keyword)
    private String name;
    /**
     * 英雄名字
     */
    @Field(type=FieldType.Keyword)
    private String realName;
    /**
     * 英雄描述信息
     */
    @Field(type=FieldType.Text,analyzer = "ik_max_word")
    private String desc;

    @Field(type=FieldType.Integer)
    private int age;

    @Field(type=FieldType.Keyword,fielddata=true)
    private String zy;
}