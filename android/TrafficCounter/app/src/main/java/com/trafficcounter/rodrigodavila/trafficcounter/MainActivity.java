package com.trafficcounter.rodrigodavila.trafficcounter;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private final int REQUEST_ENABLE_BT_START_PRESS = 11;

    private Intent beaconServiceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button btServiceStart = (Button) findViewById(R.id.btStart);
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
            startAndBindBeaconService();
        }
    }

    private void startAndBindBeaconService() {
        beaconServiceIntent = new Intent(MainActivity.this, BeaconService.class);
        startService(beaconServiceIntent);
        Toast.makeText(this, "Beacon service initialized.", Toast.LENGTH_LONG).show();
    }
}
