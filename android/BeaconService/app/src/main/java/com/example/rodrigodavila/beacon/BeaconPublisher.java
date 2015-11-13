package com.example.rodrigodavila.beacon;

import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

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
        if (subscribers.contains(subs)) {
            subscribers.remove(subs);
        }
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
                subs.update(beacons);
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
            subs.update(Arrays.asList(beacon));
        }
    }
}
