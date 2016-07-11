package net.minecraftforge.gui.impl;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.gui.IGuiProvider;

import javax.annotation.Nullable;

public abstract class GuiProviderTile extends GuiProviderBase<TileEntity> {

    public BlockPos position;
    public EnumFacing side;

    public GuiProviderTile(){ }
    public GuiProviderTile(TileEntity t, @Nullable EnumFacing side)
    {
        super(t);
        this.position = t.getPos();
        this.side = side;
    }

    @Override
    public IGuiProvider deserialize(ByteBuf buffer)
    {
        this.position = BlockPos.fromLong(buffer.readLong());
        this.side = buffer.isReadable() ? EnumFacing.getFront(buffer.readInt()) : null;
        return this;
    }

    @Override
    public void serialize(ByteBuf buffer, Object... extras)
    {
        buffer.writeLong(owner.getPos().toLong());
        if(side != null) buffer.writeInt(side.getIndex());
    }

    @Override
    public TileEntity getOwner(World world, EntityPlayer player)
    {
        if (world == null) return null;
        if (owner == null)
        {
            owner = world.getTileEntity(this.position);
            return owner;
        }
        else
            return owner;
    }
}
