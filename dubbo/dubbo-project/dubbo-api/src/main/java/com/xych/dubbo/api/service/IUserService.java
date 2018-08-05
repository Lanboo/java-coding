package com.xych.dubbo.api.service;

import java.util.List;

import com.xych.dubbo.api.pojo.User;

public interface IUserService
{
    List<User> list();

    User findOne(String id);
}
