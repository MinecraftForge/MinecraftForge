package net.minecraftforge.event.entity.player;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.event.Cancelable;

public class PlayerBlockChangeEvent extends PlayerEvent {

    public final World world;
    public final int x;
    public final int y;
    public final int z;
    public int newId;
    public int newMeta;
    
    public PlayerBlockChangeEvent(EntityPlayer player, World world, int x, int y, int z, int newId, int newMeta)
    {
        super(player);
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.newId = newId;
        this.newMeta = newMeta;
    }

}
