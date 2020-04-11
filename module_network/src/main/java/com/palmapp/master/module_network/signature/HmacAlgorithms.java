package com.palmapp.master.module_network.signature;

/**
 * Created by dengyuanting on 16-7-6.
 */
public enum HmacAlgorithms {
    HMAC_SHA_256("HmacSHA256");

    private final String algorithm;

    HmacAlgorithms(final String algorithm) {
        this.algorithm = algorithm;
    }

    @Override
    public String toString() {
        return algorithm;
    }
}
