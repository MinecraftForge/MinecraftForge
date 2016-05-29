package net.minecraftforge.event.world;

import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.Event.HasResult;

/**
 * WeatherEvent is fired when an event involving the weather cycle is about to occur.<br>
 * If a method utilizes this {@link Event} as its parameter, the method will
 * receive every child event of this class.<br>
 * <br>
 * {@link #world} contains the {@link World} this event is occurring in.<br>
 * <br>
 * All children of this event are fired on the {@link MinecraftForge#EVENT_BUS}.<br>
 **/   
public class WeatherEvent extends Event
{
    private final World world;
    
    public World getWorld()
    {
        return world;
    }
    
    public WeatherEvent(World world)
    {
        this.world = world;
    }
    
    /**
     * WeatherEvent.CleanWeatherTime is fired when a change of the clean weather time in the natural weather cycle is about to occur.<br>
     * This event is fired when the method World#updateWeatherBody is called. It is fired on the server side. <br>
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
        
        public CleanWeatherTime(World world, int newCleanWeatherTime)
        {
            super(world);
            this.newCleanWeatherTime = newCleanWeatherTime;
        }

        public int getNewCleanWeatherTime()
        {
            return newCleanWeatherTime;
        }
    }
    
    /**
     * WeatherEvent.RainingState is fired when a change of the raining state in the natural weather cycle is about to occur.<br>
     * This event is fired when the method World#updateWeatherBody is called. It is fired on the server side. <br>
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

        public RainingState(World world, boolean newRainingState)
        {
            super(world);
            this.newRainingState = newRainingState;
        }

        public boolean getNewRainingState()
        {
            return newRainingState;
        }
    }
    
    /**
     * WeatherEvent.RainTime is fired when a change of the rain time in the natural weather cycle is about to occur.<br>
     * This event is fired when the method World#updateWeatherBody is called. It is fired on the server side. <br>
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
        
        public RainTime(World world, int newRainTime)
        {
            super(world);
            this.newRainTime = newRainTime;
        }

        public int getNewRainTime()
        {
            return newRainTime;
        }
    }

    /**
     * WeatherEvent.ThunderingState is fired when a change of the thundering state in the natural weather cycle is about to occur.<br>
     * This event is fired when the method World#updateWeatherBody is called. It is fired on the server side. <br>
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
        
        public ThunderingState(World world, boolean newThunderingState)
        {
            super(world);
            this.newThunderingState = newThunderingState;
        }

        public boolean getNewThunderingState()
        {
            return newThunderingState;
        }
    }
    
    /**
     * WeatherEvent.ThunderTime is fired when a change of the thunder time in the natural weather cycle is about to occur.<br>
     * This event is fired when the method World#updateWeatherBody is called. It is fired on the server side. <br>
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
        
        public ThunderTime(World world, int newThunderTime)
        {
            super(world);
            this.newThunderTime = newThunderTime;
        }

        public int getNewThunderTime()
        {
            return newThunderTime;
        }
    }
}
