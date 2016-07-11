package net.minecraftforge.gui.capability.impl;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.gui.capability.IGuiProvider;

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
        this.side = EnumFacing.getFront(buffer.readInt());
        return this;
    }

    @Override
    public void serialize(ByteBuf buffer, Object... extras)
    {
        buffer.writeLong(owner.getPos().toLong());
        if(extras[0] != null && extras[0] instanceof EnumFacing) buffer.writeInt(((EnumFacing) extras[0]).getIndex());
        else buffer.writeInt(-1);
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
