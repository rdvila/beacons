package com.example.rodrigodavila.beacon;

import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;

import java.util.Arrays;

public class Beacon {

    public byte[] id;
    public int rssi;
    public long timeStampNanos;
    public int txPowerLevel;
    public String alias;
    public String address;
    public double distance;

    public static Beacon from(ScanResult result) {
        Beacon beacon = new Beacon();

        beacon.id = extractEddystoneId(result.getScanRecord());
        beacon.rssi = result.getRssi();
        beacon.timeStampNanos = result.getTimestampNanos();
        beacon.txPowerLevel = result.getScanRecord().getTxPowerLevel();
        beacon.alias = getAlias(result.getDevice().getAddress());
        beacon.address = result.getDevice().getAddress();
        beacon.distance = RSSIDistanceEstimation.calculate(beacon.txPowerLevel, beacon.rssi);
        return beacon;
    }

    private static byte[] extractEddystoneId(ScanRecord record) {
        if (isEddystoneBeacon(record)) {
            return Arrays.copyOfRange(record.getServiceData(EddystoneScanSettings.SERVICE_UUID), 2, 18);
        }
        return null;
    }

    public static boolean isEddystoneBeacon(ScanRecord record) {
        byte[] servData = record.getServiceData(EddystoneScanSettings.SERVICE_UUID);
        if (servData != null && servData[0] == EddystoneScanSettings.UID_FRAME_TYPE) {
            return true;
        }
        return false;
    }

    private static String getAlias(String address) {
        if (address.equals("D3:35:8A:6E:E3:A4")) {
            return BeaconsAlias._FIdo;
        }

        if (address.equals("E4:52:5E:4E:A6:EC")) {
            return BeaconsAlias._b6sI;
        }

        if (address.equals("F7:7D:99:E5:0C:5E")) {
            return BeaconsAlias._0Fbe;
        }

        return address;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getAlias(address)).append(" ")
                .append(distance).append("m ")
                .append(alias).append(" ")
                .append(toHexString(id));

        return builder.toString();
    }

    private static final char[] HEX = "0123456789ABCDEF".toCharArray();
    private static String toHexString(byte[] bytes) {
        char[] chars = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            int c = bytes[i] & 0xFF;
            chars[i * 2] = HEX[c >>> 4];
            chars[i * 2 + 1] = HEX[c & 0x0F];
        }
        return new String(chars).toLowerCase();
    }
}
