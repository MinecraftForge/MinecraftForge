/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

package net.minecraftforge.common.extensions;

import javax.annotation.Nullable;

import net.minecraft.client.audio.MusicTicker;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.NetherDimension;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.IRenderHandler;

public interface IForgeDimension
{
    default Dimension getDimension()
    {
        return (Dimension) this;
    }

    World getWorld();

    /**
     * Called from {@link World#initCapabilities()}, to gather capabilities for this
     * world. It's safe to access world here since this is called after world is
     * registered.
     *
     * On server, called directly after mapStorage and world data such as Scoreboard
     * and VillageCollection are initialized. On client, called when world is
     * constructed, just before world load event is called. Note that this method is
     * always called before the world load event.
     *
     * @return initial holder for capabilities on the world
     */
    default net.minecraftforge.common.capabilities.ICapabilityProvider initCapabilities()
    {
        return null;
    }

    /**
     * The dimension's movement factor. Whenever a player or entity changes
     * dimension from world A to world B, their coordinates are multiplied by
     * worldA.provider.getMovementFactor() / worldB.provider.getMovementFactor()
     * Example: Overworld factor is 1, nether factor is 8. Traveling from overworld
     * to nether multiplies coordinates by 1/8.
     *
     * @return The movement factor
     */
    default double getMovementFactor()
    {
        if (getDimension() instanceof NetherDimension)
        {
            return 8.0;
        }
        return 1.0;
    }

    @OnlyIn(Dist.CLIENT)
    @Nullable
    IRenderHandler getSkyRenderer();

    @OnlyIn(Dist.CLIENT)
    void setSkyRenderer(IRenderHandler skyRenderer);

    @OnlyIn(Dist.CLIENT)
    @Nullable
    IRenderHandler getCloudRenderer();

    @OnlyIn(Dist.CLIENT)
    void setCloudRenderer(IRenderHandler renderer);

    @OnlyIn(Dist.CLIENT)
    @Nullable
    IRenderHandler getWeatherRenderer();

    @OnlyIn(Dist.CLIENT)
    void setWeatherRenderer(IRenderHandler renderer);

    /**
     * Allows for manipulating the coloring of the lightmap texture.
     * Will be called for each 16*16 combination of sky/block light values.
     *
     * @param partialTicks Progress between ticks.
     * @param sunBrightness Current sun brightness.
     * @param skyLight Sky light brightness factor.
     * @param blockLight Block light brightness factor.
     * @param colors The color values that will be used: [r, g, b].
     *
     * @see net.minecraft.client.renderer.GameRenderer#updateLightmap(float)
     */
    default void getLightmapColors(float partialTicks, float sunBrightness, float skyLight, float blockLight, Vector3f colors) {}

    void resetRainAndThunder();

    default boolean canDoLightning(Chunk chunk)
    {
        return true;
    }

    default boolean canDoRainSnowIce(Chunk chunk)
    {
        return true;
    }

    /**
     * Called on the client to get the music type to play when in this world type.
     * At the time of calling, the client player and world are guaranteed to be non-null
     * @return null to use vanilla logic, otherwise a MusicType to play in this world
     */
    @Nullable
    @OnlyIn(Dist.CLIENT)
    default MusicTicker.MusicType getMusicType()
    {
        return null;
    }

    /**
     * Determines if the player can sleep in this world (or if the bed should explode for example).
     *
     * @param player The player that is attempting to sleep
     * @param pos The location where the player tries to sleep at (the position of the clicked on bed for example)
     * @return the result of a player trying to sleep at the given location
     */
    default SleepResult canSleepAt(net.minecraft.entity.player.PlayerEntity player, BlockPos pos)
    {
        return (getDimension().canRespawnHere() && getWorld().getBiome(pos) != Biomes.NETHER) ? SleepResult.ALLOW : SleepResult.BED_EXPLODES;
    }

    enum SleepResult
    {
        ALLOW,
        DENY,
        BED_EXPLODES;
    }

    default boolean isDaytime()
    {
        return getDimension().getType() == DimensionType.OVERWORLD && getWorld().getSkylightSubtracted() < 4;
    }

    /**
     * Calculates the current moon phase factor.
     * This factor is effective for slimes.
     * (This method do not affect the moon rendering)
     * */
    default float getCurrentMoonPhaseFactor(long time)
    {
        return Dimension.MOON_PHASE_FACTORS[this.getDimension().getMoonPhase(time)];
    }

    default void setAllowedSpawnTypes(boolean allowHostile, boolean allowPeaceful) { }

    default void calculateInitialWeather()
    {
        getWorld().calculateInitialWeatherBody();
    }

    default void updateWeather(Runnable defaultLogic)
    {
        defaultLogic.run();
    }

    default long getSeed()
    {
        return getWorld().getWorldInfo().getSeed();
    }

    default long getWorldTime()
    {
        return getWorld().getWorldInfo().getDayTime();
    }

    default void setWorldTime(long time)
    {
        getWorld().getWorldInfo().setDayTime(time);
    }

    default BlockPos getSpawnPoint()
    {
        WorldInfo info = getWorld().getWorldInfo();
        return new BlockPos(info.getSpawnX(), info.getSpawnY(), info.getSpawnZ());
    }

    default void setSpawnPoint(BlockPos pos)
    {
        getWorld().getWorldInfo().setSpawn(pos);
    }

    default boolean canMineBlock(PlayerEntity player, BlockPos pos)
    {
        return getWorld().canMineBlockBody(player, pos);
    }

    default boolean isHighHumidity(BlockPos pos)
    {
        return getWorld().getBiome(pos).isHighHumidity();
    }

    default int getHeight()
    {
        return 256;
    }

    default int getActualHeight()
    {
        return getDimension().isNether() ? 128 : 256;
    }

    default int getSeaLevel()
    {
        return 63;
    }

    default double getHorizonHeight()
    {
        return getWorld().getWorldInfo().getGenerator() == WorldType.FLAT ? 0.0D : 63.0D;
    }

    /**
     * Determine if the cursor on the map should 'spin' when rendered, like it does for the player in the nether.
     *
     * @param entity The entity holding the map, playername, or frame-ENTITYID
     * @param x X Position
     * @param z Z Position
     * @param rotation the regular rotation of the marker
     * @return True to 'spin' the cursor
     */
    default boolean shouldMapSpin(String entity, double x, double z, double rotation)
    {
        return getDimension().getType() == DimensionType.THE_NETHER;
    }

    /**
     * Determines the dimension the player will be respawned in, typically this brings them back to the overworld.
     *
     * @param player The player that is respawning
     * @return The dimension to respawn the player in
     */
    default DimensionType getRespawnDimension(ServerPlayerEntity player)
    {
        return player.getSpawnDimension();
    }
}
