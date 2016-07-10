package net.minecraftforge.gui.capability;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public interface IGuiProvider
{
    void deserialize(ByteBuf buffer);
    void serialize(ByteBuf buffer);

    Object getOwner(EntityPlayer playerSP, World world);

    @Nullable Object getClientGuiElement(EntityPlayer player, World world, @Nullable Object owner);

    @Nullable Object getServerGuiElement(EntityPlayer player, World world, @Nullable Object owner);
}
