package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet10Flying extends Packet
{
    /** The player's X position. */
    public double xPosition;

    /** The player's Y position. */
    public double yPosition;

    /** The player's Z position. */
    public double zPosition;

    /** The player's stance. (boundingBox.minY) */
    public double stance;

    /** The player's yaw rotation. */
    public float yaw;

    /** The player's pitch rotation. */
    public float pitch;

    /** True if the client is on the ground. */
    public boolean onGround;

    /** Boolean set to true if the player is moving. */
    public boolean moving;

    /** Boolean set to true if the player is rotating. */
    public boolean rotating;

    public Packet10Flying() {}

    public Packet10Flying(boolean par1)
    {
        this.onGround = par1;
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(NetHandler par1NetHandler)
    {
        par1NetHandler.handleFlying(this);
    }

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void readPacketData(DataInputStream par1DataInputStream) throws IOException
    {
        this.onGround = par1DataInputStream.read() != 0;
    }

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public void writePacketData(DataOutputStream par1DataOutputStream) throws IOException
    {
        par1DataOutputStream.write(this.onGround ? 1 : 0);
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
        return 1;
    }
}
