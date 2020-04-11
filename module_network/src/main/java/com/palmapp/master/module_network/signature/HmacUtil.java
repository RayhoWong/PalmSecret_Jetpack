package com.palmapp.master.module_network.signature;

import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by dengyuanting on 16-7-5.
 */
public class HmacUtil {
    public static byte[] hmacSha256(final byte[] key, final byte[] valueToDigest) {
        try {
            return getHmacSha256(key).doFinal(valueToDigest);
        } catch (final IllegalStateException e) {
            // cannot happen
            throw new IllegalArgumentException(e);
        }
    }

    public static Mac getHmacSha256(final byte[] key) {
        return getInitializedMac(HmacAlgorithms.HMAC_SHA_256, key);
    }

    public static Mac getInitializedMac(final HmacAlgorithms algorithm, final byte[] key) {
        return getInitializedMac(algorithm.toString(), key);
    }

    public static Mac getInitializedMac(final String algorithm, final byte[] key) {

        if (key == null) {
            throw new IllegalArgumentException("Null key");
        }

        try {
            final SecretKeySpec keySpec = new SecretKeySpec(key, algorithm);
            final Mac mac = Mac.getInstance(algorithm);
            mac.init(keySpec);
            return mac;
        } catch (final NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        } catch (final InvalidKeyException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static byte[] hmacSha256(String key, String valueToDigest) {
        return hmacSha256(getBytesUtf8(key), getBytesUtf8(valueToDigest));
    }

    private static byte[] getBytes(String string, Charset charset) {
        return string == null?null:string.getBytes(charset);
    }

    public static byte[] getBytesUtf8(String string) {
        return getBytes(string, Charset.forName("UTF-8"));
    }
}
