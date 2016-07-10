package net.minecraftforge.gui.capability.impl;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.gui.capability.IGuiProvider;

public abstract class GuiProviderTile implements IGuiProvider
{
    private BlockPos position;
    private EnumFacing side;

    public GuiProviderTile(TileEntity tile, EnumFacing side)
    {
        this.position = tile.getPos();
        this.side = side;
    }

    @Override public void deserialize(ByteBuf buffer)
    {
        this.position = BlockPos.fromLong(buffer.readLong());
        this.side = EnumFacing.getFront(buffer.readInt());
    }

    @Override public void serialize(ByteBuf buffer)
    {
        buffer.writeLong(position.toLong());
        buffer.writeInt(side.getIndex());
    }

    @Override public Object getOwner(EntityPlayer player, World world)
    {
        if(world == null) return null;
        return world.getTileEntity(this.position);
    }
}
