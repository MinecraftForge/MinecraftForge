/*
 * Forge Mod Loader
 * Copyright (c) 2012-2013 cpw.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     cpw - implementation
 */

package net.minecraftforge.fml.common;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.cert.Certificate;

public class CertificateHelper {

    private static final String HEXES = "0123456789abcdef";

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
            return null;
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
            return null;
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
