package net.minecraftforge.event;
import net.minecraft.src.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class ForgeEventFactory
{
    public static boolean doPlayerHarvestCheck(EntityPlayer player, Block block, boolean success)
    {
        PlayerEvent.HarvestCheck event = new PlayerEvent.HarvestCheck(player, block, success);
        MinecraftForge.EVENT_BUS.post(event);
        return event.success;
    }

    public static float getBreakSpeed(EntityPlayer player, Block block, int metadata, float original)
    {
        PlayerEvent.BreakSpeed event = new PlayerEvent.BreakSpeed(player, block, metadata, original);
        return (MinecraftForge.EVENT_BUS.post(event) ? -1 : event.newSpeed);
    }
}
