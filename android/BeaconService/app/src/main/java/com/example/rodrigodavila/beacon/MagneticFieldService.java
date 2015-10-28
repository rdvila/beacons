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

public class MagneticFieldService  extends Service {

    SensorManager sensorMgr;
    Sensor magneticField;
    SensorEventListener listener;

    @Override
    public void onCreate() {
        sensorMgr = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        magneticField = sensorMgr.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        listener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                Sensor sensor = event.sensor;
                if (sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                    float x = event.values[0];
                    float y = event.values[1];
                    float z = event.values[2];

                    Log.i("Magnetic Field", String.format("X = %f, Y = %f, Z = %f", x, y, z));
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        sensorMgr.registerListener(listener, magneticField, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
