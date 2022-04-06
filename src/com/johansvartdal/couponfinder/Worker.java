package com.johansvartdal.couponfinder;

import java.io.*;
import java.util.*;

public class Worker {

    ArrayList<String> wordsTriedSincePrintout;
    ArrayList<String> combinations = new ArrayList<>();

    //Hardcode customization
    int secondsWaitBeforePrintout = 10;

    //Customization
    String connectionURL;
    String couponKey;
    int maxLengthCoupon;
    int numberOfWorkerThreads;
    String startAtWord;
    String availableChars;

    public Worker(String connectionURL, String couponKey, int threadCount, int couponLength, String selectedCharset, String startPosition) {
        this.connectionURL = connectionURL;
        this.couponKey = couponKey;
        this.numberOfWorkerThreads = threadCount;
        this.maxLengthCoupon = couponLength;
        this.availableChars = selectedCharset;
        this.startAtWord = startPosition;
    }

    public void startWorking() throws IOException {
        combinations = differentFlagPermutations(maxLengthCoupon, getEverySingleAlphabetCharArray(maxLengthCoupon));

        wordsTriedSincePrintout = new ArrayList<>();

        Thread printoutThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    startPrintoutLoop();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        printoutThread.start();

        Iterator iterator = combinations.iterator();

        if (!startAtWord.isEmpty()) {
            System.out.println("Skipping til this position: " + startAtWord + "...");
            while (iterator.hasNext()) {
                String nextWord = (String) iterator.next();
                wordsTriedSincePrintout.add(nextWord);
                if (startAtWord.equals(nextWord)) {
                    System.out.println("We are now at the correct start position. Starting workers...");
                    break;
                }
            }
        }

        for (int i = 0; i < numberOfWorkerThreads; i++) {
            int finalI = i;

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            CouponTryer couponTryer = new CouponTryer(finalI, connectionURL, couponKey);
                            if (iterator.hasNext()) {
                                String nextWord = (String) iterator.next();
                                wordsTriedSincePrintout.add(nextWord);
                                couponTryer.tryCoupon(nextWord);
                            }else {
                                couponTryer.threadPrint("We have now tried every possible coupons. No available coupon found within the range");
                                System.exit(0);
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            System.out.println("ERROR: Thread could not be interrupted: " + finalI);
                        }
                    }
                }
            });
            thread.start();
        }
    }

    int totalWordsTried = 0;

    private void startPrintoutLoop() throws InterruptedException {
        totalWordsTried += wordsTriedSincePrintout.size();

        String lastWordTried = "";
        if (wordsTriedSincePrintout.size() > 0) {
            lastWordTried = wordsTriedSincePrintout.get(wordsTriedSincePrintout.size()-1);
            if (lastWordTried == null) {
                lastWordTried = "";
            }
        }
        System.out.print("Amount of words that was tried the last " + secondsWaitBeforePrintout + " seconds: " + wordsTriedSincePrintout.size() + "! The last word that was tried: " + lastWordTried);
        System.out.println(". With this speed, there is: " + ((combinations.size()-totalWordsTried)/wordsTriedSincePrintout.size())/(secondsWaitBeforePrintout*60*60) + " hours left...");
        wordsTriedSincePrintout.clear();
        Thread.sleep(secondsWaitBeforePrintout*1000);
        startPrintoutLoop();
    }

    protected String[] getEverySingleAlphabetCharArray(int length) {
        String[] myArray = new String[availableChars.length()];
        for (int i = 0; i < availableChars.length(); i++) {
            myArray[i] = String.valueOf(availableChars.charAt(i));
        }
        return myArray;
    }

    static ArrayList<String> differentFlagPermutations(int X, String[] arr) {
        StringBuilder forPrintout = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            forPrintout.append(arr[i]);
        }
        System.out.println("Calculating all possible combinations of: " + forPrintout + " with a length of: " + X + "...");

        ArrayList<String> combinations = new ArrayList<>();
        String[] ml = arr;

        for(int z = 0; z < X - 1; z++) {
            Vector<String> tmp = new Vector<String>();

            for(int i = 0; i < arr.length; i++) {
                for(int k = 0; k < ml.length; k++) {
                    if (arr[i] != ml[k]) {
                        tmp.add(ml[k] + arr[i]);
                    }
                }
            }

            for(int i = 0; i < tmp.size(); i++) {
                combinations.add(tmp.get(i));
            }

            ml = tmp.toArray(new String[tmp.size()]);;
        }

        return combinations;
    }
}
