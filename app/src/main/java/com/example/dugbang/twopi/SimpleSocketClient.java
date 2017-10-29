package com.example.dugbang.twopi;

import java.io.DataOutputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by dugbang on 2017-10-28.
 */

public class SimpleSocketClient {
    private static int PORT = 8888;
    private static Socket socket;

    public static void send(String str) {
        try {
            socket = new Socket("localhost", PORT);
            OutputStream o_stream = socket.getOutputStream();
            DataOutputStream dos = new DataOutputStream(o_stream);
            dos.writeUTF(str);
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        send("클라이언트 메시지입니다.");
    /*
        open();
        send("클라이언트에서 (...안드로이드 스튜디오...) 보내는 메시지입니다.");

        try {
//          소켓 생성
            socket = new Socket("localhost", PORT);

            System.out.println("console> PORT(" + PORT + ") 로 접속을 시도합니다.");

            OutputStream o_stream = socket.getOutputStream();
            DataOutputStream dos = new DataOutputStream(o_stream);
            dos.writeUTF("클라이언트에서 (안드로이드 스튜디오) 보내는 메시지입니다.");

//          소켓 닫음
            socket.close();

//          프로그램 종료
            System.exit(0);

//        예외처리
        } catch (Exception e) {
            e.printStackTrace();
        }
    */
    }
}
