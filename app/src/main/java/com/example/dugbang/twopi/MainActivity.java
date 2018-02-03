package com.example.dugbang.twopi;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.InputDevice;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;

import static com.example.dugbang.twopi.SimpleSockterServer.PORT;

public class MainActivity extends AppCompatActivity {

    private TextView txt_output;
    private TextView txt_ipAddress;

    private EditText edit_macAddress;
    private EditText edit_bleSleepDuration;
    private int bleSleepDuration;

    private RadioButton rb_bleOff;
    private RadioButton rb_bleOn;

    private int blockId;
    private StateRule stateRule;
    private RadioGroup rg;
    private boolean bleFlag = false;

    private int mMajor;
    private int mMinor;

    BleReceiver bleReceiver;
    private static final int PERMISSIONS = 100;
    ScanCallback mScanCallback;
    String strScanResult;
    private SendMassgeHandler mMainHandler;

    private static final int HANDLER_EVENT_SEND_MSG_OUTPUT = 0;
    private static final int HANDLER_EVENT_ACTION_MESSAGE = 1;
    private static final int HANDLER_EVENT_BLOCK_ID_BLE = 2;
    private static final int HANDLER_EVENT_BLOCK_ID_SOCKET = 3;
    private static final int HANDLER_EVENT_BLE_ON = 4;

    private WebView lWebView;

    private HashMap<Integer, String> mappingNFC;
    private HashMap<Integer, Integer> mappingNFCtoBlockId;

    private BleTimer bleTimer;
    private ServerThread thread;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bleFlag == true) {
            BleOff();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSIONS);

        bleReceiver = new BleReceiver(BluetoothAdapter.getDefaultAdapter());

        txt_ipAddress = (TextView) findViewById(R.id.ipAddress);
        edit_macAddress = (EditText) findViewById(R.id.macAddress);
        edit_bleSleepDuration = (EditText) findViewById(R.id.bleSleepDuration);

        txt_output = (TextView) findViewById(R.id.textView);
        txt_output.setMovementMethod(new ScrollingMovementMethod());

        rb_bleOn = (RadioButton) findViewById(R.id.bleOn);
        rb_bleOff = (RadioButton) findViewById(R.id.bleOff);

        stateRule = new StateRule(getApplicationContext());
        mMainHandler = new SendMassgeHandler();

        lWebView = (WebView) findViewById(R.id.webView);
        lWebView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                float x = view.getWidth() / 2;
                float y = 2 * view.getHeight() / 3;

                MotionEvent motionEventDown = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, x, y, 0.5f, 0.5f, 0, 0.5f, 0.5f, InputDevice.SOURCE_TOUCHSCREEN, 0);
                MotionEvent motionEventUp = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis() + 200, MotionEvent.ACTION_UP, x, y, 0.5f, 0.5f, 0, 0.5f, 0.5f, InputDevice.SOURCE_TOUCHSCREEN, 0);

                view.dispatchTouchEvent(motionEventDown);
                view.dispatchTouchEvent(motionEventUp);
            }
        });

        final WebSettings settings = lWebView.getSettings();
        settings.setJavaScriptEnabled(true);

        rg = (RadioGroup) findViewById(R.id.radioGroup1);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.bleOff) {
                    BleOff();
                } else {
                    BleOn();
                }
            }
        });

        // ========================================================
        // NFC mapping; 임시적으로 사용하는 것임...
        mappingNFCtoBlockId = new HashMap<Integer, Integer>();

        mappingNFC = new HashMap<Integer, String>();
        mappingNFC.put(560, "사용자 1");  // 사용자 1
        mappingNFC.put(561, "사용자 2");  // 사용자 2
        mappingNFC.put(559, "NEXT"); // NEXT
        mappingNFC.put(614, "NEXT"); // NEXT
        mappingNFC.put(100, "BACK"); // BACK

        mappingNFCtoBlockId.put(560, 0xFFFFFF);
        mappingNFCtoBlockId.put(561, 0xFFFFFF);
        mappingNFCtoBlockId.put(559, 2);
        mappingNFCtoBlockId.put(614, 2);
//        mappingNFCtoBlockId.put(100, 3);

        mappingNFC.put(100, "0");   // 0
        mappingNFC.put(567, "1");   // 1
        mappingNFC.put(721, "2");   // 2
        mappingNFC.put(571, "3");   // 3

        mappingNFCtoBlockId.put(567, 101);    // 1
        mappingNFCtoBlockId.put(721, 102);    // 2
        mappingNFCtoBlockId.put(571, 103);    // 3

        mappingNFC.put(562, "삼각형");   // 삼각형
        mappingNFC.put(708, "마름모");   // 마름모
        mappingNFC.put(564, "원");   // 원
        mappingNFC.put(100, "별");   // 별

        mappingNFCtoBlockId.put(562, 110);   // 삼각형
        mappingNFCtoBlockId.put(708, 111);   // 마름모
        mappingNFCtoBlockId.put(564, 112);   // 원
//        mappingNFCtoBlockId.put(100, "별");   // 별

        mappingNFC.put(717, "E");   // E
        mappingNFC.put(712, "L");   // L
        mappingNFC.put(569, "O");   // O
        mappingNFC.put(707, "P");   // P

        mappingNFCtoBlockId.put(717, 114);   // E
        mappingNFCtoBlockId.put(712, 115);   // L
        mappingNFCtoBlockId.put(569, 116);   // O
        mappingNFCtoBlockId.put(707, 117);   // P

        mappingNFC.put(710, "주관식 1");   // 주관식 1
        mappingNFC.put(100, "주관식 2");   // 주관식 2

        mappingNFCtoBlockId.put(710, 118);   // 주관식 1
        mappingNFCtoBlockId.put(100, 119);   // 주관식 2
        // ========================================================

        mScanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);
                try {
                    ScanRecord scanRecord = result.getScanRecord();
                    Log.d("getTxPowerLevel()", scanRecord.getTxPowerLevel() + "");
                    byte[] scanData = scanRecord.getBytes();

                    strScanResult = result.getDevice().getAddress()
                            + "\n" + result.getRssi()
                            + "\n" + result.getDevice().getName()
                            + "\n" + result.getDevice().getBondState()
                            + "\n" + result.getDevice().getType();
                    Log.d("onScanResult()", strScanResult);

//                    msg.what = HANDLER_EVENT_BLOCK_ID_BLE;
//                    blockId = mappingNFC.get((scanData[27] & 0xff) * 0x100 + (scanData[28] & 0xff));

//                    msg.obj = "onScanResult; \n" + strScanResult + "\n" + mMajor + "\t" + mMinor + "\n";

//                    blockId = (scanData[26] & 0xff) * 0x10000
//                            + (scanData[27] & 0xff) * 0x100
//                            + (scanData[28] & 0xff);

                    mMajor = (scanData[25] & 0xff) * 0x100 + (scanData[26] & 0xff);
                    mMinor = (scanData[27] & 0xff) * 0x100 + (scanData[28] & 0xff);

                    Message msg = mMainHandler.obtainMessage();
                    // TODO; 실제 블록 팟이 동작할 경우 활성화 시킨다.
//                    blockId = mappingNFCtoBlockId.get(mMinor);
//                    blockId = mMinor;
                    msg.what = HANDLER_EVENT_BLOCK_ID_BLE;
                    msg.obj = mappingNFCtoBlockId.get(mMinor);
                    mMainHandler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                super.onBatchScanResults(results);
                Log.d("onBatchScanResults", results.size() + "");
            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
                Log.d("onScanFailed()", errorCode + "");
            }
        };

        Button userButton = (Button) findViewById(R.id.blockid_user);
        userButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                txt_output.setText(edit_macAddress.getText().toString().replace(".", ":"));
//                blockId = 0xfffffe;
                Message msg = mMainHandler.obtainMessage();
                msg.what = HANDLER_EVENT_BLOCK_ID_BLE;
                msg.obj = 0xfffffe;
                mMainHandler.sendMessage(msg);
            }
        });

        Button backButton = (Button) findViewById(R.id.blockid_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                txt_output.setText("자동저장 기능으로 구현됨...");
//                blockId = 3;
                Message msg = mMainHandler.obtainMessage();
                msg.what = HANDLER_EVENT_BLOCK_ID_BLE;
                msg.obj = 3;
                mMainHandler.sendMessage(msg);
            }
        });

        Button nextButton = (Button) findViewById(R.id.blockid_next);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                txt_output.setText("자동저장 기능으로 구현됨...");
//                blockId = 2;
                Message msg = mMainHandler.obtainMessage();
                msg.what = HANDLER_EVENT_BLOCK_ID_BLE;
                msg.obj = 2;
                mMainHandler.sendMessage(msg);
            }
        });

        getLocalIpAddress();
        try {
            thread = new ServerThread();
            thread.start();
            println("서비스가 시작되었습니다.\n");

            bleTimer = new BleTimer();
            bleTimer.start();
        } catch (IOException e) {
            println("Server Thread를 시작하지 못했습니다." + e.toString());
        }
    }

    private void BleOn() {
        bleFlag = true;
        bleReceiver.setScanFilterOfMacAccress(edit_macAddress.getText().toString().replace(".", ":"));
        bleReceiver.mBluetoothLeScanner.startScan(bleReceiver.scanFilters, bleReceiver.getScanSettings(), mScanCallback);
    }

    private void BleOff() {
        bleFlag = false;
        bleReceiver.mBluetoothLeScanner.stopScan(mScanCallback);

        bleSleepDuration = Integer.parseInt(String.valueOf(edit_bleSleepDuration.getText()));
    }

    private void println(String outputText) {
        String prevText = txt_output.getText().toString() + "\n" + outputText;
        txt_output.setText(prevText);
    }

    private class SendMassgeHandler extends Handler {
        private String prevFileName = "";
        private String fileName;
        private String path_str;

        private File f;

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

//            int blockId;
            switch (msg.what) {
                case HANDLER_EVENT_BLE_ON:
//                    BleOn();
                    rb_bleOn.performClick();
                    System.out.println("rb_bleOn.performClick()");
                    break;
                case HANDLER_EVENT_BLOCK_ID_BLE:
                    if (msg.obj!=null) {
                        blockId = (Integer) msg.obj;
                        ActionBlockId();  // 인식할 수 없는 BlockId 입력시 대응...?
                        rb_bleOff.performClick();
//                    BleOff();
                        System.out.println("rb_bleOff.performClick()");
                    }
                    break;
                case HANDLER_EVENT_BLOCK_ID_SOCKET:
                    ActionBlockId();
                    break;
                case HANDLER_EVENT_SEND_MSG_OUTPUT:
                    rb_bleOff.performClick();
                    txt_output.setText((String) msg.obj);
                    break;
                case HANDLER_EVENT_ACTION_MESSAGE:
                    // TODO; 이벤트 정보를 받아서 화면에 영상을 출력할 때 사용...
                    String dir_path = (String) msg.obj;
                    txt_output.setText(txt_output.getText().toString() + "\n\n" + dir_path);
                    path_str = stateRule.getRoot();
                    f = new File(path_str + dir_path + "/index.html");
                    if (f.isFile()) {
                        lWebView.loadUrl("file:///" + path_str + dir_path + "/index.html");
                    }
                    break;
                default:
                    break;
            }
        }
        private void ActionBlockId() {
            stateRule.insertBlock(blockId);
            fileName = stateRule.getOutMsg();
            if (!fileName.equals(prevFileName)) {
                prevFileName = fileName;

                path_str = stateRule.getRoot();
                f = new File(path_str + fileName + "/index.html");
                if (f.isFile()) {
                    lWebView.loadUrl("file:///" + path_str + fileName + "/index.html");
                }
            }
            txt_output.setText("수신 block ID : " + blockId + "\n" +
                    stateRule.getOutStr() + "\n\n" + fileName);
        }
    }

    class BleTimer extends Thread {

        @Override
        public void run() {
            while (true) {
                DelayTime(200);

                if (bleFlag==false) {
                    DelayTime(bleSleepDuration);

                    Message msg = mMainHandler.obtainMessage();
                    msg.what = HANDLER_EVENT_BLE_ON;
                    mMainHandler.sendMessage(msg);
                }
            }
        }

        private void DelayTime(int millis) {
            try {
                Thread.sleep(millis);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    class ServerThread extends Thread {
        private ServerSocket serverSocket;

        public ServerThread() throws IOException {
            serverSocket = new ServerSocket(PORT);
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Socket socket = serverSocket.accept();

                    InputStream i_stream = socket.getInputStream();
                    DataInputStream dis = new DataInputStream(i_stream);

                    String line = dis.readUTF();
                    blockId = Integer.parseInt(line.substring(line.indexOf(",") + 1));
//                    stateRule.insertBlock(blockId);

                    Message msg = mMainHandler.obtainMessage();
                    msg.what = HANDLER_EVENT_BLOCK_ID_SOCKET;
//                    msg.obj = blockId;
                    mMainHandler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void getLocalIpAddress() {
        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        DhcpInfo dhcpInfo = wm.getDhcpInfo();
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
