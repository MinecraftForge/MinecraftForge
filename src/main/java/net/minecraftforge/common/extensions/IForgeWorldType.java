package net.minecraftforge.common.extensions;

import java.util.function.LongFunction;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiCreateBuffetWorld;
import net.minecraft.client.gui.GuiCreateFlatWorld;
import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.IContextExtended;
import net.minecraft.world.gen.OverworldGenSettings;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.area.IAreaFactory;
import net.minecraft.world.gen.layer.GenLayerBiome;
import net.minecraft.world.gen.layer.GenLayerBiomeEdge;
import net.minecraft.world.gen.layer.GenLayerZoom;
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
    default void onCustomizeButton(Minecraft mc, GuiCreateWorld gui)
    {
        if (this == WorldType.FLAT)
            mc.displayGuiScreen(new GuiCreateFlatWorld(gui, gui.chunkProviderSettingsJson));
        else if (this == WorldType.CUSTOMIZED)
            mc.displayGuiScreen(new GuiCreateBuffetWorld(gui, gui.chunkProviderSettingsJson));
    }

    default boolean handleSlimeSpawnReduction(java.util.Random random, World world)
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
    default IChunkGenerator<?> createChunkGenerator(World world)
    {
        return world.dimension.createChunkGenerator();
    }

    /**
     * Creates the GenLayerBiome used for generating the world with the specified
     * ChunkProviderSettings JSON String *IF AND ONLY IF* this WorldType ==
     * WorldType.CUSTOMIZED.
     *
     *
     * @param worldSeed     The world seed
     * @param parentLayer   The parent layer to feed into any layer you return
     * @param chunkSettings The ChunkGeneratorSettings constructed from the custom
     *                      JSON
     * @return A GenLayer that will return ints representing the Biomes to be
     *         generated, see GenLayerBiome
     */
    default <T extends IArea, C extends IContextExtended<T>> IAreaFactory<T> getBiomeLayer(IAreaFactory<T> parentLayer,
            OverworldGenSettings chunkSettings, LongFunction<C> contextFactory)
    {
        parentLayer = (new GenLayerBiome(getWorldType(), chunkSettings)).apply((IContextExtended) contextFactory.apply(200L), parentLayer);
        parentLayer = LayerUtil.repeat(1000L, GenLayerZoom.NORMAL, parentLayer, 2, contextFactory);
        parentLayer = GenLayerBiomeEdge.INSTANCE.apply((IContextExtended) contextFactory.apply(1000L), parentLayer);
        return parentLayer;
    }
}
