package com.example.test.proxy;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class CglibProxyHandler implements MethodInterceptor {
    /**
     * 维护对象
     */
    private Object target;

    public Object getProxyInstance(final Object target){
        this.target = target;
        // Enhancer类是CGLIB中的一个字节码增强器，它可以方便的对你想要处理的类进行扩展
        Enhancer enhancer = new Enhancer();
        // 将被代理的对象设置成父类
        enhancer.setSuperclass(target.getClass());
        // 回调方法，设置拦截器
        enhancer.setCallback(this);
        return enhancer.create();
    }

    @Override
    public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        System.out.println("cglib动态代理来了");
        //唱歌要歌手自己唱
        Object result = methodProxy.invokeSuper(o, args);
        System.out.println("cglib动态代理清场了");
        return result;
    }
}