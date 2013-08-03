package net.minecraftforge.event.entity.living;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import net.minecraftforge.event.Cancelable;
import static net.minecraftforge.event.Event.Result.*;

@Cancelable
public class LivingOnLadderEvent extends LivingEvent
{
    public Block block;
    public World world;
    public int blockX, blockY, blockZ;

    Result isLivingOnLadder;

    public LivingOnLadderEvent(EntityLivingBase entity, Block block, World world, int x, int y, int z)
    {
        super(entity);
        this.isLivingOnLadder = DEFAULT;
        this.block = block;
        this.world = world;
        this.blockX = x;
        this.blockY = y;
        this.blockZ = z;
    }

    public boolean isLivingOnLadder()
    {
        return this.isLivingOnLadder == ALLOW;
    }

    public void setLivingIsOnLadder()
    {
        this.isLivingOnLadder = ALLOW;
        this.setCanceled(true);
    }
}