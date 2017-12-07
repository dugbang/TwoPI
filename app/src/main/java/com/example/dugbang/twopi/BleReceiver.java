package com.example.dugbang.twopi;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;

import java.util.List;
import java.util.Vector;

public class BleReceiver {
    BluetoothAdapter mBluetoothAdapter;
    BluetoothLeScanner mBluetoothLeScanner;
    BluetoothLeAdvertiser mBluetoothLeAdvertiser;
    ScanSettings.Builder mScanSettings;
    List<ScanFilter> scanFilters;

    public BleReceiver(BluetoothAdapter ble) {
        mBluetoothAdapter = ble;
        mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        mBluetoothLeAdvertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();

        mScanSettings = new ScanSettings.Builder();
        mScanSettings.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY);

        scanFilters = new Vector<>();
    }

    public void setScanFilterOfMacAccress(String bleMacAddress) {
        ScanFilter.Builder scanFilter = new ScanFilter.Builder();
        scanFilter.setDeviceAddress(bleMacAddress);
        scanFilters.clear();
        scanFilters.add(scanFilter.build());
    }

    public ScanSettings getScanSettings() {
        return mScanSettings.build();
    }

}