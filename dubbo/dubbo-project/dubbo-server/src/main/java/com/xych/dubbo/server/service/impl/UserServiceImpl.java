package com.xych.dubbo.server.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.xych.dubbo.code.pojo.User;
import com.xych.dubbo.code.service.IUserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserServiceImpl implements IUserService
{
    @Override
    public List<User> list()
    {
        log.info("假装有一堆人~");
        List<User> userList = new ArrayList<>();
        userList.add(new User("1", "xych"));
        userList.add(new User("2", "xych2"));
        userList.add(new User("3", "xych3"));
        return userList;
    }

    @Override
    public User findOne(String id)
    {
        log.info("假装这里有一个人");
        return new User(id, "xych");
    }
}
