package net.minecraftforge.event.entity.player;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.event.Cancelable;

public class PlayerBlockEvent extends PlayerEvent {

    public final Stage stage;
    public final World world;
    public final int x;
    public final int y;
    public final int z;
    public final int oldId;
    public final int oldMeta;
    
    public PlayerBlockEvent(EntityPlayer player, World world, int x, int y, int z, int oldId, int oldMeta, Stage stage)
    {
        super(player);
        this.stage = stage;
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.oldId = oldId;
        this.oldMeta = oldMeta;
    }
    
    @Override
    public boolean isCancelable()
    {
        return stage == Stage.PRE;
    }

    public static class Break extends PlayerBlockEvent
    {

        public Break(EntityPlayer player, World world, int x, int y, int z, int oldId, int oldMeta, Stage stage)
        {
            super(player, world, x, y, z, oldId, oldMeta, stage);
        }
        
    }
    
    public static class Place extends PlayerBlockEvent
    {

        public final int newId;
        public final int newMeta;
        
        public Place(EntityPlayer player, World world, int x, int y, int z, int oldId, int oldMeta, int newId, int newMeta, Stage stage)
        {
            super(player, world, x, y, z, oldId, oldMeta, stage);
            this.newId = newId;
            this.newMeta = newMeta;
        }
        
    }
    
    public static enum Stage
    {
        
        PRE, POST
        
    }

}
