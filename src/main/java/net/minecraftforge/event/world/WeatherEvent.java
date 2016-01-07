package net.minecraftforge.event.world;

import net.minecraft.world.World;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

public class WeatherEvent 
{
    /**
     * WeatherEvent.change is called when the weather in a world changes.<br>
     * This event is called when the weather changes in
     * WorldInfo.setRaining,
     * WorldInfo.setThundering,
     * and WorldInfo.setCleanWeatherTime. <br>
     * <br>
     * This event is {@link Cancelable}. Canceling the event prevents the weather from changing.<br>
     * <br>
     * This event does not have a result. {@link Event.HasResult} <br>
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
     */
    @Cancelable
    public static class Change extends WorldEvent 
    {
        public final WeatherType changeTo;

        public Change (World world, WeatherType type) 
        {
            super(world);
            this.changeTo = type;
        }

        public enum WeatherType 
        {
            CLEAR,
            RAIN,
            STORM
        }
    }

    /**
     * WeatherEvent.lightningStrike is called when a lightning bolt is spawned. This only exists because {@link net.minecraftforge.event.entity.living.LivingSpawnEvent} and {@link net.minecraftforge.event.entity.EntityJoinWorldEvent} do not handle {@link EntityLightningBolt}s. <br>
     * This event is called when lightning bolts are spawned in
     * WorldServer.updateBlocks. <br>
     * <br>
     * This event is {@link Cancelable}. Canceling the event prevents the lightning from being spawned.<br>
     * <br>
     * This event does not have a a result. {@link Event.HasResult} <br>
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
     */
    @Cancelable
    public static class LightningStrike extends WorldEvent 
    {
        public final BlockPos pos;

        public LightningStrike (World world, BlockPos pos) 
        {
            super(world);
            this.pos = pos;
        }
    }
}
