package com.example.rodrigodavila.beacon;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class RSSIDistanceEstimation {

   public static double calculate(int txpower, int rssi) {
        if (rssi == 0) {
            return -1;
        } else {
            double ratio = rssi*(1.0/txpower);
            if (ratio < 1.0) {
                return Math.pow(ratio, 10);
            } else {
                return  (0.89976) * Math.pow(ratio, 7.7095) + 0.111;
            }
        }
   }
}
