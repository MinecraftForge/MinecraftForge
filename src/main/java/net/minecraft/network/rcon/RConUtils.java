package net.minecraft.network.rcon;

import com.google.common.base.Charsets;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.SERVER)
public class RConUtils
{
    // JAVADOC FIELD $$ field_72666_a
    public static char[] hexDigits = new char[] {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private static final String __OBFID = "CL_00001799";

    // JAVADOC METHOD $$ func_72661_a
    public static String getBytesAsString(byte[] par0ArrayOfByte, int par1, int par2)
    {
        int k = par2 - 1;
        int l;

        for (l = par1 > k ? k : par1; 0 != par0ArrayOfByte[l] && l < k; ++l)
        {
            ;
        }

        return new String(par0ArrayOfByte, par1, l - par1, Charsets.UTF_8);
    }

    // JAVADOC METHOD $$ func_72662_b
    public static int getRemainingBytesAsLEInt(byte[] par0ArrayOfByte, int par1)
    {
        // JAVADOC METHOD $$ func_72665_b
        return getBytesAsLEInt(par0ArrayOfByte, par1, par0ArrayOfByte.length);
    }

    // JAVADOC METHOD $$ func_72665_b
    public static int getBytesAsLEInt(byte[] par0ArrayOfByte, int par1, int par2)
    {
        return 0 > par2 - par1 - 4 ? 0 : par0ArrayOfByte[par1 + 3] << 24 | (par0ArrayOfByte[par1 + 2] & 255) << 16 | (par0ArrayOfByte[par1 + 1] & 255) << 8 | par0ArrayOfByte[par1] & 255;
    }

    // JAVADOC METHOD $$ func_72664_c
    public static int getBytesAsBEint(byte[] par0ArrayOfByte, int par1, int par2)
    {
        return 0 > par2 - par1 - 4 ? 0 : par0ArrayOfByte[par1] << 24 | (par0ArrayOfByte[par1 + 1] & 255) << 16 | (par0ArrayOfByte[par1 + 2] & 255) << 8 | par0ArrayOfByte[par1 + 3] & 255;
    }

    // JAVADOC METHOD $$ func_72663_a
    public static String getByteAsHexString(byte par0)
    {
        return "" + hexDigits[(par0 & 240) >>> 4] + hexDigits[par0 & 15];
    }
}