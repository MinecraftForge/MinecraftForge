package net.minecraftforge.event.world;

import net.minecraft.world.World;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.event.Event;

public class WeatherEvent extends WorldEvent {
    /**
     * Beware of using these events on a client side, most of them
     * are *not* fired (or if they are, world/worldInfo may be null)!
     */

    public final WorldInfo info;

    public WeatherEvent(World world, WorldInfo info) {
        super(world);
        this.info = info;
    }

    @Event.HasResult
    public static class LightningGeneratedEvent extends WeatherEvent {
        /**
         * This event is fired when a natural lightning is about to be spawned.
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

    /**
     * All events from this point are fired *after* the change.
     */

    public static class RainChangedEvent extends WeatherEvent {
        /**
         * Use info.isRaining() to find out if it's raining.
         * (The world.isRaining() will be updated after some time.)
         */

        public RainChangedEvent(World world, WorldInfo info) {
            super(world, info);
        }
    }

    public static class ThunderingChangedEvent extends WeatherEvent {
        /**
         * Thundering event is fired even when there's no storm.
         * You're probably looking for the storm events.
         */

        public ThunderingChangedEvent(World world, WorldInfo info) {
            super(world, info);
        }
    }

    public static class StormChangedEvent extends WeatherEvent {
        /**
         * storm = raining + thundering
         */

        public final boolean stormActive;

        public StormChangedEvent(World world, WorldInfo info, boolean isStormActive) {
            super(world, info);
            stormActive = isStormActive;
        }
    }
}
