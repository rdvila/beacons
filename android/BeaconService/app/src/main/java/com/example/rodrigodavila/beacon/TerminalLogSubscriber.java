package com.example.rodrigodavila.beacon;

import android.util.Log;

import java.util.List;

public class TerminalLogSubscriber implements BeaconSubscriber {
    @Override
    public void update(List<Beacon> beacons) {
        for (Beacon b : beacons) {
            Log.i("TerminalLogSubscriber", b.toString());
        }
    }

    @Override
    public void error(String message) {
        Log.e("TerminalLogSubscriber", message);
    }
}
