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
import android.widget.RadioGroup;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import static com.example.dugbang.twopi.SimpleSockterServer.PORT;

public class MainActivity extends AppCompatActivity {

    private TextView output;
    private TextView txt_ipAddress;
    private ServerThread thread;
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
    private EditText macAddress;
    private SendMassgeHandler mMainHandler;
    private static final int HANDLER_EVENT_SEND_MSG_OUTPUT = 0;
    private static final int HANDLER_EVENT_ACTION_MESSAGE = 1;
    private WebView lWebView;

    private boolean webviewFlag;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bleFlag == true) {
            bleReceiver.mBluetoothLeScanner.stopScan(mScanCallback);
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
//        macAddress = (EditText) findViewById(R.id.macAddress);
        output = (TextView) findViewById(R.id.textView);
        output.setMovementMethod(new ScrollingMovementMethod());

        webviewFlag = true;

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

//        lWebView.setWebViewClient(new MyCustomWebViewClient());
//        lWebView.setWebViewClient(new WebViewClient() {
//            // autoplay when finished loading via javascript injection
//            public void onPageFinished(WebView view, String url) { lWebView.loadUrl("javascript:(function() { document.getElementsByTagName('video')[0].play(); })()"); }
//        });
//        settings.setAppCacheEnabled(true);
//        settings.setMediaPlaybackRequiresUserGesture(false);
//        lWebView.mediaPlaybackRequiresUserAction =
//        settings.setJavaScriptCanOpenWindowsAutomatically(true);
//        lWebView.getSettings().setJavaScriptEnabled(true);
//        lWebView.setMediaPlaybackRequiresUserGesture(false);
//        lWebView.getSettings().setAppCacheEnabled(true);


        rg = (RadioGroup) findViewById(R.id.radioGroup1);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.bleOff) {
                    bleFlag = false;
                    bleReceiver.mBluetoothLeScanner.stopScan(mScanCallback);
                } else {
                    bleFlag = true;
//                    bleReceiver.setScanFilterOfMacAccress(macAddress.getText().toString());
                    bleReceiver.setScanFilterOfMacAccress("FA:70:00:00:00:FA");
                    bleReceiver.mBluetoothLeScanner.startScan(bleReceiver.scanFilters, bleReceiver.getScanSettings(), mScanCallback);
                }
            }
        });

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

                    mMajor = (scanData[25] & 0xff) * 0x100 + (scanData[26] & 0xff);
                    mMinor = (scanData[27] & 0xff) * 0x100 + (scanData[28] & 0xff);

                    Message msg = mMainHandler.obtainMessage();
                    msg.what = HANDLER_EVENT_SEND_MSG_OUTPUT;
                    msg.obj = "onScanResult; \n" + strScanResult + "\n" + mMajor + "\n" + mMinor + "\n";
                    mMainHandler.sendMessage(msg);

                    // TODO; 실제 블록 팟이 동작할 경우 활성화 시킨다.
//                    blockId = (scanData[26] & 0xff) * 0x10000
//                            + (scanData[27] & 0xff) * 0x100
//                            + (scanData[28] & 0xff);
//                    stateRule.insertBlock(blockId);
//
//                    msg = mMainHandler.obtainMessage();
//                    msg.what = HANDLER_EVENT_ACTION_MESSAGE;
//                    msg.obj = stateRule.getOutMsg();
//                    mMainHandler.sendMessage(msg);

//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    output.setText("onScanResult; \n" + strScanResult + "\n" + mMajor + "\n" + mMinor + "\n");
//                                }
//                            });
//                        }
//                    }).start();

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

        Button clearButton = (Button) findViewById(R.id.clear);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                output.setText("");
                String path_str = stateRule.getRoot();
//                lWebView.loadUrl("file:///" + path_str + "test_html/index.html");

                if (webviewFlag) {
                    lWebView.loadUrl("file:///" + path_str + "test_html/index.html");
                    webviewFlag = false;
                } else {
                    lWebView.loadUrl("file:///" + path_str + "test_html_2/index.html");
                    webviewFlag = true;
                }
            }
        });

        Button logUploadButton = (Button) findViewById(R.id.logUpload);
        logUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO; 업로드 프로세스 구현...
                output.setText("upload process 구현하기...");
            }
        });

        stateRule = new StateRule(getApplicationContext());
        mMainHandler = new SendMassgeHandler();

        getLocalIpAddress();
        try {
            thread = new ServerThread();
            thread.start();
            println("서비스가 시작되었습니다.\n");
        } catch (IOException e) {
            println("Server Thread를 시작하지 못했습니다." + e.toString());
        }
    }

    private void println(String outputText) {
        String prevText = output.getText().toString() + "\n" + outputText;
        output.setText(prevText);
//        int lineTop =  output.getLayout().getLineTop(output.getLineCount()) ;
//        int scrollY = lineTop - output.getHeight();
//        if (scrollY > 0) {
//            output.scrollTo(0, scrollY);
//        } else {
//            output.scrollTo(0, 0);
//        }
    }

    private class SendMassgeHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case HANDLER_EVENT_SEND_MSG_OUTPUT:
//                    println((String)msg.obj);
                    output.setText((String)msg.obj);
                    break;
                case HANDLER_EVENT_ACTION_MESSAGE:
                    // TODO; 이벤트 정보를 받아서 화면에 영상을 출력할 때 사용...
//                    println((String)msg.obj);
//                    output.setText((String)msg.obj);
                    String prevText = output.getText().toString() + "\n\n" + (String)msg.obj;
                    output.setText(prevText);
                    break;
                default:
                    break;
            }
        }
    }

    class ServerThread extends Thread {
//        Handler mHandler = new Handler();
        private ServerSocket serverSocket;

        public ServerThread() throws IOException {
            serverSocket = new ServerSocket(PORT);
        }

        @Override
        public void run() {
            while(true) {
                try {
                    Socket socket = serverSocket.accept();

                    InputStream i_stream = socket.getInputStream();
                    DataInputStream dis = new DataInputStream(i_stream);

                    String line = dis.readUTF();
                    blockId = Integer.parseInt(line.substring(line.indexOf(",") + 1));
                    stateRule.insertBlock(blockId);

                    Message msg = mMainHandler.obtainMessage();
                    msg.what = HANDLER_EVENT_SEND_MSG_OUTPUT;
                    msg.obj = "console> 수신 blockId : " + blockId + "\n" + stateRule.getOutStr();
                    mMainHandler.sendMessage(msg);

                    msg = mMainHandler.obtainMessage();
                    msg.what = HANDLER_EVENT_ACTION_MESSAGE;
                    msg.obj = stateRule.getOutMsg();
                    mMainHandler.sendMessage(msg);

//                    mHandler.post(new Runnable() {
//                        public void run() {
//                            println("console> 수신 blockId : " + blockId);
//                            println(stateRule.getOutStr());
//                        }
//                    });

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

//    private class MyCustomWebViewClient extends WebViewClient {
//        @Override
//        public boolean shouldOverrideUrlLoading(WebView view, String url) {
//            view.loadUrl(url);
//            return true;
//        }
//    }
}
