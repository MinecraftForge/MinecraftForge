/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.util;

import com.google.common.collect.ImmutableList;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.cert.Certificate;

public class CertificateHelper {

    private static final String HEXES = "0123456789abcdef";

    public static ImmutableList<String> getFingerprints(Certificate[] certificates)
    {
        int len = 0;
        if (certificates != null)
        {
            len = certificates.length;
        }
        ImmutableList.Builder<String> certBuilder = ImmutableList.builder();
        for (int i = 0; i < len; i++)
        {
            certBuilder.add(CertificateHelper.getFingerprint(certificates[i]));
        }
        return certBuilder.build();
    }

    public static String getFingerprint(Certificate certificate)
    {
        if (certificate == null)
        {
            return "NO VALID CERTIFICATE FOUND";
        }
        try
        {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] der = certificate.getEncoded();
            md.update(der);
            byte[] digest = md.digest();
            return hexify(digest);
        }
        catch (Exception e)
        {
            return "CERTIFICATE FINGERPRINT EXCEPTION";
        }
    }

    public static String getFingerprint(ByteBuffer buffer)
    {
        try
        {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.update(buffer);
            byte[] chksum = digest.digest();
            return hexify(chksum);
        }
        catch (Exception e)
        {
            return "CERTIFICATE FINGERPRINT EXCEPTION";
        }
    }

    private static String hexify(byte[] chksum)
    {
        final StringBuilder hex = new StringBuilder( 2 * chksum.length );
        for ( final byte b : chksum ) {
          hex.append(HEXES.charAt((b & 0xF0) >> 4))
             .append(HEXES.charAt((b & 0x0F)));
        }
        return hex.toString();
    }

}
