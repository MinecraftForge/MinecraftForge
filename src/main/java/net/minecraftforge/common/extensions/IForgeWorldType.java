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

import java.util.function.LongFunction;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.CreateBuffetWorldScreen;
import net.minecraft.client.gui.screen.CreateFlatWorldScreen;
import net.minecraft.client.gui.screen.CreateWorldScreen;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.IExtendedNoiseRandom;
import net.minecraft.world.gen.OverworldGenSettings;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.area.IAreaFactory;
import net.minecraft.world.gen.layer.AddBambooForestLayer;
import net.minecraft.world.gen.layer.BiomeLayer;
import net.minecraft.world.gen.layer.EdgeBiomeLayer;
import net.minecraft.world.gen.layer.ZoomLayer;
import net.minecraft.world.gen.layer.LayerUtil;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IForgeWorldType
{
    default WorldType getWorldType()
    {
        return (WorldType) this;
    }

    /**
     * Called when 'Create New World' button is pressed before starting game
     */
    default void onGUICreateWorldPress()
    {
    }

    /**
     * Called when the 'Customize' button is pressed on world creation GUI
     *
     * @param mc  The Minecraft instance
     * @param gui the createworld GUI
     */
    @OnlyIn(Dist.CLIENT)
    default void onCustomizeButton(Minecraft mc, CreateWorldScreen gui)
    {
        if (this == WorldType.FLAT)
            mc.displayGuiScreen(new CreateFlatWorldScreen(gui, gui.chunkProviderSettingsJson));
        else if (this == WorldType.BUFFET)
            mc.displayGuiScreen(new CreateBuffetWorldScreen(gui, gui.chunkProviderSettingsJson));
    }

    default boolean handleSlimeSpawnReduction(java.util.Random random, IWorld world)
    {
        return this == WorldType.FLAT ? random.nextInt(4) != 1 : false;
    }

    default double getHorizon(World world)
    {
        return this == WorldType.FLAT ? 0.0D : 63.0D;
    }

    default double voidFadeMagnitude()
    {
        return this == WorldType.FLAT ? 1.0D : 0.03125D;
    }

    /**
     * Get the height to render the clouds for this world type
     *
     * @return The height to render clouds at
     */
    default float getCloudHeight()
    {
        return 128.0F;
    }

    @SuppressWarnings("deprecation")
    default ChunkGenerator<?> createChunkGenerator(World world)
    {
        return world.dimension.createChunkGenerator();
    }

    /**
     * Allows modifying the {@link IAreaFactory} used for this type's biome
     * generation.
     *
     * @param                <T> The type of {@link IArea}.
     * @param                <C> The type of {@link IContextExtended}.
     *
     * @param parentLayer    The parent layer to feed into any layer you return
     * @param chunkSettings  The {@link OverworldGenSettings} used to create the
     *                       {@link GenLayerBiome}.
     * @param contextFactory A {@link LongFunction} factory to create contexts of
     *                       the supplied size.
     * @return An {@link IAreaFactory} that representing the Biomes to be generated.
     * @see {@link GenLayerBiome}
     */
    default <T extends IArea, C extends IExtendedNoiseRandom<T>> IAreaFactory<T> getBiomeLayer(IAreaFactory<T> parentLayer,
            OverworldGenSettings chunkSettings, LongFunction<C> contextFactory)
    {
        parentLayer = (new BiomeLayer(getWorldType(), chunkSettings.getBiomeId())).apply(contextFactory.apply(200L), parentLayer);
        parentLayer = AddBambooForestLayer.INSTANCE.apply(contextFactory.apply(1001L), parentLayer);
        parentLayer = LayerUtil.repeat(1000L, ZoomLayer.NORMAL, parentLayer, 2, contextFactory);
        parentLayer = EdgeBiomeLayer.INSTANCE.apply(contextFactory.apply(1000L), parentLayer);
        return parentLayer;
    }
}
