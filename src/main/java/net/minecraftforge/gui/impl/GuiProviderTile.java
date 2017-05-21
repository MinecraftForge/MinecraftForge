package net.minecraftforge.gui.impl;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.gui.GuiProvider;

import javax.annotation.Nullable;

public abstract class GuiProviderTile extends GuiProvider {

    public BlockPos position;
    public EnumFacing side;

    public GuiProviderTile(){ }
    public GuiProviderTile(TileEntity tile, @Nullable EnumFacing side)
    {
        super(tile);
        this.position = tile.getPos();
        this.side = side;
    }

    @Override
    public GuiProvider deserialize(ByteBuf buffer, World world, EntityPlayer player)
    {
        this.position = BlockPos.fromLong(buffer.readLong());
        this.side = buffer.isReadable() ? EnumFacing.getFront(buffer.readInt()) : null;
        this.owner = world.getTileEntity(position);
        return this;
    }

    @Override
    public void serialize(ByteBuf buffer, Object... extras)
    {
        buffer.writeLong(((TileEntity) owner).getPos().toLong());
        if(side != null) buffer.writeInt(side.getIndex());
    }
}
