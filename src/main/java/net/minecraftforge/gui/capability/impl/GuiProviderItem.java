package net.minecraftforge.gui.capability.impl;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.gui.capability.IGuiProvider;

import javax.annotation.Nullable;

public abstract class GuiProviderItem implements IGuiProvider<ItemStack>
{
    private ItemStack stack;

    public GuiProviderItem() { }
    public GuiProviderItem(ItemStack stack)
    {
        this.stack = stack;
    }

    @Override
    public void deserialize(ByteBuf buffer)
    {
        stack = ByteBufUtils.readItemStack(buffer);
    }

    @Override
    public void serialize(ByteBuf buffer)
    {
        ByteBufUtils.writeItemStack(buffer, stack);
    }

    @Override
    public ItemStack getOwner(EntityPlayer player, World world, @Nullable EnumHand hand)
    {
        return player.getHeldItem(hand);
    }
}
