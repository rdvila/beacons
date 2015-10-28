package com.example.rodrigodavila.beacon;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

public class AccelerometerService  extends Service {

    SensorManager sensorMgr;
    Sensor acceleration;
    SensorEventListener listener;

    @Override
    public void onCreate() {
        sensorMgr = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        acceleration = sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        listener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                Sensor sensor = event.sensor;
                if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                    float x = event.values[0];
                    float y = event.values[1];
                    float z = event.values[2];

                    Log.i("Accelerometer", String.format("X = %f, Y = %f, Z = %f", x, y, z));
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        sensorMgr.registerListener(listener, acceleration, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onDestroy() {
        Log.e("Destroy", "E Morreu!");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
