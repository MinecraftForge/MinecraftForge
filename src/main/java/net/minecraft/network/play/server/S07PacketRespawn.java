package net.minecraft.network.play.server;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;

public class S07PacketRespawn extends Packet
{
    private int field_149088_a;
    private EnumDifficulty field_149086_b;
    private WorldSettings.GameType field_149087_c;
    private WorldType field_149085_d;
    private static final String __OBFID = "CL_00001322";

    public S07PacketRespawn() {}

    public S07PacketRespawn(int p_i45213_1_, EnumDifficulty p_i45213_2_, WorldType p_i45213_3_, WorldSettings.GameType p_i45213_4_)
    {
        this.field_149088_a = p_i45213_1_;
        this.field_149086_b = p_i45213_2_;
        this.field_149087_c = p_i45213_4_;
        this.field_149085_d = p_i45213_3_;
    }

    public void func_148833_a(INetHandlerPlayClient p_149084_1_)
    {
        p_149084_1_.func_147280_a(this);
    }

    public void func_148837_a(PacketBuffer p_148837_1_) throws IOException
    {
        this.field_149088_a = p_148837_1_.readInt();
        this.field_149086_b = EnumDifficulty.func_151523_a(p_148837_1_.readUnsignedByte());
        this.field_149087_c = WorldSettings.GameType.getByID(p_148837_1_.readUnsignedByte());
        this.field_149085_d = WorldType.parseWorldType(p_148837_1_.func_150789_c(16));

        if (this.field_149085_d == null)
        {
            this.field_149085_d = WorldType.DEFAULT;
        }
    }

    public void func_148840_b(PacketBuffer p_148840_1_) throws IOException
    {
        p_148840_1_.writeInt(this.field_149088_a);
        p_148840_1_.writeByte(this.field_149086_b.func_151525_a());
        p_148840_1_.writeByte(this.field_149087_c.getID());
        p_148840_1_.func_150785_a(this.field_149085_d.getWorldTypeName());
    }

    @SideOnly(Side.CLIENT)
    public int func_149082_c()
    {
        return this.field_149088_a;
    }

    @SideOnly(Side.CLIENT)
    public EnumDifficulty func_149081_d()
    {
        return this.field_149086_b;
    }

    @SideOnly(Side.CLIENT)
    public WorldSettings.GameType func_149083_e()
    {
        return this.field_149087_c;
    }

    @SideOnly(Side.CLIENT)
    public WorldType func_149080_f()
    {
        return this.field_149085_d;
    }

    public void func_148833_a(INetHandler p_148833_1_)
    {
        this.func_148833_a((INetHandlerPlayClient)p_148833_1_);
    }
}