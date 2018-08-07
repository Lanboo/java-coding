package com.xych.dubbo.server;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Application
{
    private static String PATH = "classpath:dubbo/dubbo*.xml";

    public static void main(String[] args) throws Exception
    {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(PATH);
        log.info("context={}", context);
        log.info("userService={}", context.getBean("userService"));
        System.in.read();//阻塞主线程
    }
}
