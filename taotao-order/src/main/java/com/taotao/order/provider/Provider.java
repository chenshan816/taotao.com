package com.taotao.order.provider;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Provider {
	   
    public static void main(String[] args) {
        try {
            ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:spring/provider.xml");
            context.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        synchronized (Provider.class) {
            while (true) {
                try {
                    Provider.class.wait();
                } catch (InterruptedException e) {
                	e.printStackTrace();
                }
            }
        }
    }
}
