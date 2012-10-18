package net.minecraftforge.event.world.terraingen;

import java.util.Random;

import net.minecraft.src.World;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.world.WorldEvent;

@Cancelable
public class ChunkGeneratorSnowAndIce extends WorldEvent
{
    
    public final Random rand;
    public final int worldX;
    public final int worldZ;
    
    private boolean handled;

    public ChunkGeneratorSnowAndIce(World world, Random rand, int worldX, int worldZ)
    {
        super(world);
        this.rand = rand;
        this.worldX = worldX;
        this.worldZ = worldZ;
    }

    public boolean isHandled()
    {
        return handled;
    }

    public void setHandled()
    {
        handled = true;
    }

}
