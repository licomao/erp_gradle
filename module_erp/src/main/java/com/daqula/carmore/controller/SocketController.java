package com.daqula.carmore.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by swj on 2015/11/11.
 */
@Controller
public class SocketController {

    private Socket socket;

    private InputStream inputStream;

    private OutputStream outputStream;

    @RequestMapping(value = "/socket/connect/long", method = RequestMethod.POST)
    @ResponseBody
    public boolean init() {

        try {
            socket = new Socket("192.168.0.107", 9797);
            outputStream = socket.getOutputStream();
            inputStream = socket.getInputStream();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }

    @RequestMapping(value = "/socket/send", method = RequestMethod.POST)
    @ResponseBody
    public boolean sendMessage(String message){
        try {
            outputStream.write(message.getBytes());
//            socket.shutdownOutput();
            byte[] buf = new byte[1024];
            int len = inputStream.read(buf);
            System.out.println("接收返回的数据:" + new String(buf, 0, len));


            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @RequestMapping(value = "/socket/shutdown", method = RequestMethod.POST)
    @ResponseBody
    public boolean shutdown(){
        try {
            socket.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
