package com.example.rodrigodavila.beacon;

import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.joda.time.Instant;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final int REQUEST_ENABLE_BT_START_PRESS = 11;

    private BeaconService beaconservice;
    ArrayAdapter<Beacon> adapter;
    private ArrayList<Beacon> beacons = new ArrayList<>();

    private BeaconScaneerCountDownTimer countDown;

    private float distance = -1;
    private float minutes = -1;

    private Intent beaconServiceIntent;
    private Intent accelerometerServiceIntent;
    private Intent magneticFieldServiceIntent;

    BeaconSubscriber logger = new BeaconSubscriber() {
        @Override
        public void update(List<Beacon> beacons) {
            for (Beacon b : beacons) {
                File f = new File(getExternalFilesDir(null), String.format("beacon_%s_%.3f_%.3f.log", b.alias, minutes, distance));
                SimpleDateFormat formatter= new SimpleDateFormat("HH:mm:ss");
                try (PrintWriter p =new PrintWriter(new BufferedWriter(new FileWriter(f, true)))) {
                    p.println(String.format("%s, %s, %s, %s, %s", formatter.format(new Date()), b.alias, b.rssi, b.txPowerLevel, b.timeStampNanos));
                } catch (IOException e) {
                    Log.e("Erro beacons", "cannot write logs", e);
                }
            }
        }

        @Override
        public void error(String message) {

        }
    };

    BeaconSubscriber subs = new BeaconSubscriber() {
        @Override
        public void update(List<Beacon> bs) {
            for (Beacon b : bs) {
                if (beacons.contains(b)) {
                    beacons.set(beacons.indexOf(b), b);
                } else {
                    beacons.add(b);
                }
                adapter.notifyDataSetChanged();
            }
        }

        @Override
        public void error(String message) {

        }
    };

    class BeaconScaneerCountDownTimer extends CountDownTimer {
        public BeaconScaneerCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            MainActivity.this.updateButtonTime(millisUntilFinished);
        }

        @Override
        public void onFinish() {
            MainActivity.this.startOrStopCountDownTimer();
        }
    }

    private void stopScanning() {
        distance = -1;
        minutes = -1;
        beacons.clear();
        adapter.notifyDataSetChanged();
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            BeaconService.LocalBind binder = (BeaconService.LocalBind) service;
            beaconservice = binder.getService();
            beaconservice.publisher.attach(subs);
            beaconservice.publisher.attach(logger);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // detach subscriber
            beaconservice = null;
            beaconservice.publisher.detach(subs);
            beaconservice.publisher.detach(logger);
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

        final Button btServiceStart = (Button) findViewById(R.id.btStartService);
        btServiceStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startBeaconService(REQUEST_ENABLE_BT_START_PRESS);
            }
        });

        ListView lvBeacons = (ListView)findViewById(R.id.lvBeacons);
       adapter = new ArrayAdapter<Beacon>(this, R.layout.simplerow, beacons);
        lvBeacons.setAdapter(adapter);
    }

    private void startBeaconService(int ret) {
        BluetoothAdapter bAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!bAdapter.isEnabled()) {
            Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetoothIntent, ret);
        } else {
            startAndBindBeaconService();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode < 0) {
            if (requestCode == REQUEST_ENABLE_BT_START_PRESS) {
                startAndBindBeaconService();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void startAndBindBeaconService() {
        beaconServiceIntent = new Intent(MainActivity.this, BeaconService.class);
        startService(beaconServiceIntent);
        if (beaconservice == null) {
            bindService(beaconServiceIntent, connection, BIND_AUTO_CREATE);
        }
        Toast.makeText(this, "Beacon service initialized.", Toast.LENGTH_LONG).show();
        startOrStopCountDownTimer();

    }

    private void startOrStopCountDownTimer() {
        if (countDown == null) {
            final EditText edTime = (EditText)findViewById(R.id.edTime);
            final EditText edDistance = (EditText)findViewById(R.id.edDistance);

            if (edDistance.getText().toString().trim().equals("") || edTime.getText().toString().trim().equals("")) {
                Toast.makeText(this, "Enter distance and time", Toast.LENGTH_SHORT).show();
                return;
            }

            distance = Float.valueOf(edDistance.getText().toString()).floatValue();
            minutes = Float.valueOf(edTime.getText().toString()).floatValue();

            final int timeInMillis = (int) (minutes * 60000);
            countDown = new BeaconScaneerCountDownTimer(timeInMillis, 1000);
            countDown.start();



        } else {
            countDown.cancel();
            countDown = null;
            resetStartButton();
            stopScanning();
        }
    }

    private void resetStartButton() {
        final Button startButton = (Button) findViewById(R.id.btStartService);
        startButton.setText(R.string.bt_start_name);
    }

    private void updateButtonTime(long time) {
        final Button startButton = (Button) findViewById(R.id.btStartService);
        startButton.setText(String.format("%02d:%02d", (time/60000), (time/1000)%60));
    }
}

