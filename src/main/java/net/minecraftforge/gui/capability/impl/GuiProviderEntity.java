package net.minecraftforge.gui.capability.impl;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.gui.capability.IGuiProvider;

import javax.annotation.Nullable;

public abstract class GuiProviderEntity implements IGuiProvider<Entity>
{

    private int entityID;
    public GuiProviderEntity() { }

    public GuiProviderEntity(Entity entity)
    {
        this.entityID = entity.getEntityId();
    }

    @Override
    public void deserialize(ByteBuf buffer)
    {
        entityID = buffer.readInt();
    }

    @Override
    public void serialize(ByteBuf buffer)
    {
        buffer.writeInt(entityID);
    }

    @Override
    public Entity getOwner(EntityPlayer player, World world, @Nullable EnumHand hand)
    {
        if (world == null) return null;
        return world.getEntityByID(entityID);
    }
}
