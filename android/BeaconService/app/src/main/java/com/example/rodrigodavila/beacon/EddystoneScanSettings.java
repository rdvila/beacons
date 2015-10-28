package com.example.rodrigodavila.beacon;

import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.os.ParcelUuid;

import java.util.ArrayList;
import java.util.List;

public class EddystoneScanSettings {

    public static final ScanSettings SCAN_SETTINGS =
            new ScanSettings.Builder().
                    setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                    .build();

    public static final List<ScanFilter> SCAN_FILTERS = buildScanFilters();

    public static final byte UID_FRAME_TYPE = 0x00;

    public static final ParcelUuid SERVICE_UUID =
            ParcelUuid.fromString("0000FEAA-0000-1000-8000-00805F9B34FB");

    public static List<ScanFilter> buildScanFilters() {
        List<ScanFilter> scanFilters = new ArrayList<>();
        scanFilters.add( new ScanFilter.Builder()
                .setServiceUuid(SERVICE_UUID)
                .build());
        return scanFilters;
    }

}
