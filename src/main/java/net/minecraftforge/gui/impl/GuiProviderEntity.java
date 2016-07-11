package net.minecraftforge.gui.impl;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.gui.IGuiProvider;

public abstract class GuiProviderEntity extends GuiProviderBase<Entity>
{

    private int entityID;
    public GuiProviderEntity() { }

    public GuiProviderEntity(Entity in)
    {
        super(in);
        this.entityID = in.getEntityId();
    }

    @Override
    public IGuiProvider deserialize(ByteBuf buffer)
    {
        entityID = buffer.readInt();
        return this;
    }

    @Override
    public void serialize(ByteBuf buffer, Object... extras)
    {
        buffer.writeInt(owner.getEntityId());
    }

    @Override
    public Entity getOwner(World world, EntityPlayer player)
    {
        if (world == null) return null;
        if (owner != null) return owner;
        owner = world.getEntityByID(entityID);
        return owner;
    }
}
