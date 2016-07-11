package net.minecraftforge.gui.capability.impl;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.gui.capability.IGuiProvider;

public class GuiProviderBase<T> implements IGuiProvider<T>
{

    /**
     * The owner of this gui provider.
     */
    public T owner;

    public GuiProviderBase()
    {
        this.owner = null;
    }

    public GuiProviderBase(T in)
    {
        this.owner = in;
    }

    @Override
    public IGuiProvider deserialize(ByteBuf buffer) { return null; }

    @Override
    public void serialize(ByteBuf buffer, Object... extras) { }

    @Override
    public T getOwner(World world, EntityPlayer player)
    {
        return owner;
    }

    @Override
    public GuiScreen clientElement(World world, EntityPlayer player)
    {
        return null;
    }

    @Override
    public Container serverElement(World world, EntityPlayer player)
    {
        return null;
    }

    @Override
    public ResourceLocation getGuiIdentifier() {
        return new ResourceLocation("forge:blank_gui_provider");
    }
}
