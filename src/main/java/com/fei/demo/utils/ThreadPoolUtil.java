package com.fei.demo.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Console;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.concurrent.*;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * @author: zhangxinfei
 * create at:  2021/3/2  11:15 上午
 * @description: 线程池工具
 */
public class ThreadPoolUtil {

    public static void main(String[] args){


    }
}

class RunnableTest implements Runnable {

    private FileInputStream fileInputStream;

    private Semaphore semaphore;

    public RunnableTest(FileInputStream fileInputStream,Semaphore semaphore){
        this.fileInputStream = fileInputStream;
        this.semaphore = semaphore;
    }

    @SneakyThrows
    @Override
    public void run() {
        semaphore.acquire();
        System.out.println(Thread.currentThread().getName());
        try {
            //把下载的图片放到统一的目录下
            String filePath = "/Users/zhangxinfei/Documents/project/demo/src/main/resources/static/images/" + DateUtil.offsetDay(DateUtil.date(),1).toDateStr() + "/";
            File file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
            File f = new File(file,DateUtil.date().getTime() + ".jpg");
            FileOutputStream outputStream = new FileOutputStream(f);

            // 判断输入或输出是否准备好
            if (fileInputStream != null && outputStream != null) {
                int temp = 0;
                // 开始拷贝
//                byte[] inNum = new byte[512];
                while ((temp = fileInputStream.read()) != -1) {
                    // 边读边写
                    outputStream.write(temp);
                }
                // 关闭输入输出流
                fileInputStream.close();
                outputStream.close();
            } else {
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        semaphore.release();
    }

    public static void main(String[] args) {

        Semaphore semaphore = new Semaphore(20);

        ThreadFactory threadFactory = new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                String name = String.valueOf(DateUtil.date().getTime());
                Thread thread = new Thread(r, name);
                System.out.println(thread.getName());
                return thread;
            }
        };

        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(5, 20, 5,
                TimeUnit.SECONDS, new LinkedBlockingQueue<>(5), threadFactory, new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                System.out.println(r.toString() + "出现错误");
            }
        });


        try {
            for (int i = 0; i < 50; i++){
                File inf = new File("/Users/zhangxinfei/Documents/project/demo/src/main/resources/static/test.png");
                //创建读取服务器端的文件的读取流
                FileInputStream fileInputStream = new FileInputStream(inf);
                poolExecutor.execute(new RunnableTest(fileInputStream,semaphore));
                semaphore.acquire();


//                Console.log("poolSize:" + poolExecutor.getPoolSize());
//                Console.log("corePoolSize:" + poolExecutor.getCorePoolSize());
//                Console.log("maximumPoolSize:" + poolExecutor.getMaximumPoolSize());
//                Console.log("queue:" + poolExecutor.getQueue().size());
//                Console.log("completedTaskCount:" + poolExecutor.getCompletedTaskCount());
//                Console.log("largestPoolSize:" + poolExecutor.getLargestPoolSize());
//                Console.log("keepAliveTime:" + poolExecutor.getKeepAliveTime(TimeUnit.SECONDS));
            }

            System.out.println("执行完成");

        } catch (Exception e){
            e.printStackTrace();
            System.out.println("执行完成1");
        }

    }
}
