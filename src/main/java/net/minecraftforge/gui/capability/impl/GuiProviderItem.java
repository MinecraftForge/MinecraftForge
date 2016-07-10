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
        hand = buffer.readInt() == 0 ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
    }

    @Override
    public void serialize(ByteBuf buffer)
    {
        ByteBufUtils.writeItemStack(buffer, stack);
        buffer.writeInt(hand == EnumHand.MAIN_HAND ? 0 : 1);
    }

    @Override
    public ItemStack getOwner(EntityPlayer player, World world)
    {
        return player.getHeldItem(hand);
    }
}
