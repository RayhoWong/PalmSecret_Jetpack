package com.palmapp.master.module_network.signature;

import android.text.TextUtils;
import com.palmapp.master.baselib.utils.LogUtil;

/**
 * Created by dengyuanting on 16-7-5.
 */
public class Signature {
    private static final String METHOD_GET = "GET";
    private static final String METHOD_POST = "POST";
    private static final char DELIMITER = '\n';

    private static String sign(String secret, String out) {
        LogUtil.d("Signature", out);
        byte[] digest = HmacUtil.hmacSha256(secret, out);
        String signature = Base64.encodeBase64URLSafeString(digest);
        return signature;
    }

    public static String getSign(String method, String queryUri, String secret, String queryString, String payload) {
        StringBuilder valueToDigest = new StringBuilder();
        valueToDigest.append(method)
                .append(DELIMITER)
                .append(queryUri)
                .append(DELIMITER)
                .append(queryString)
                .append(DELIMITER)
                .append(payload);
        return sign(secret, valueToDigest.toString());
    }
}
