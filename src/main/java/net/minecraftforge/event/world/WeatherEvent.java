package net.minecraftforge.event.world;

import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.Event.HasResult;

/**
 * WeatherEvent is fired when an event involving the weather is about to occur.<br>
 * If a method utilizes this {@link Event} as its parameter, the method will
 * receive every child event of this class.<br>
 * <br>
 * {@link #worldInfo} contains the {@link WorldInfo} this event is occurring in.<br>
 * <br>
 * All children of this event are fired on the {@link MinecraftForge#EVENT_BUS}.<br>
 **/   
public class WeatherEvent extends Event
{
    private final WorldInfo worldInfo;
    
    public WorldInfo getWorldInfo()
    {
        return worldInfo;
    }
    
    public WeatherEvent(WorldInfo info)
    {
        this.worldInfo = info;
    }
    
    /**
     * WeatherEvent.CleanWeatherTime is fired when a change of the clean weather time in a WorldInfo is about to occur.<br>
     * This event is fired when the method WorldInfo#setCleanWeatherTime(int) is called. <br>
     * <br>
     * {@link #newCleanWeatherTime} contains the value the clean weather time is about to be set to.<br>
     * <br>
     * This event is {@link Cancelable}. Canceling it will prevent the changes form occurring.<br>
     * <br>
     * This event does not have a result. {@link HasResult} <br>
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
     **/    
    @Cancelable
    public static class CleanWeatherTime extends WeatherEvent
    {
        private final int newCleanWeatherTime;
        
        public CleanWeatherTime(WorldInfo info,int newCleanWeatherTime)
        {
            super(info);
            this.newCleanWeatherTime = newCleanWeatherTime;
        }

        public int getNewCleanWeatherTime()
        {
            return newCleanWeatherTime;
        }
    }
    
    /**
     * WeatherEvent.RainingState is fired when a change of the raining state in a WorldInfo is about to occur.<br>
     * This event is fired when the method WorldInfo#setRaining(boolean) is called. <br>
     * <br>
     * {@link #newRainingState} contains the value the raining state is about to be set to.<br>
     * <br>
     * This event is {@link Cancelable}. Canceling it will prevent the changes form occurring.<br>
     * <br>
     * This event does not have a result. {@link HasResult} <br>
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
     **/
    @Cancelable
    public static class RainingState extends WeatherEvent
    {
        private final boolean newRainingState;

        public RainingState(WorldInfo info,boolean newRainingState)
        {
            super(info);
            this.newRainingState = newRainingState;
        }

        public boolean getNewRainingState()
        {
            return newRainingState;
        }
    }
    
    /**
     * WeatherEvent.RainTime is fired when a change of the rain time in a WorldInfo is about to occur.<br>
     * This event is fired when the method WorldInfo#setRainTime(int) is called. <br>
     * <br>
     * {@link #newRainTime} contains the value the rain time is about to be set to.<br>
     * <br>
     * This event is {@link Cancelable}. Canceling it will prevent the changes form occurring.<br>
     * <br>
     * This event does not have a result. {@link HasResult} <br>
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
     **/
    @Cancelable
    public static class RainTime extends WeatherEvent
    {
        private final int newRainTime;
        
        public RainTime(WorldInfo info,int newRainTime)
        {
            super(info);
            this.newRainTime = newRainTime;
        }

        public int getNewRainTime()
        {
            return newRainTime;
        }
    }

    /**
     * WeatherEvent.ThunderingState is fired when a change of the thundering state in a WorldInfo is about to occur.<br>
     * This event is fired when the method WorldInfo#setThundering(boolean) is called. <br>
     * <br>
     * {@link #newThunderingState} contains the value the thundering state is about to be set to.<br>
     * <br>
     * This event is {@link Cancelable}. Canceling it will prevent the changes form occurring.<br>
     * <br>
     * This event does not have a result. {@link HasResult} <br>
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
     **/
    @Cancelable
    public static class ThunderingState extends WeatherEvent
    {
        private final boolean newThunderingState;
        
        public ThunderingState(WorldInfo info,boolean newThunderingState)
        {
            super(info);
            this.newThunderingState = newThunderingState;
        }

        public boolean getNewThunderingState()
        {
            return newThunderingState;
        }
    }
    
    /**
     * WeatherEvent.ThunderTime is fired when a change of the thunder time in a WorldInfo is about to occur.<br>
     * This event is fired when the method WorldInfo#setThunderTime(int) is called. <br>
     * <br>
     * {@link #newThunderTime} contains the value the thunder time is about to be set to.<br>
     * <br>
     * This event is {@link Cancelable}. Canceling it will prevent the changes form occurring.<br>
     * <br>
     * This event does not have a result. {@link HasResult} <br>
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
     **/
    @Cancelable
    public static class ThunderTime extends WeatherEvent
    {
        private final int newThunderTime;
        
        public ThunderTime(WorldInfo info,int newThunderTime)
        {
            super(info);
            this.newThunderTime = newThunderTime;
        }

        public int getNewThunderTime()
        {
            return newThunderTime;
        }
    }
}
