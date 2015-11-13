package com.trafficcounter.rodrigodavila.trafficcounter;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

public interface BeaconSubscriber {
    void update(List<Beacon> beacons) throws IOException;
    void error(String message);
}
