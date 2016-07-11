package net.minecraftforge.gui.impl;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.gui.GuiProvider;

public abstract class GuiProviderItem extends GuiProvider
{
    protected EnumHand hand;

    public GuiProviderItem() { }
    public GuiProviderItem(ItemStack in)
    {
        super(in);
    }

    @Override
    public GuiProvider deserialize(ByteBuf buffer, World world, EntityPlayer player)
    {
        owner = ByteBufUtils.readItemStack(buffer);
        hand = buffer.readBoolean() ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
        return this;
    }

    @Override
    public void serialize(ByteBuf buffer, Object... extras)
    {
        ByteBufUtils.writeItemStack(buffer, (ItemStack) owner);
        if(extras.length == 1 && extras[0] instanceof EnumHand) buffer.writeBoolean(extras[0] == EnumHand.MAIN_HAND);
    }
}
