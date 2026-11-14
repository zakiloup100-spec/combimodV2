package com.example.combimod.core;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;

/** Hachages stables : mêmes entrées, mêmes identifiants et mêmes résultats à chaque lancement. */
public final class Ids {

    private Ids() {}

    private static byte[] md5(String s) {
        try {
            return MessageDigest.getInstance("MD5").digest(s.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /** 12 caractères hexadécimaux. */
    public static String hash(String s) {
        return HexFormat.of().formatHex(md5(s), 0, 6);
    }

    public static long seed(String s) {
        byte[] d = md5(s);
        long v = 0;
        for (int i = 0; i < 8; i++) v = (v << 8) | (d[i] & 0xff);
        return v;
    }
}
