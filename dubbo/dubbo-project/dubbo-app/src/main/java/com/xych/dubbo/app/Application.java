package com.xych.dubbo.app;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.xych.dubbo.api.pojo.User;
import com.xych.dubbo.api.service.IUserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Application
{
    private static String PATH = "classpath:dubbo/dubbo*.xml";

    public static void main(String[] args)
    {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(PATH);
//        context.start();
        IUserService userService = context.getBean("userService", IUserService.class);
        log.info("context={}", context);
        log.info("userService={}", userService);
        User user = userService.findOne("4");
        log.info("user={}", user);
    }
}
