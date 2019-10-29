package com.example.test;

import com.example.test.proxy.CglibProxyHandler;
import com.example.test.proxy.JdkProxyHandler;
import com.example.test.proxy.vo.RealStar;
import com.example.test.proxy.vo.Star;
import lombok.extern.slf4j.Slf4j;
import org.gavaghan.geodesy.Ellipsoid;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GeodeticCurve;
import org.gavaghan.geodesy.GlobalCoordinates;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestApplicationTests {
    private ThreadLocal<DateFormat> format = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd"));
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Test
    public void contextLoads() {
    }

    @Test
    public void testBigDecimal(){
        BigDecimal num = BigDecimal.ZERO;
        num.add(new BigDecimal(100));
        num.add(new BigDecimal(200));
        System.out.println("用BigDecimal.ZERO作为初始值" + num);


        BigDecimal x = new BigDecimal(100);
        BigDecimal add = x.add(new BigDecimal(200));
        System.out.println("x=" + x);
        System.out.println("add=" + add);

        System.out.println("------------测试精度丢失问题----------------");
        BigDecimal a = new BigDecimal(1.01);
        BigDecimal b = new BigDecimal(1.02);
        BigDecimal c = new BigDecimal("1.01");
        BigDecimal d = new BigDecimal("1.02");
        BigDecimal e = BigDecimal.valueOf(1.01);
        BigDecimal f = BigDecimal.valueOf(1.02);
        System.out.println(a.add(b));
        System.out.println(c.add(d));
        System.out.println(e.add(f));
        System.out.println(new BigDecimal(1).add(new BigDecimal(2)));

        System.out.println(BigDecimal.valueOf(0).compareTo(BigDecimal.valueOf(1)));
    }

    @Test
    public void testString(){
//        System.out.println("😭😭😭".length());
//        String[] x = "gfdsfsd".split("\\&");
//        for (String s : x) {
//            System.out.println(s);
//        }
//        String[] split = "ques=1&req=2&nj=3".split("&");
//        for (String s : split) {
//            System.out.println(s);
//        }
//        StringJoiner joiner = new StringJoiner(" and ", "begin ", " end");
//        joiner.add("1");
//        joiner.add("2");
//        System.out.println(joiner);
//
//        System.out.println(String.join(", ", split));
//
//        List<Integer> integers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
//        integers.forEach( i -> System.out.println(Thread.currentThread().getId() + "-->数值-->" + i));
//        System.out.println( (999L + ""));
        List<String> result = new ArrayList<>();
        StringBuilder sb = new StringBuilder(10);
        for (int i = 0; i < 5; i++) {
            sb.append(i + "");
            result.add(sb.toString());
            sb.setLength(0);
        }
        result.forEach(r -> System.out.println(r.hashCode() + "->" + r));
        System.out.println("----------------");
        List<String> collect = result.stream().sorted((o1, o2) -> o2.hashCode() - o1.hashCode())
                .collect(Collectors.toList());
        collect.forEach(c -> System.out.println(c.hashCode() + "->" + c));
    }

    /**
     * SimpleDateFormat是线程不安全的，表现为格式化出来的数据可能不准确。
     * 比如这里表现为漏掉了某一天数据，或者某一天数据重复了
     * @throws InterruptedException
     */
    @Test
    public void testThread() throws InterruptedException {
        ThreadPoolExecutor executor = (ThreadPoolExecutor)Executors.newFixedThreadPool(500);
        int count = 10;
        Calendar date = Calendar.getInstance();
        List<Date> dateList = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            date.add(Calendar.DAY_OF_YEAR, 1);
            Date time = date.getTime();
            dateList.add(time);
        }
//        for (int i = 0; i < count; i++) {
//            int finalI = i;
//            executor.submit(() -> System.out.println(simpleDateFormat.format(dateList.get(finalI))));
//        }
//
//        while(executor.getCompletedTaskCount() < count){
//            Thread.sleep(500);
//        }

//        System.out.println("做下一个实验");
        for (int i = 0; i < count; i++) {
            final int finalI = i;
            executor.submit(() -> {
                try {
                    DateFormat dateFormat = simpleDateFormat;   //改成使用ThreadLocal就可以解决这个问题。format.get()
                    String dateString = dateFormat.format(dateList.get(finalI));
                    Date parse = dateFormat.parse(dateString);
                    String formatStr = dateFormat.format(parse);
                    System.out.println(dateString.equals(formatStr));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            });
        }
        executor.shutdown();
        while(true){
            if (executor.isTerminated()){
                break;
            }
            Thread.sleep(500);
        }
    }

    @Test
    public void testGeo(){
        GlobalCoordinates source = new GlobalCoordinates(23.181783, 113.421527);
        GlobalCoordinates target = new GlobalCoordinates(23.1778589865,113.4110631222);
        double meter1 = getDistanceMeter(source, target, Ellipsoid.Sphere);
        double meter2 = getDistanceMeter(source, target, Ellipsoid.WGS84);

        System.out.println("Sphere坐标系计算结果："+meter1 + "米");
        System.out.println("WGS84坐标系计算结果："+meter2 + "米");
    }
    public static double getDistanceMeter(GlobalCoordinates gpsFrom, GlobalCoordinates gpsTo, Ellipsoid ellipsoid) {
        //创建GeodeticCalculator，调用计算方法，传入坐标系、经纬度用于计算距离
        GeodeticCurve geoCurve = new GeodeticCalculator().calculateGeodeticCurve(ellipsoid, gpsFrom, gpsTo);
        return geoCurve.getEllipsoidalDistance();
    }

    /**
     * 测试动态代理
     * JDK的动态代理仅针对有实现接口的类，代理该类的全部方法
     */
    @Test
    public void testProxy(){
        Star realStar = new RealStar();
        Star proxy = (Star) new JdkProxyHandler(realStar).getProxyInstance();
        proxy.sing("千里之外");
        proxy.dance("鸡你太美");
    }

    /**
     * 测试Cglib生成动态代理
     */
    @Test
    public void testCglibProxy(){
        Star realStar = new RealStar();
        Star proxy = (Star)new CglibProxyHandler().getProxyInstance(realStar);
        proxy.sing("千里之外");
        proxy.dance("鸡你太美");
    }

}
