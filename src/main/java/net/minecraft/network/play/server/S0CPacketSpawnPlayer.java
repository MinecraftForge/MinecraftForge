package net.minecraft.network.play.server;

import com.mojang.authlib.GameProfile;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.IOException;
import java.util.List;
import net.minecraft.entity.DataWatcher;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.MathHelper;

public class S0CPacketSpawnPlayer extends Packet
{
    private int field_148957_a;
    private GameProfile field_148955_b;
    private int field_148956_c;
    private int field_148953_d;
    private int field_148954_e;
    private byte field_148951_f;
    private byte field_148952_g;
    private int field_148959_h;
    private DataWatcher field_148960_i;
    private List field_148958_j;
    private static final String __OBFID = "CL_00001281";

    public S0CPacketSpawnPlayer() {}

    public S0CPacketSpawnPlayer(EntityPlayer p_i45171_1_)
    {
        this.field_148957_a = p_i45171_1_.func_145782_y();
        this.field_148955_b = p_i45171_1_.func_146103_bH();
        this.field_148956_c = MathHelper.floor_double(p_i45171_1_.posX * 32.0D);
        this.field_148953_d = MathHelper.floor_double(p_i45171_1_.posY * 32.0D);
        this.field_148954_e = MathHelper.floor_double(p_i45171_1_.posZ * 32.0D);
        this.field_148951_f = (byte)((int)(p_i45171_1_.rotationYaw * 256.0F / 360.0F));
        this.field_148952_g = (byte)((int)(p_i45171_1_.rotationPitch * 256.0F / 360.0F));
        ItemStack itemstack = p_i45171_1_.inventory.getCurrentItem();
        this.field_148959_h = itemstack == null ? 0 : Item.func_150891_b(itemstack.getItem());
        this.field_148960_i = p_i45171_1_.getDataWatcher();
    }

    public void func_148837_a(PacketBuffer p_148837_1_) throws IOException
    {
        this.field_148957_a = p_148837_1_.func_150792_a();
        this.field_148955_b = new GameProfile(p_148837_1_.func_150789_c(36), p_148837_1_.func_150789_c(16));
        this.field_148956_c = p_148837_1_.readInt();
        this.field_148953_d = p_148837_1_.readInt();
        this.field_148954_e = p_148837_1_.readInt();
        this.field_148951_f = p_148837_1_.readByte();
        this.field_148952_g = p_148837_1_.readByte();
        this.field_148959_h = p_148837_1_.readShort();
        this.field_148958_j = DataWatcher.func_151508_b(p_148837_1_);
    }

    public void func_148840_b(PacketBuffer p_148840_1_) throws IOException
    {
        p_148840_1_.func_150787_b(this.field_148957_a);
        p_148840_1_.func_150785_a(this.field_148955_b.getId());
        p_148840_1_.func_150785_a(this.field_148955_b.getName());
        p_148840_1_.writeInt(this.field_148956_c);
        p_148840_1_.writeInt(this.field_148953_d);
        p_148840_1_.writeInt(this.field_148954_e);
        p_148840_1_.writeByte(this.field_148951_f);
        p_148840_1_.writeByte(this.field_148952_g);
        p_148840_1_.writeShort(this.field_148959_h);
        this.field_148960_i.func_151509_a(p_148840_1_);
    }

    public void func_148833_a(INetHandlerPlayClient p_148950_1_)
    {
        p_148950_1_.func_147237_a(this);
    }

    @SideOnly(Side.CLIENT)
    public List func_148944_c()
    {
        if (this.field_148958_j == null)
        {
            this.field_148958_j = this.field_148960_i.getAllWatched();
        }

        return this.field_148958_j;
    }

    public String func_148835_b()
    {
        return String.format("id=%d, gameProfile=\'%s\', x=%.2f, y=%.2f, z=%.2f, carried=%d", new Object[] {Integer.valueOf(this.field_148957_a), this.field_148955_b, Float.valueOf((float)this.field_148956_c / 32.0F), Float.valueOf((float)this.field_148953_d / 32.0F), Float.valueOf((float)this.field_148954_e / 32.0F), Integer.valueOf(this.field_148959_h)});
    }

    @SideOnly(Side.CLIENT)
    public int func_148943_d()
    {
        return this.field_148957_a;
    }

    @SideOnly(Side.CLIENT)
    public GameProfile func_148948_e()
    {
        return this.field_148955_b;
    }

    @SideOnly(Side.CLIENT)
    public int func_148942_f()
    {
        return this.field_148956_c;
    }

    @SideOnly(Side.CLIENT)
    public int func_148949_g()
    {
        return this.field_148953_d;
    }

    @SideOnly(Side.CLIENT)
    public int func_148946_h()
    {
        return this.field_148954_e;
    }

    @SideOnly(Side.CLIENT)
    public byte func_148941_i()
    {
        return this.field_148951_f;
    }

    @SideOnly(Side.CLIENT)
    public byte func_148945_j()
    {
        return this.field_148952_g;
    }

    @SideOnly(Side.CLIENT)
    public int func_148947_k()
    {
        return this.field_148959_h;
    }

    public void func_148833_a(INetHandler p_148833_1_)
    {
        this.func_148833_a((INetHandlerPlayClient)p_148833_1_);
    }
}