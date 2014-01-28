package net.minecraft.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S33PacketUpdateSign;

public class TileEntitySign extends TileEntity
{
    public String[] field_145915_a = new String[] {"", "", "", ""};
    public int field_145918_i = -1;
    private boolean field_145916_j = true;
    private EntityPlayer field_145917_k;
    private static final String __OBFID = "CL_00000363";

    public void func_145841_b(NBTTagCompound p_145841_1_)
    {
        super.func_145841_b(p_145841_1_);
        p_145841_1_.setString("Text1", this.field_145915_a[0]);
        p_145841_1_.setString("Text2", this.field_145915_a[1]);
        p_145841_1_.setString("Text3", this.field_145915_a[2]);
        p_145841_1_.setString("Text4", this.field_145915_a[3]);
    }

    public void func_145839_a(NBTTagCompound p_145839_1_)
    {
        this.field_145916_j = false;
        super.func_145839_a(p_145839_1_);

        for (int i = 0; i < 4; ++i)
        {
            this.field_145915_a[i] = p_145839_1_.getString("Text" + (i + 1));

            if (this.field_145915_a[i].length() > 15)
            {
                this.field_145915_a[i] = this.field_145915_a[i].substring(0, 15);
            }
        }
    }

    public Packet func_145844_m()
    {
        String[] astring = new String[4];
        System.arraycopy(this.field_145915_a, 0, astring, 0, 4);
        return new S33PacketUpdateSign(this.field_145851_c, this.field_145848_d, this.field_145849_e, astring);
    }

    public boolean func_145914_a()
    {
        return this.field_145916_j;
    }

    @SideOnly(Side.CLIENT)
    public void func_145913_a(boolean p_145913_1_)
    {
        this.field_145916_j = p_145913_1_;

        if (!p_145913_1_)
        {
            this.field_145917_k = null;
        }
    }

    public void func_145912_a(EntityPlayer p_145912_1_)
    {
        this.field_145917_k = p_145912_1_;
    }

    public EntityPlayer func_145911_b()
    {
        return this.field_145917_k;
    }
}