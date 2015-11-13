package com.example.rodrigodavila.beacon;

import android.app.Service;
import android.content.Intent;
import android.os.*;
import android.util.Log;

public class BeaconService extends Service {

    public  final BeaconPublisher publisher = new BeaconPublisher();
    public  final BeaconScanner scanner = new BeaconScanner(publisher);

    public final IBinder binder = new LocalBind();

    public class LocalBind extends Binder {
        BeaconService getService() {
            return BeaconService.this;
        }
    };

    private final class ServiceHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            long endTime = System.currentTimeMillis() + 2200;
            while (System.currentTimeMillis() < endTime) {
                synchronized(this) {
                    try {
                        scanner.scanBLEDevices(EddystoneScanSettings.SCAN_SETTINGS, EddystoneScanSettings.SCAN_FILTERS);
                        long waitTime = endTime - System.currentTimeMillis();
                        if (waitTime > 0) {
                            wait(waitTime);
                        }
                    } catch (InterruptedException e) {
                        // no exception here
                    } catch (UnsupportedOperationException e) {
                        Log.wtf("WTF", "Operação não suportada.", e);
                    }
                }
               sendEmptyMessage(0);
            }
        }
    }

    @Override
    public void onCreate() {
        publisher.attach(new TerminalLogSubscriber());

        Thread thread = new Thread(new Runnable() {
            public Handler handler;

            @Override
            public void run() {
                Looper.prepare();
                handler = new ServiceHandler();
                handler.sendEmptyMessage(0);
                Looper.loop();
            }
        });
        thread.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
}
