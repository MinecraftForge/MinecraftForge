package net.minecraftforge.gui.capability.impl;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.gui.capability.IGuiProvider;

import javax.annotation.Nullable;

public class GuiProviderBlank implements IGuiProvider
{
    @Override public void deserialize(ByteBuf buffer)
    {

    }

    @Override public void serialize(ByteBuf buffer)
    {

    }

    @Override public Object getOwner(EntityPlayer player, World world)
    {
        return null;
    }

    @Override public Object getClientGuiElement(EntityPlayer player, World world, @Nullable Object owner)
    {
        return null;
    }

    @Override public Object getServerGuiElement(EntityPlayer player, World world, @Nullable Object owner)
    {
        return null;
    }
}
