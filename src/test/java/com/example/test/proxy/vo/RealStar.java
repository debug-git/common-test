package com.example.test.proxy.vo;

public class RealStar implements Star {
    @Override
    public void sing(String songName) {
        System.out.println("明星本人开始唱歌，歌名：" + songName);
    }

    @Override
    public void dance(String danceName) {
        System.out.println("明星本人开始跳舞，什么舞：" + danceName);
    }
}