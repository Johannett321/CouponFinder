package com.johansvartdal.couponfinder;

import java.io.IOException;
import java.util.Scanner;

public class MainClass {

    private static final String version = "1.0.0";

    private static String charset1 = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
    private static String charset2 = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static String charset3 = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
    private static String charset4 = "abcdefghijklmnopqrstuvwxyz1234567890";
    private static String charset5 = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static String charset6 = "abcdefghijklmnopqrstuvwxyz";
    private static String charset7 = "1234567890";

    static Scanner scanner;

    public static void main (String[] args) {
        scanner = new Scanner(System.in);
        System.out.println("-------------------------------- WELCOME --------------------------------");
        System.out.println("Welcome to CouponFinder (version " + version + ")! This application will try every single possible coupon on a website you choose, and help you find the one that works!");
        System.out.println("If you need a tutorial, please watch this video: https://youtu.be/26JnoJYlMA4");
        System.out.println("Please press the 'enter' key to continue");
        scanner.nextLine();
        System.out.println("------------------------------- Questions -------------------------------");
        System.out.println("First, what is the URL that is used to perform the POST request? (You can find this URL using the network tab " +
                "in the Google Chrome Inspection Tool)");
        String connectionURL = stringIsFilledWithMaxLength(10000);
        System.out.println("What is the name of the key in the postrequest, defining the coupon that is currently being tried? (case sensitive)");
        String couponKey = stringIsFilledWithMaxLength(100);
        System.out.println("All the mandatory configuration is now done! The coupon finder is ready to start. However, if you want to make it faster, you can also do the advanced configuration as well. Would you like to run the advanced config? (Y/N)");
        String advancedMode = scanner.nextLine();

        int threadCount = 1;
        int couponLength = 5;
        String selectedCharset = charset3;
        String startPosition = "";

        if (advancedMode.equalsIgnoreCase("y")) {
            System.out.println("---------------------------- Advanced config ----------------------------");
            System.out.println("How many threads would you like? (1-100) (This will influence your computers performance! Higher is faster, but slows down computer a lot)");
            threadCount = numberIsHigherThanAndLowerThan(1, 100);
            System.out.println("What is the maximum length of a coupon you would like to try? How many characters? (1-6)");
            couponLength = numberIsHigherThanAndLowerThan(1, 6);
            System.out.println("What characters will the coupons contain? You can only select one (1-7)");
            System.out.println("1) " + charset1);
            System.out.println("2) " + charset2);
            System.out.println("3) " + charset3);
            System.out.println("4) " + charset4);
            System.out.println("5) " + charset5);
            System.out.println("6) " + charset6);
            System.out.println("7) " + charset7);
            selectedCharset = String.valueOf(numberIsHigherThanAndLowerThan(1, 7));

            switch (selectedCharset) {
                case "1":
                    selectedCharset = charset1;
                    break;
                case "2":
                    selectedCharset = charset2;
                    break;
                case "3":
                    selectedCharset = charset3;
                    break;
                case "4":
                    selectedCharset = charset4;
                    break;
                case "5":
                    selectedCharset = charset5;
                    break;
                case "6":
                    selectedCharset = charset6;
                    break;
                case "7":
                    selectedCharset = charset7;
                    break;
            }

            System.out.println("Lastly, where should the workers start? They will go through like this A, B, C...BA, CA, DA...AB, CB, DB... (just press enter if you want them to start from the beginning)");
            startPosition = scanner.nextLine();
        }

        System.out.println("------------------------ Starting coupon finder ------------------------");
        Worker worker = new Worker(connectionURL, couponKey, threadCount, couponLength, selectedCharset, startPosition);
        try {
            worker.startWorking();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String stringIsFilledWithMaxLength(int maxLength) {
        String answer = scanner.nextLine();
        if (answer == null || answer.isEmpty()) {
            System.out.println("Please answer the question");
            return stringIsFilledWithMaxLength(maxLength);
        }
        if (answer.length() > maxLength) {
            System.out.println("Your answer was a bit too long, wasn't it?");
            return stringIsFilledWithMaxLength(maxLength);
        }
        return answer;
    }

    private static int numberIsHigherThanAndLowerThan(int min, int max) {
        String answer = scanner.nextLine();
        if (answer == null || answer.isEmpty()) {
            System.out.println("Please answer the question");
            return numberIsHigherThanAndLowerThan(min, max);
        }
        if (Integer.parseInt(answer) >= min && Integer.parseInt(answer) <= max) {
            return Integer.parseInt(answer);
        }
        System.out.println("Your answer was to high, or too small!");
        return numberIsHigherThanAndLowerThan(min, max);
    }
}
