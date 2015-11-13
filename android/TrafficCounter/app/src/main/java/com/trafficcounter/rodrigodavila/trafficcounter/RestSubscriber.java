package com.trafficcounter.rodrigodavila.trafficcounter;

import android.os.AsyncTask;
import android.util.Log;

import com.koushikdutta.ion.Ion;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class RestSubscriber implements BeaconSubscriber {

    class AsyncPost extends AsyncTask<String, Void, Void> {

        private final Beacon b;

        public AsyncPost(Beacon beacon) {
            super();
            b = beacon;
        }

        @Override
        protected Void doInBackground(String... params) {
            URL request = null;
            try {
                request = new URL("https://hokettrafficmonitor.herokuapp.com/beacons/");
                HttpsURLConnection conn = (HttpsURLConnection) request.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");

               String json = ("{    \n" +
                        "     \"beacons\": [ \n" +
                        String.format("     { \"mac\": \"%s\", \"id\": \"%s\", \"alias\": \"%s\", \"powerLevel\": \"%s\", \"rssi\": \"%s\", \"beaconTimestamp\": \"%s\" }",
                                b.address, Beacon.toHexString(b.id), b.alias, b.txPowerLevel, b.rssi, b.timeStampNanos) +
                        "     ],\n" +
                        "     \"customer\": {\n" +
                        "        \"id\": \"0\",\n" +
                        String.format("        \"timestamp\": \"%s\", \n", System.currentTimeMillis()) +
                        String.format("        \"device\" : \"%s %s %s\"\n", android.os.Build.DEVICE, android.os.Build.MODEL, android.os.Build.PRODUCT) +
                        "     }\n" +
                        "    }");

                byte[] jsonbytes = json.getBytes();
                conn.setFixedLengthStreamingMode(jsonbytes.length);

                BufferedOutputStream buf = new BufferedOutputStream(conn.getOutputStream());

                buf.write(jsonbytes);
                buf.flush();
                buf.close();

                Log.i("JSON", json);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    public void update(List<Beacon> beacons) throws IOException {

        for (Beacon b : beacons) {
           AsyncPost task = new AsyncPost(b);
            task.execute();
        }
    }

    @Override
    public void error(String message) {
        Log.e("RestSubscriber", message);
    }
}
