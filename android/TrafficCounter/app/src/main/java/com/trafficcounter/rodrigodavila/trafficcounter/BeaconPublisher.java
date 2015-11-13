package com.trafficcounter.rodrigodavila.trafficcounter;

import android.annotation.TargetApi;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.os.Build;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class BeaconPublisher extends ScanCallback {
    private List<BeaconSubscriber> subscribers = new LinkedList<>();

    private String[] messages = {
            "",
            "Already started",
            "Application registration failed",
            "Internal error",
            "Feature not supported",
            "Out of hardware resources",
    };

    public void attach(BeaconSubscriber subs) {
        subscribers.add(subs);
    }

    public void detach(BeaconSubscriber subs) {
        subscribers.remove(subs);
    }

    @Override
    public void onScanFailed(int errorCode) {
        for (BeaconSubscriber subs : subscribers) {
            subs.error(messages[errorCode]);
        }
    }

    @Override
    public void onBatchScanResults(List<ScanResult> results){
        List<Beacon> beacons = new ArrayList<>();
        for (ScanResult s : results) {
            if (Beacon.isEddystoneBeacon(s.getScanRecord())) {
                beacons.add(Beacon.from(s));
            }
        }

        if (beacons.size() > 0) {
            for (BeaconSubscriber subs : subscribers) {
                try {
                    subs.update(beacons);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onScanResult(int callbackType, ScanResult result) {
        if (!Beacon.isEddystoneBeacon(result.getScanRecord())) {
            return;
        }

        Beacon beacon = Beacon.from(result);
        for (BeaconSubscriber subs : subscribers) {
            try {
                subs.update(Arrays.asList(beacon));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
