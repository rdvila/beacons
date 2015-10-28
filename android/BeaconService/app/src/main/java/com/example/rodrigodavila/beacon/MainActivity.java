package com.example.rodrigodavila.beacon;

import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private BeaconService beaconservice;

    private final int REQUEST_ENABLE_BT_INIT = 10;
    private final int REQUEST_ENABLE_BT_START_PRESS = 11;

    private Intent beaconServiceIntent;
    private Intent accelerometerServiceIntent;
    private Intent magneticFieldServiceIntent;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            BeaconService.LocalBind binder = (BeaconService.LocalBind) service;
            beaconservice = binder.getService();
            // attach subscriber

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // detach subscriber
            beaconservice = null;
        }
    };

    class RssiHistory {
        public Deque<Integer> rssiHistory = new LinkedList<>();
        public void push(int rssi) {
            if (rssiHistory.size() >= 6) {
                rssiHistory.pollLast();
            }

            rssiHistory.push(rssi);
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            for (Integer i : rssiHistory) {
                builder.append(i).append(", ");
            }

            return builder.toString();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // List android Sensors
        SensorManager mgr = (SensorManager) getSystemService(SENSOR_SERVICE);
        List<Sensor> sensors = mgr.getSensorList(Sensor.TYPE_ALL);
        for (Sensor sensor : sensors) {
            Log.d("Sensors", "" + sensor.getName());
        }

//        accelerometerServiceIntent = new Intent(this, AccelerometerService.class);
//        startService(accelerometerServiceIntent);
//
//        magneticFieldServiceIntent = new Intent(this, MagneticFieldService.class);
//        startService(magneticFieldServiceIntent);

        beaconServiceIntent = new Intent(this, BeaconService.class);
        if (beaconservice == null) {
            bindService(beaconServiceIntent, connection, BIND_AUTO_CREATE);
        }

        BluetoothAdapter bAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bAdapter == null) {
            Toast.makeText(this, "Bluetooth not supported.", Toast.LENGTH_SHORT).show();
        }

        startBeaconService(REQUEST_ENABLE_BT_INIT);
        final Button btServiceStart = (Button) findViewById(R.id.btStartService);
        btServiceStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startBeaconService(REQUEST_ENABLE_BT_START_PRESS);
            }
        });
    }

    private void startBeaconService(int ret) {
        BluetoothAdapter bAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!bAdapter.isEnabled()) {
            Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetoothIntent, ret);
        } else {
            startService(beaconServiceIntent);
            Toast.makeText(this, "Beacon service reinitialized.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode < 0) {
            if (requestCode == REQUEST_ENABLE_BT_START_PRESS || requestCode == REQUEST_ENABLE_BT_START_PRESS) {
                startService(beaconServiceIntent);
                Toast.makeText(this, "Beacon service initialized.", Toast.LENGTH_LONG).show();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}

