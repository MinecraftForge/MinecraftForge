package net.minecraft.network.play.server;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.IChatComponent;

public class S02PacketChat extends Packet
{
    private IChatComponent field_148919_a;
    private boolean field_148918_b;
    private static final String __OBFID = "CL_00001289";

    public S02PacketChat()
    {
        this.field_148918_b = true;
    }

    public S02PacketChat(IChatComponent p_i45179_1_)
    {
        this(p_i45179_1_, true);
    }

    public S02PacketChat(IChatComponent p_i45180_1_, boolean p_i45180_2_)
    {
        this.field_148918_b = true;
        this.field_148919_a = p_i45180_1_;
        this.field_148918_b = p_i45180_2_;
    }

    public void func_148837_a(PacketBuffer p_148837_1_) throws IOException
    {
        this.field_148919_a = IChatComponent.Serializer.func_150699_a(p_148837_1_.func_150789_c(32767));
    }

    public void func_148840_b(PacketBuffer p_148840_1_) throws IOException
    {
        p_148840_1_.func_150785_a(IChatComponent.Serializer.func_150696_a(this.field_148919_a));
    }

    public void func_148833_a(INetHandlerPlayClient p_148917_1_)
    {
        p_148917_1_.func_147251_a(this);
    }

    public String func_148835_b()
    {
        return String.format("message=\'%s\'", new Object[] {this.field_148919_a});
    }

    @SideOnly(Side.CLIENT)
    public IChatComponent func_148915_c()
    {
        return this.field_148919_a;
    }

    public boolean func_148916_d()
    {
        return this.field_148918_b;
    }

    public void func_148833_a(INetHandler p_148833_1_)
    {
        this.func_148833_a((INetHandlerPlayClient)p_148833_1_);
    }
}