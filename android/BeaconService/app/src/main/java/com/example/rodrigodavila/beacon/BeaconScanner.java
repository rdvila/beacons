package com.example.rodrigodavila.beacon;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Intent;
import android.os.Handler;
import android.os.ParcelUuid;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BeaconScanner {

    private ScanCallback scanCallback;

    private static final int BLE_SCAN_TIME_MILLIS = 2000;

    public BeaconScanner(ScanCallback scanCallback) {
        this.scanCallback = scanCallback;
    }

    public void scanBLEDevices(ScanSettings settings, List<ScanFilter> filters) {
        final BluetoothAdapter adapter   = BluetoothAdapter.getDefaultAdapter();
        final BluetoothLeScanner scanner = adapter.getBluetoothLeScanner();
        scanner.startScan(filters, settings, scanCallback);

        Runnable stopScanning = new Runnable() {
            @Override
            public void run() {
                scanner.stopScan(scanCallback);
            }
        };

        Handler handler = new Handler();
        handler.postAtTime(stopScanning, BLE_SCAN_TIME_MILLIS);
    }
}
