package com.xych.dubbo.api.pojo;

import java.io.Serializable;

import lombok.Data;

@Data
public class User implements Serializable
{
    private static final long serialVersionUID = 1L;
    private String id;
    private String name;
    private Integer age;
    private String sex;

    public User()
    {
        super();
    }

    public User(String id, String name, Integer age, String sex)
    {
        super();
        this.id = id;
        this.name = name;
        this.age = age;
        this.sex = sex;
    }

    public User(String id, String name)
    {
        super();
        this.id = id;
        this.name = name;
    }
}
