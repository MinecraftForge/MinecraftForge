package net.minecraftforge.gui.impl;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.gui.GuiProvider;

public abstract class GuiProviderEntity extends GuiProvider
{

    private int entityID;
    public GuiProviderEntity() { }

    public GuiProviderEntity(Entity in)
    {
        super(in);
        this.entityID = in.getEntityId();
    }

    @Override
    public GuiProvider deserialize(ByteBuf buffer, World world, EntityPlayer player)
    {
        entityID = buffer.readInt();
        owner = world.getEntityByID(entityID);
        return this;
    }

    @Override
    public void serialize(ByteBuf buffer, Object... extras)
    {
        buffer.writeInt(((Entity) owner).getEntityId());
    }
}
