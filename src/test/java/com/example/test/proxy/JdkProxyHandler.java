package com.example.test.proxy;

import java.lang.reflect.Proxy;

public class JdkProxyHandler {
    /**
     * 用来接收真实的歌手
     */
    private Object realStar;

    /**
     * 通过构造方法传入真实对象
     * @param realStar
     */
    public JdkProxyHandler(Object realStar) {
        super();
        this.realStar = realStar;
    }

    public Object getProxyInstance(){
        return Proxy.newProxyInstance(realStar.getClass().getClassLoader(),
                realStar.getClass().getInterfaces(),
                (proxy, method, args) -> {
                    System.out.println("开始干活之前写写点日志");
                    Object object = method.invoke(realStar, args);
                    System.out.println("任务结束写日志");
                    return object;
                });
    }
}