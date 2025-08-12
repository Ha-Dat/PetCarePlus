package org.example.petcareplus.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;


public class VNPayUtils {

    public static String hmacSHA512(String key, String data) throws Exception {
        Mac hmac512 = Mac.getInstance("HmacSHA512");
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA512");
        hmac512.init(secretKey);
        byte[] bytes = hmac512.doFinal(data.getBytes("UTF-8"));
        StringBuilder hash = new StringBuilder();
        for (byte b : bytes) {
            hash.append(String.format("%02x", b & 0xff));
        }
        return hash.toString();
    }
}
