package net.minecraftforge.gui.capability.impl;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.gui.capability.IGuiProvider;

import javax.annotation.Nullable;

public class GuiProviderBlank implements IGuiProvider<Object>
{

    public GuiProviderBlank() { }

    @Override
    public void deserialize(ByteBuf buffer) { }

    @Override
    public void serialize(ByteBuf buffer) { }

    @Override
    public Object getOwner(EntityPlayer player, World world, @Nullable EnumHand hand)
    {
        return null;
    }

    @Override
    public Object getClientGuiElement(EntityPlayer player, World world, Object owner)
    {
        return null;
    }

    @Override
    public Container getServerGuiElement(EntityPlayer player, World world, Object owner)
    {
        return null;
    }
}
