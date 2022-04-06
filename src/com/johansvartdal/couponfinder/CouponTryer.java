package com.johansvartdal.couponfinder;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

public class CouponTryer {

    private String couponToTry = "hei";
    private HttpURLConnection http;
    private int id = 0;
    private String connectionURL;
    private String couponKey;

    public CouponTryer(int id, String connectionURL, String couponKey) {
        this.connectionURL = connectionURL;
        this.id = id;
        this.couponKey = couponKey;
    }

    private void establishConnection() {
        try {
            URL url = new URL(connectionURL);
            URLConnection con = url.openConnection();
            http = (HttpURLConnection)con;
            http.setRequestMethod("POST"); // PUT is another valid option
            http.setDoOutput(true);
        }catch (IOException e) {
            establishConnection();
        }
    }

    public void tryCoupon(String coupon) throws InterruptedException {
        Boolean waitingForAnswer = true;
        this.couponToTry = coupon;

        establishConnection();

        Map<String,String> arguments = new HashMap<>();
        arguments.put(couponKey, couponToTry);

        StringJoiner sj = new StringJoiner("&");
        try {
            for(Map.Entry<String,String> entry : arguments.entrySet()) {
                sj.add(URLEncoder.encode(entry.getKey(), "UTF-8") + "=" + URLEncoder.encode(entry.getValue(), "UTF-8"));
            }

            byte[] out = sj.toString().getBytes(StandardCharsets.UTF_8);
            int length = out.length;

            http.setFixedLengthStreamingMode(length);
            http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            http.connect();
            try(OutputStream os = http.getOutputStream()) {
                os.write(out);
            }

            InputStream is = http.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            while (waitingForAnswer) {
                String readLine = br.readLine();
                if (readLine != null && !readLine.isEmpty()) {
                    if (!readLine.contains("ugyldig")) {
                        System.out.println("---------- GYLDIG KUPONG! ----------");
                        threadPrint("The coupon that worked: " + coupon + ". Result: " + readLine);
                        System.out.println("------------------------------------");
                        System.exit(0);
                    }
                    waitingForAnswer = false;
                }
                Thread.sleep(50);
            }
        }catch (IOException e) {
            tryCoupon(coupon);
        }
    }

    public void threadPrint(String message) {
        System.out.println("Thread(" + id + "): " + message);
    }
}
