package com.example.test.utils;

import java.security.SecureRandom;

public class RandomStringUtil {
    private static final  SecureRandom random = new SecureRandom();
    private static final  String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    public static String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder();
        int charLength = characters.length();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(charLength);
            sb.append(characters.charAt(index));
        }
        return sb.toString();
    }

}
