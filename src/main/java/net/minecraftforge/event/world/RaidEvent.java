/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.event.world;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.raid.Raid;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nullable;
import java.util.function.BiFunction;
import java.util.function.Function;

public class RaidEvent extends Event
{
    private final Raid raid;
    public RaidEvent(Raid raid)
    {
        this.raid = raid;
    }

    public Raid getRaid()
    {
        return raid;
    }

    /**
     * RaidEvent.GetOrCreate is triggered when
     * a raid is requested in the given position
     */
    public static class GetOrCreate extends RaidEvent
    {
        private final ServerLevel level;
        private final BlockPos pos;
        private Function<Integer, Raid> customRaid = null;
        public GetOrCreate(@Nullable Raid raid, ServerLevel level, BlockPos pos)
        {
            super(raid);
            this.level = level;
            this.pos = pos;
        }

        /**
         * The server level the raid is requested on
         * @return the server level
         */
        public ServerLevel getLevel()
        {
            return level;
        }

        /**
         * @return the position requested for raid
         */
        public BlockPos getPos()
        {
            return pos;
        }

        /**
         * Gets the existing raid
         * @return null if there's no ongoing raid nearby
         */
        @Nullable
        @Override
        public Raid getRaid()
        {
            return super.getRaid();
        }

        /**
         * Given id, provides a new raid
         * @return Gets the new raid
         */
        @Nullable
        public Function<Integer, Raid> getCustomRaid()
        {
            return customRaid;
        }

        /**
         * Set using custom raids
         * @param customRaid The customized raid
         */
        public void setCustomRaid(Function<Integer, Raid> customRaid)
        {
            this.customRaid = customRaid;
        }
    }

    /**
     * RaidEvent.Load is triggered when
     * {@link net.minecraft.world.entity.raid.Raids#load(ServerLevel, CompoundTag)}
     * trying to load raids
     */
    public static class Load extends RaidEvent
    {
        private BiFunction<ServerLevel, CompoundTag, Raid> raidConstructor = (level, tag) -> null;

        public Load()
        {
            super(null);
        }

        /**
         * Sets a custom raid loader
         * @param raidConstructor own raid constructor
         */
        public void setRaidConstructor(BiFunction<ServerLevel, CompoundTag, Raid> raidConstructor)
        {
            this.raidConstructor = raidConstructor;
        }

        public BiFunction<ServerLevel, CompoundTag, Raid> getRaidConstructor()
        {
            return raidConstructor;
        }
    }

    /**
     * RaidEvent.SpawnGroup is triggered when
     * a raid is trying to spawn a new wave
     *
     * This event is {@link Cancelable}
     * If cancelled, the group will be counted spawned
     * like usual but the entities will not be added.
     */
    @Cancelable
    public static class SpawnGroup extends RaidEvent
    {
        private BlockPos pos;
        private boolean shouldSpawnBonusGroup;
        public SpawnGroup(Raid raid, BlockPos pos, boolean shouldSpawnBonusGroup)
        {
            super(raid);
            this.pos = pos;
            this.shouldSpawnBonusGroup = shouldSpawnBonusGroup;
        }

        /**
         * Gets the position raiders spawning on
         * @return the spawn position
         */
        public BlockPos getPos()
        {
            return pos;
        }

        /**
         * Sets the position of raiders going to spawn on
         * @param pos the new spawn position
         */
        public void setPos(BlockPos pos)
        {
            this.pos = pos;
        }

        /**
         * Should the raid spawn bonus groups
         * Default {@link Raid#shouldSpawnBonusGroup()}
         */
        @SuppressWarnings("JavadocReference")
        public boolean shouldSpawnBonusGroup()
        {
            return shouldSpawnBonusGroup;
        }

        /**
         * Set whether the bonus group should spawn
         */
        public void setShouldSpawnBonusGroup(boolean shouldSpawnBonusGroup)
        {
            this.shouldSpawnBonusGroup = shouldSpawnBonusGroup;
        }
    }

    /**
     * RaidEvent.Stop is triggered when the raid
     * attempts to end
     *
     * This event is {@link Cancelable}
     * If cancelled, the raid is not stopped
     */
    @Cancelable
    public static class Stop extends RaidEvent
    {
        public Stop(Raid raid)
        {
            super(raid);
        }
    }
}
