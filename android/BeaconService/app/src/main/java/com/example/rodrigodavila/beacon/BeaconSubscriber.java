package com.example.rodrigodavila.beacon;

import java.util.List;

public interface BeaconSubscriber {
    void update(List<Beacon> beacons);
    void error(String message);
}
