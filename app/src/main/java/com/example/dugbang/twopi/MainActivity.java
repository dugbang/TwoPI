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
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
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
        macAddress = (EditText) findViewById(R.id.macAddress);
        output = (TextView) findViewById(R.id.textView);

        rg = (RadioGroup) findViewById(R.id.radioGroup1);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.bleOff) {
                    bleFlag = false;
                    bleReceiver.mBluetoothLeScanner.stopScan(mScanCallback);
                } else {
                    bleFlag = true;
                    bleReceiver.setScanFilterOfMacAccress(macAddress.getText().toString());
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
//                    blockId = (scanData[26] & 0xff) * 0x10000
//                            + (scanData[27] & 0xff) * 0x100
//                            + (scanData[28] & 0xff);
//                    stateRule.insertBlock(blockId);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    output.setText("onScanResult; \n" + strScanResult + "\n" + mMajor + "\n" + mMinor + "\n");
                                }
                            });
                        }
                    }).start();

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
        getLocalIpAddress();

        try {
            thread = new ServerThread();
            thread.start();
            output.setText("서비스가 시작되었습니다.\n");
        } catch (IOException e) {
            output.setText("Server Thread를 시작하지 못했습니다." + e.toString());
        }
    }

    class ServerThread extends Thread {
        Handler mHandler = new Handler();
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
                    blockId = Integer.parseInt(line.substring(line.indexOf(",")+1));
                    stateRule.insertBlock(blockId);

                    mHandler.post(new Runnable() {
                        public void run() {
                            output.setText("console> 수신 blockId : " + blockId);
                        }
                    });
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
