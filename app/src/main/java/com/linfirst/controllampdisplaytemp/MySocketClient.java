package com.linfirst.controllampdisplaytemp;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author linfirst
 */
class MySocketClient {
    /**
     * 客户端输入输出流
     */
    PrintStream out;
    BufferedReader in;
    Socket socket;
    String message;
    ExecutorService executorService;

    OnReceivesListener onReceivesListener;
    OnConnectionListener onConnectionListener;

    /**
     * 构造方法
     *
     * @return
     */
    MySocketClient() {
        //创建线程池
        executorService = Executors.newCachedThreadPool();
    }

    /**
     * 初始化连接
     * @param ip
     * @param port
     */
    void initSocket(final String ip, final int port) {
        //任务

        Runnable runnable1 = new Runnable() {
            @Override
            public void run() {
                //根据服务器端名和端口号，连接服务器
                try {
                    if (socket == null) {
                        socket = new Socket(ip,port);
                        Log.i("AAAAAAA", "33333");
                        Log.i("AAAAAAA", "666666");
                    }
//                    SocketAddress socAddress = new InetSocketAddress(ip, port);
//                    socket.connect(socAddress, 2000);
                    //回调连接成功接口
                    onConnectionListener.success();
                    //获取Socket的输入输出流
                    out = new PrintStream(socket.getOutputStream());
                } catch (IOException e) {
                    //回调连接失败接口
                    onConnectionListener.failure();
                    Log.e("SSSSSSS", "无法连接到服务器");
                }
            }
        };
        executorService.execute(runnable1);
    }


    /**
     * 发送消息
     * @param msg
     */
    void sendMessage(final String msg) {
        if (out == null) {
            Log.e("SSSSSSS", "没有连接");
            onConnectionListener.failure();
            return;
        }
        Runnable runnable2 = new Runnable() {
            @Override
            public void run() {
                //向Socket的输出流中写数据
                out.print(msg);
                Log.e("CCCCC发送的消息：", msg);
            }
        };
        executorService.execute(runnable2);
    }

    /**
     * 接收消息
     */
    public void receiveMessage() {
        Runnable runnable3 = new Runnable() {
            @Override
            public void run() {
                try {
                    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    while (true) {
                        message = in.readLine();
                        Log.e("CCCCCC", "接收到的消息：" + message);
                        if (onReceivesListener != null) {
                            onReceivesListener.success(message);
                        }
                    }
                } catch (IOException e) {
                    if (onReceivesListener != null) {
                        onReceivesListener.failure("断开连接");
                    }

                    Log.i("receiv", "断开连接");
                    e.printStackTrace();
                }
            }
        };
        executorService.execute(runnable3);
    }

    void close() {
        try {
            if (socket != null) {
                socket.close();
                socket=null;
            }
            if (out != null) {
                out.close();
            }
            if (in != null) {
                in.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void setOnReceivesLisrtner(OnReceivesListener onReceivesLisrtner) {
        this.onReceivesListener = onReceivesLisrtner;
    }

    void setOnConnectionListener(OnConnectionListener onConnectionListener) {
        this.onConnectionListener = onConnectionListener;
    }
}
