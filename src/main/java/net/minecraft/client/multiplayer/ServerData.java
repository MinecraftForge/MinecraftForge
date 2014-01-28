package net.minecraft.client.multiplayer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;

@SideOnly(Side.CLIENT)
public class ServerData
{
    public String serverName;
    public String serverIP;
    // JAVADOC FIELD $$ field_78846_c
    public String populationInfo;
    // JAVADOC FIELD $$ field_78843_d
    public String serverMOTD;
    // JAVADOC FIELD $$ field_78844_e
    public long pingToServer;
    public int field_82821_f = 4;
    // JAVADOC FIELD $$ field_82822_g
    public String gameVersion = "1.7.2";
    public boolean field_78841_f;
    public String field_147412_i;
    private boolean field_78842_g = true;
    private boolean acceptsTextures;
    // JAVADOC FIELD $$ field_82823_k
    private boolean hideAddress;
    private String field_147411_m;
    private static final String __OBFID = "CL_00000890";

    public ServerData(String par1Str, String par2Str)
    {
        this.serverName = par1Str;
        this.serverIP = par2Str;
    }

    // JAVADOC METHOD $$ func_78836_a
    public NBTTagCompound getNBTCompound()
    {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        nbttagcompound.setString("name", this.serverName);
        nbttagcompound.setString("ip", this.serverIP);
        nbttagcompound.setBoolean("hideAddress", this.hideAddress);

        if (this.field_147411_m != null)
        {
            nbttagcompound.setString("icon", this.field_147411_m);
        }

        if (!this.field_78842_g)
        {
            nbttagcompound.setBoolean("acceptTextures", this.acceptsTextures);
        }

        return nbttagcompound;
    }

    public boolean func_147408_b()
    {
        return this.acceptsTextures;
    }

    public boolean func_147410_c()
    {
        return this.field_78842_g;
    }

    public void setAcceptsTextures(boolean par1)
    {
        this.acceptsTextures = par1;
        this.field_78842_g = false;
    }

    public boolean isHidingAddress()
    {
        return this.hideAddress;
    }

    public void setHideAddress(boolean par1)
    {
        this.hideAddress = par1;
    }

    // JAVADOC METHOD $$ func_78837_a
    public static ServerData getServerDataFromNBTCompound(NBTTagCompound par0NBTTagCompound)
    {
        ServerData serverdata = new ServerData(par0NBTTagCompound.getString("name"), par0NBTTagCompound.getString("ip"));
        serverdata.hideAddress = par0NBTTagCompound.getBoolean("hideAddress");

        if (par0NBTTagCompound.func_150297_b("icon", 8))
        {
            serverdata.func_147407_a(par0NBTTagCompound.getString("icon"));
        }

        if (par0NBTTagCompound.func_150297_b("acceptTextures", 99))
        {
            serverdata.setAcceptsTextures(par0NBTTagCompound.getBoolean("acceptTextures"));
        }

        return serverdata;
    }

    public String func_147409_e()
    {
        return this.field_147411_m;
    }

    public void func_147407_a(String p_147407_1_)
    {
        this.field_147411_m = p_147407_1_;
    }
}