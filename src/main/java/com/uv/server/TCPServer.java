package com.uv.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @Description: TCPServer 负责控制Router的TCP服务器，负责消息接收和发送
 * @Author: benjakang
 * @Time: 2020/1/5 15:13
 **/

public class TCPServer {

    private boolean isStarted;
    private int port;
    private ServerThread serverThread;

    public TCPServer(int port){
        this.port = port;
    }

    public void stop() throws IOException {
        //关闭连接
        isStarted = false;
        new Socket("localhost",port);

    }

    public void start() throws IOException {
        isStarted = true;
        //启动服务器
        if (serverThread == null){
            serverThread = new ServerThread(port);
        }
        serverThread.start();

    }

    public synchronized boolean isStarting(){
        return isStarted;
    }

    /**
     * 当前Server专属线程
     */

    class ServerThread extends Thread{
        private int port;

        public ServerThread(int port) throws IOException{
            this.port = port;
        }

        @Override
        public void run() {

            try {
                ServerSocket ss = new ServerSocket(port);
                //循环接收
                while (isStarting()) {
                    System.out.println("等待client接入");
//                    System.out.println(Thread.currentThread().getName());
//                    System.out.println(InetAddress.getLocalHost().getHostAddress());
                    Socket socket = ss.accept();

                    if(isStarting()) { //最后一次连接，不载入
                        BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        PrintWriter output = new PrintWriter(socket.getOutputStream());

                        String line = "";
                        StringBuilder message = new StringBuilder();
                        while ((line = input.readLine()) != null){
                            message.append(line);
                        }

                        String reply = "welcome";

                        output.write(reply);
                        output.flush();


                        input.close();
                    }
                    socket.close();
                }
                ss.close();
            }catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

}
