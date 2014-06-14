package net.minecraftforge.permissions.api.context;

import net.minecraft.entity.player.EntityPlayer;

public class PlayerContext extends EntityContext
{
    private EntityPlayer player;

    public PlayerContext(EntityPlayer entity)
    {
        super(entity);
        this.player = entity;
    }
    
    public EntityPlayer getPlayer()
    {
        return player;
    }
}
