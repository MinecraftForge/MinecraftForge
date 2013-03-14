package net.minecraftforge.event.world;

import net.minecraft.world.World;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.event.Event;

public class WeatherEvent extends WorldEvent {
    public final WorldInfo info;

    public WeatherEvent(World world, WorldInfo info) {
        super(world);
        this.info = info;
    }

    @Event.HasResult
    public static class LightningGeneratedEvent extends WeatherEvent {
        /**
         * This event is fired when a lightning is about to be spawned.
         * Setting result to DENY will suppress creation of lightning.
         */

        public final int x;
        public final int y;
        public final int z;

        public LightningGeneratedEvent(World world, int x, int y, int z, WorldInfo info) {
            super(world, info);
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    public static class RainStartedEvent extends WeatherEvent {
        public RainStartedEvent(World world, WorldInfo info) {
            super(world, info);
        }
    }

    public static class RainStoppedEvent extends WeatherEvent {
        public RainStoppedEvent(World world, WorldInfo info) {
            super(world, info);
        }
    }

    public static class RainChangedEvent extends WeatherEvent {
        public RainChangedEvent(World world, WorldInfo info) {
            super(world, info);
        }
    }

    // thundering event is fired even when there's no storm
    // you're probably looking for storm events
    public static class ThunderingStartedEvent extends WeatherEvent {
        public ThunderingStartedEvent(World world, WorldInfo info) {
            super(world, info);
        }
    }

    public static class ThunderingStoppedEvent extends WeatherEvent {
        public ThunderingStoppedEvent(World world, WorldInfo info) {
            super(world, info);
        }
    }

    public static class ThunderingChangedEvent extends WeatherEvent {
        public ThunderingChangedEvent(World world, WorldInfo info) {
            super(world, info);
        }
    }

    // storm = raining + thundering
    public static class StormStartedEvent extends WeatherEvent{
        public StormStartedEvent(World world, WorldInfo info) {
            super(world, info);
        }
    }

    public static class StormStoppedEvent extends WeatherEvent{
        public StormStoppedEvent(World world, WorldInfo info) {
            super(world, info);
        }
    }

    public static class StormChangedEvent extends WeatherEvent{
        public StormChangedEvent(World world, WorldInfo info) {
            super(world, info);
        }
    }
}
