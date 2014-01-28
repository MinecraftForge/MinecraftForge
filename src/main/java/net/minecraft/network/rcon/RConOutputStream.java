package net.minecraft.network.rcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@SideOnly(Side.SERVER)
public class RConOutputStream
{
    // JAVADOC FIELD $$ field_72674_a
    private ByteArrayOutputStream byteArrayOutput;
    // JAVADOC FIELD $$ field_72673_b
    private DataOutputStream output;
    private static final String __OBFID = "CL_00001798";

    public RConOutputStream(int par1)
    {
        this.byteArrayOutput = new ByteArrayOutputStream(par1);
        this.output = new DataOutputStream(this.byteArrayOutput);
    }

    // JAVADOC METHOD $$ func_72670_a
    public void writeByteArray(byte[] par1ArrayOfByte) throws IOException
    {
        this.output.write(par1ArrayOfByte, 0, par1ArrayOfByte.length);
    }

    // JAVADOC METHOD $$ func_72671_a
    public void writeString(String par1Str) throws IOException
    {
        this.output.writeBytes(par1Str);
        this.output.write(0);
    }

    // JAVADOC METHOD $$ func_72667_a
    public void writeInt(int par1) throws IOException
    {
        this.output.write(par1);
    }

    // JAVADOC METHOD $$ func_72668_a
    public void writeShort(short par1) throws IOException
    {
        this.output.writeShort(Short.reverseBytes(par1));
    }

    // JAVADOC METHOD $$ func_72672_a
    public byte[] toByteArray()
    {
        return this.byteArrayOutput.toByteArray();
    }

    // JAVADOC METHOD $$ func_72669_b
    public void reset()
    {
        this.byteArrayOutput.reset();
    }
}