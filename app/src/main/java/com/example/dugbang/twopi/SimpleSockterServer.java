package com.example.dugbang.twopi;


import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by dugbang on 2017-10-28.
 */

public class SimpleSockterServer {
    /*
    본 클래스 자체를 스레드에서 파생한 것으로 재정의하면 그냥 2PI 앱에서 사용가능
    이 경우 main => run 으로 변경
    2PI 앱에서 thread.start() 호출
     */
    static final int PORT = 8888;
    private static ServerSocket serverSocket;
    private static Socket socket;

    public static void main(String[] args) throws IOException {
        try {
            serverSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("console> 서버 : 클라이언트의 접속을 기다립니다.");
        StateRule stateRule = new StateRule(null);

//          소켓 서버가 종료될 때까지 무한루프
        while(true) {
            //System.out.println(getTime() + " 연결요청을 기다립니다.");

            try {
                socket = serverSocket.accept();
                //System.out.println("console> 서버 " + socket.getInetAddress() + " 클라이언트와 " +
                //        socket.getLocalPort() + " 포트로 연결되었습니다.");

                InputStream i_stream = socket.getInputStream();
                DataInputStream dis = new DataInputStream(i_stream);

//                System.out.println("console> 수신 response : " + dis.readUTF());

                String line = dis.readUTF();
                int pos = line.indexOf(",");
                int blockId = Integer.parseInt(line.substring(pos+1));

                System.out.println("blockId : " + blockId);
                System.out.println(stateRule.insertBlock(blockId));

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                socket.close();
            }
        }
    }

//    private static String getTime() {
//        SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd_HH.mm.ss");
//        return f.format(new Date());
//    }
}
