package net.minecraftforge.gui.capability.impl;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.gui.capability.IGuiProvider;

public abstract class GuiProviderItem implements IGuiProvider<ItemStack>
{
    private ItemStack stack;
    private EnumHand hand;

    public GuiProviderItem(ItemStack stack, EnumHand hand)
    {
        this.stack = stack;
        this.hand = hand;
    }

    @Override
    public void deserialize(ByteBuf buffer)
    {
        stack = ByteBufUtils.readItemStack(buffer);
        hand = buffer.readBoolean() ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
    }

    @Override
    public void serialize(ByteBuf buffer)
    {
        ByteBufUtils.writeItemStack(buffer, stack);
        buffer.writeBoolean(hand == EnumHand.MAIN_HAND);
    }

    @Override
    public ItemStack getOwner(EntityPlayer player, World world)
    {
        return player.getHeldItem(hand);
    }
}
