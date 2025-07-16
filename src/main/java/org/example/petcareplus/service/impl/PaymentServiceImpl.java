package org.example.petcareplus.service.impl;

import org.example.petcareplus.service.PaymentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Value("${vnpay.url}")
    private String vnpPayUrl;

    @Value("${vnpay.returnUrl}")
    private String vnpReturnUrl;

    @Value("${vnpay.tmnCode}")
    private String vnpTmnCode;

    @Value("${vnpay.secretKey}")
    private String secretKey;

    @Value("${vnpay.version}")
    private String vnpVersion;

    public String createPaymentUrl(BigDecimal amount, String orderInfo) {
        try {
            String vnpIpAddr = "127.0.0.1"; // IP của client, có thể lấy từ request
            String vnpTxnRef = generateTransactionRef();

            Map<String, String> vnpParams = new HashMap<>();
            vnpParams.put("vnp_Version", vnpVersion);
            vnpParams.put("vnp_Command", "pay");
            vnpParams.put("vnp_TmnCode", vnpTmnCode);
            vnpParams.put("vnp_Amount", String.valueOf(amount.multiply(BigDecimal.valueOf(100)))); // VNPay yêu cầu số tiền nhân 100
            vnpParams.put("vnp_CurrCode", "VND");
            vnpParams.put("vnp_BankCode", "");
            vnpParams.put("vnp_TxnRef", vnpTxnRef);
            vnpParams.put("vnp_OrderInfo", orderInfo);
            vnpParams.put("vnp_OrderType", "other");
            vnpParams.put("vnp_Locale", "vn");
            vnpParams.put("vnp_ReturnUrl", vnpReturnUrl);
            vnpParams.put("vnp_IpAddr", vnpIpAddr);
            vnpParams.put("vnp_CreateDate", getCurrentDate());

            // Sắp xếp tham số theo thứ tự alphabet
            List<String> fieldNames = new ArrayList<>(vnpParams.keySet());
            Collections.sort(fieldNames);

            StringBuilder hashData = new StringBuilder();
            StringBuilder query = new StringBuilder();

            for (String fieldName : fieldNames) {
                String fieldValue = vnpParams.get(fieldName);
                if (fieldValue != null && !fieldValue.isEmpty()) {
                    // Build hash data
                    hashData.append(fieldName).append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    // Build query
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString())).append('=')
                            .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));

                    if (fieldNames.indexOf(fieldName) < fieldNames.size() - 1) {
                        hashData.append('&');
                        query.append('&');
                    }
                }
            }

            String queryUrl = query.toString();
            String vnpSecureHash = hmacSHA512(secretKey, hashData.toString());
            queryUrl += "&vnp_SecureHash=" + vnpSecureHash;

            return vnpPayUrl + "?" + queryUrl;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Error encoding VNPay parameters", e);
        }
    }

    private String generateTransactionRef() {
        return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) +
                String.format("%04d", new Random().nextInt(10000));
    }

    private String getCurrentDate() {
        return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
    }

    private String hmacSHA512(String key, String data) {
        try {
            return org.apache.commons.codec.digest.HmacUtils.hmacSha512Hex(key, data);
        } catch (Exception e) {
            throw new RuntimeException("Error generating HMAC-SHA512 hash", e);
        }
    }

}
