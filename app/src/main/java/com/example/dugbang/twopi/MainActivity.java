package com.example.dugbang.twopi;

import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

import static com.example.dugbang.twopi.SimpleSockterServer.PORT;

public class MainActivity extends AppCompatActivity {

    private TextView output;
    private TextView txt_ipAddress;
    private ServerThread thread;
    private int blockId;
    private StateRule stateRule;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        output = (TextView) findViewById(R.id.textView);
        txt_ipAddress = (TextView) findViewById(R.id.ipAddress);

//        System.setProperty("org.apache.poi.javax.xml.stream.XMLInputFactory", "com.fasterxml.aalto.stax.InputFactoryImpl");
//        System.setProperty("org.apache.poi.javax.xml.stream.XMLOutputFactory", "com.fasterxml.aalto.stax.OutputFactoryImpl");
//        System.setProperty("org.apache.poi.javax.xml.stream.XMLEventFactory", "com.fasterxml.aalto.stax.EventFactoryImpl");

//        stateRule = new StateRule(getApplicationContext());

        getLocalIpAddress();

        try {
            thread = new ServerThread();
            thread.start();
            output.setText("서비스가 시작되었습니다.\n");
        } catch (IOException e) {
            output.setText("Server Thread를 시작하지 못했습니다." + e.toString());
        }

        System.out.println("ddd");

//        String outStr = "ddd...";
//        output.setText(txt_ipAddress.getText());
    }

    class ServerThread extends Thread {
        Handler mHandler = new Handler();

        private ServerSocket serverSocket;
//        private final ConsoleOutput out;

        public ServerThread() throws IOException {
            serverSocket = new ServerSocket(PORT);

//            out = new AndroidConsoleOutput();
//            out.mHandler = mHandler;
        }

        @Override
        public void run() {
//            System.out.println("console> 서버 : 클라이언트의 접속을 기다립니다.");
            while(true) {
                try {
                    Socket socket = serverSocket.accept();
                    //System.out.println("console> 서버 " + socket.getInetAddress() + " 클라이언트와 " +
                    //        socket.getLocalPort() + " 포트로 연결되었습니다.");

                    InputStream i_stream = socket.getInputStream();
                    DataInputStream dis = new DataInputStream(i_stream);

                    String line = dis.readUTF();
                    int pos = line.indexOf(",");
                    blockId = Integer.parseInt(line.substring(pos+1));
//                    stateRule.insertBlock(Integer.parseInt(line.substring(pos+1)));

                    mHandler.post(new Runnable() {
                        public void run() {
                            output.setText("console> 수신 blockId : " + blockId);
                        }
                    });

//                        System.out.println("console> 수신 response : " + dis.readUTF());
//                        System.out.println("blockId : " + blockId);
//                        System.out.println(stateRule.insertBlock(blockId));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void getLocalIpAddress() {
        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        DhcpInfo dhcpInfo = wm.getDhcpInfo() ;
        int serverIp = dhcpInfo.ipAddress;

        String ipAddressStr = String.format(
                "%d.%d.%d.%d",
                (serverIp & 0xff),
                (serverIp >> 8 & 0xff),
                (serverIp >> 16 & 0xff),
                (serverIp >> 24 & 0xff));

        txt_ipAddress.setText(ipAddressStr);
    }
}
