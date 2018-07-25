package com.xych.dubbo.code.pojo;

import lombok.Data;

@Data
public class User
{
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
