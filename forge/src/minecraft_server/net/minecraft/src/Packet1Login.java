package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet1Login extends Packet
{
    /** The protocol version in use. Current version is 2. */
    public int protocolVersion;

    /** The name of the user attempting to login. */
    public String username;
    public WorldType terrainType;

    /** 0 for survival, 1 for creative */
    public int serverMode;
    public int field_48112_e;

    /** The difficulty setting byte. */
    public byte difficultySetting;

    /** Defaults to 128 */
    public byte worldHeight;

    /** The maximum players. */
    public byte maxPlayers;

    public Packet1Login() {}

    public Packet1Login(String par1Str, int par2, WorldType par3WorldType, int par4, int par5, byte par6, byte par7, byte par8)
    {
        this.username = par1Str;
        this.protocolVersion = par2;
        this.terrainType = par3WorldType;
        this.field_48112_e = par5;
        this.difficultySetting = par6;
        this.serverMode = par4;
        this.worldHeight = par7;
        this.maxPlayers = par8;
    }

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void readPacketData(DataInputStream par1DataInputStream) throws IOException
    {
        this.protocolVersion = par1DataInputStream.readInt();
        this.username = readString(par1DataInputStream, 16);
        String var2 = readString(par1DataInputStream, 16);
        this.terrainType = WorldType.parseWorldType(var2);

        if (this.terrainType == null)
        {
            this.terrainType = WorldType.field_48457_b;
        }

        this.serverMode = par1DataInputStream.readInt();
        this.field_48112_e = par1DataInputStream.readInt();
        this.difficultySetting = par1DataInputStream.readByte();
        this.worldHeight = par1DataInputStream.readByte();
        this.maxPlayers = par1DataInputStream.readByte();
    }

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public void writePacketData(DataOutputStream par1DataOutputStream) throws IOException
    {
        par1DataOutputStream.writeInt(this.protocolVersion);
        writeString(this.username, par1DataOutputStream);

        if (this.terrainType == null)
        {
            writeString("", par1DataOutputStream);
        }
        else
        {
            writeString(this.terrainType.func_48449_a(), par1DataOutputStream);
        }

        par1DataOutputStream.writeInt(this.serverMode);
        par1DataOutputStream.writeInt(this.field_48112_e);
        par1DataOutputStream.writeByte(this.difficultySetting);
        par1DataOutputStream.writeByte(this.worldHeight);
        par1DataOutputStream.writeByte(this.maxPlayers);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(NetHandler par1NetHandler)
    {
        par1NetHandler.handleLogin(this);
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
        int var1 = 0;

        if (this.terrainType != null)
        {
            var1 = this.terrainType.func_48449_a().length();
        }

        return 4 + this.username.length() + 4 + 7 + 7 + var1;
    }
}
