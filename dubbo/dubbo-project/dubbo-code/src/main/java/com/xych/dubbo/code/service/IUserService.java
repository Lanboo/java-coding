package com.xych.dubbo.code.service;

import java.util.List;

import com.xych.dubbo.code.pojo.User;

public interface IUserService
{
    List<User> list();

    User findOne(String id);
}
