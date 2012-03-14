package net.minecraft.src;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5String
{
    private String field_27370_a;

    public MD5String(String par1Str)
    {
        this.field_27370_a = par1Str;
    }

    /**
     * Gets the MD5 string
     */
    public String getMD5String(String par1Str)
    {
        try
        {
            String var2 = this.field_27370_a + par1Str;
            MessageDigest var3 = MessageDigest.getInstance("MD5");
            var3.update(var2.getBytes(), 0, var2.length());
            return (new BigInteger(1, var3.digest())).toString(16);
        }
        catch (NoSuchAlgorithmException var4)
        {
            throw new RuntimeException(var4);
        }
    }
}
