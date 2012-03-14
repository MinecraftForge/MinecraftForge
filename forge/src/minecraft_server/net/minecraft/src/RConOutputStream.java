package net.minecraft.src;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RConOutputStream
{
    /** Output stream */
    private ByteArrayOutputStream byteArrayOutput;

    /** ByteArrayOutputStream wrapper */
    private DataOutputStream output;

    public RConOutputStream(int par1)
    {
        this.byteArrayOutput = new ByteArrayOutputStream(par1);
        this.output = new DataOutputStream(this.byteArrayOutput);
    }

    /**
     * Writes the given byte array to the output stream
     */
    public void writeByteArray(byte[] par1ArrayOfByte) throws IOException
    {
        this.output.write(par1ArrayOfByte, 0, par1ArrayOfByte.length);
    }

    /**
     * Writes the given String to the output stream
     */
    public void writeString(String par1Str) throws IOException
    {
        this.output.writeBytes(par1Str);
        this.output.write(0);
    }

    /**
     * Writes the given int to the output stream
     */
    public void writeInt(int par1) throws IOException
    {
        this.output.write(par1);
    }

    /**
     * Writes the given short to the output stream
     */
    public void writeShort(short par1) throws IOException
    {
        this.output.writeShort(Short.reverseBytes(par1));
    }

    /**
     * Returns the contents of the output stream as a byte array
     */
    public byte[] toByteArray()
    {
        return this.byteArrayOutput.toByteArray();
    }

    /**
     * Resets
     */
    public void reset()
    {
        this.byteArrayOutput.reset();
    }
}
