package com.uv;

import com.alibaba.fastjson.JSONObject;
import com.sun.security.ntlm.Server;
import com.uv.server.JSONUtil;
import com.uv.server.MsgUtil;
import com.uv.server.NettyServer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * <uv> [2018/10/13 19:52]
 */
public class Main {

    public static void main(String[] args) throws Exception {

        new ServerThread(11000).start();

        new ServerThread(10001).start();

        new ServerThread(10002).start();

        new ServerThread(10003).start();
//
//        new ServerThread(10004).start();

//        new ClientThread("0.0.0.0", 9999, 10000).start();
//        Socket socket = new Socket("localhost", 9999);
//        new TCPServer(9999).start();
//        System.out.println(1111);

    }


    static class ServerThread extends Thread{
        private int port;

        public ServerThread(int port) {
            this.port = port;
        }

        @Override
        public void run(){
            ServerSocket ss = null;
            try {
                ss = new ServerSocket(port);
                System.out.println(ss);
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (true) {
                try {
                    Socket socket = ss.accept();

                    PrintWriter output = new PrintWriter(socket.getOutputStream());
                    //输入流
                    BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    String line = "";
                    StringBuilder info = new StringBuilder();
                    while ( (line = input.readLine()) != null ){
                        info.append(line);
                    }
                    System.out.println(info);

                    //解析消息
                    JSONObject obj = (JSONObject) JSONUtil.stringToBean(new String(info), new Object());
                    String type = obj.getString("type");


                    //准备回复消息
                    JSONObject reply = null;
                    List<Integer> solution = new ArrayList<>();
                    switch (type) {
                        case "file":
                            reply = MsgUtil.createFiled("localhost:"+port, "success", "success to file");
                            break;
                        case "start":
                            reply = MsgUtil.createStarted("localhost:"+port);
                            break;
                        case "stop":
                            for (int i = 0; i < 5; i++) {
                                solution.add(new Random().nextInt(100));
                            }
                            reply = MsgUtil.createStopped("localhost:"+port, solution, 90, 36, 102.6);
                            break;
                        case "query":
                            for (int i = 0; i < 5; i++) {
                                solution.add(new Random().nextInt(100));
                            }
                            if(new Random().nextInt(10) / 2 >-1)
                                reply = MsgUtil.createResult("localhost:"+port, solution, 100, 100, 622.6);
                            else
                                reply = MsgUtil.createFinal("localhost:"+port, solution, 120, 200, 1122.6);
                            break;

                    }

                    output.write(reply.toJSONString());
//                    output.write("slkdjf");
//                    System.out.println("llll");
                    output.flush();

                    output.close();
                    input.close();
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }



    static class ClientThread extends Thread {
        private String ip;
        private int port;
        private int localPort;

        public ClientThread(String ip, int port, int localPort){
            this.ip = ip;
            this.port = port;
            this.localPort = localPort;
        }

        @Override
        public void run(){
            try {
                //1.建立客户端socket连接，指定服务器位置及端口
                Socket socket = new Socket(ip, port);
                //2.得到socket读写流
                PrintWriter pw = new PrintWriter(socket.getOutputStream());
                //输入流
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                //3.利用流按照一定的操作，对socket进行读写操作
                JSONObject msg = MsgUtil.createRegister("", "localhost", localPort);
                pw.write(msg.toJSONString());
                pw.flush();
                socket.shutdownOutput();
                //接收服务器的相应
                //4.读取以及响应
                String line = "";
                StringBuilder info = new StringBuilder();
                while ( (line = br.readLine()) != null ){
                    info.append(line);
                }
                System.out.println(info);
                //4.关闭资源
                br.close();
                pw.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
