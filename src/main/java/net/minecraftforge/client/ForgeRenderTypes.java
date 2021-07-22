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

package net.minecraftforge.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderStateShard.TextureStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.util.NonNullLazy;
import net.minecraftforge.common.util.NonNullSupplier;
import org.lwjgl.opengl.GL11;
import java.util.function.Supplier;

@SuppressWarnings("deprecation")
public enum ForgeRenderTypes
{
    ITEM_LAYERED_SOLID(()-> getItemLayeredSolid(TextureAtlas.LOCATION_BLOCKS)),
    ITEM_LAYERED_CUTOUT(()-> getItemLayeredCutout(TextureAtlas.LOCATION_BLOCKS)),
    ITEM_LAYERED_CUTOUT_MIPPED(()-> getItemLayeredCutoutMipped(TextureAtlas.LOCATION_BLOCKS)),
    ITEM_LAYERED_TRANSLUCENT(()-> getItemLayeredTranslucent(TextureAtlas.LOCATION_BLOCKS)),
    ITEM_UNSORTED_TRANSLUCENT(()-> getUnsortedTranslucent(TextureAtlas.LOCATION_BLOCKS)),
    ITEM_UNLIT_TRANSLUCENT(()-> getUnlitTranslucent(TextureAtlas.LOCATION_BLOCKS)),
    ITEM_UNSORTED_UNLIT_TRANSLUCENT(()-> getUnlitTranslucent(TextureAtlas.LOCATION_BLOCKS, false));

    public static boolean enableTextTextureLinearFiltering = false;

    /**
     * @return A RenderType fit for multi-layer solid item rendering.
     */
    public static RenderType getItemLayeredSolid(ResourceLocation textureLocation)
    {
        return Internal.layeredItemSolid(textureLocation);
    }

    /**
     * @return A RenderType fit for multi-layer cutout item item rendering.
     */
    public static RenderType getItemLayeredCutout(ResourceLocation textureLocation)
    {
        return Internal.layeredItemCutout(textureLocation);
    }

    /**
     * @return A RenderType fit for multi-layer cutout-mipped item rendering.
     */
    public static RenderType getItemLayeredCutoutMipped(ResourceLocation textureLocation)
    {
        return Internal.layeredItemCutoutMipped(textureLocation);
    }

    /**
     * @return A RenderType fit for multi-layer translucent item rendering.
     */
    public static RenderType getItemLayeredTranslucent(ResourceLocation textureLocation)
    {
        return Internal.layeredItemTranslucent(textureLocation);
    }

    /**
     * @return A RenderType fit for translucent item/entity rendering, but with depth sorting disabled.
     */
    public static RenderType getUnsortedTranslucent(ResourceLocation textureLocation)
    {
        return Internal.unsortedTranslucent(textureLocation);
    }

    /**
     * @return A RenderType fit for translucent item/entity rendering, but with diffuse lighting disabled
     * so that fullbright quads look correct.
     */
    public static RenderType getUnlitTranslucent(ResourceLocation textureLocation)
    {
        return getUnlitTranslucent(textureLocation, true);
    }

    /**
     * @return A RenderType fit for translucent item/entity rendering, but with diffuse lighting disabled
     * so that fullbright quads look correct.
     * @param sortingEnabled If false, depth sorting will not be performed.
     */
    public static RenderType getUnlitTranslucent(ResourceLocation textureLocation, boolean sortingEnabled)
    {
        return Internal.unlitTranslucent(textureLocation, sortingEnabled);
    }

    /**
     * @return Same as {@link RenderType#getEntityCutout(ResourceLocation)}, but with mipmapping enabled.
     */
    public static RenderType getEntityCutoutMipped(ResourceLocation textureLocation)
    {
        return Internal.layeredItemCutoutMipped(textureLocation);
    }

    /**
     * @return Replacement of {@link RenderType#getText(ResourceLocation)}, but with optional linear texture filtering.
     */
    public static RenderType getText(ResourceLocation locationIn)
    {
        return Internal.getText(locationIn);
    }

    /**
     * @return Replacement of {@link RenderType#getTextSeeThrough(ResourceLocation)}, but with optional linear texture filtering.
     */
    public static RenderType getTextSeeThrough(ResourceLocation locationIn)
    {
        return Internal.getTextSeeThrough(locationIn);
    }

    // ----------------------------------------
    //  Implementation details below this line
    // ----------------------------------------

    private final NonNullSupplier<RenderType> renderTypeSupplier;

    ForgeRenderTypes(NonNullSupplier<RenderType> renderTypeSupplier)
    {
        // Wrap in a Lazy<> to avoid running the supplier more than once.
        this.renderTypeSupplier = NonNullLazy.of(renderTypeSupplier);
    }

    public RenderType get()
    {
        return renderTypeSupplier.get();
    }

    private static class Internal extends RenderType
    {
        private Internal(String name, VertexFormat fmt, VertexFormat.Mode glMode, int size, boolean doCrumbling, boolean depthSorting, Runnable onEnable, Runnable onDisable)
        {
            super(name, fmt, glMode, size, doCrumbling, depthSorting, onEnable, onDisable);
            throw new IllegalStateException("This class must not be instantiated");
        }

        public static RenderType unsortedTranslucent(ResourceLocation textureLocation)
        {
            final boolean sortingEnabled = false;
            CompositeState renderState = CompositeState.builder()
                    .setTextureState(new TextureStateShard(textureLocation, false, false))
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
//                    .setDiffuseLightingState(DIFFUSE_LIGHTING) //TODO
//                    .setAlphaState(DEFAULT_ALPHA) //TODO
                    .setCullState(NO_CULL)
                    .setLightmapState(LIGHTMAP)
                    .setOverlayState(OVERLAY)
                    .createCompositeState(true);
            return create("forge_entity_unsorted_translucent", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, sortingEnabled, renderState);
        }

        public static RenderType unlitTranslucent(ResourceLocation textureLocation, boolean sortingEnabled)
        {
            CompositeState renderState = CompositeState.builder()
                    .setTextureState(new TextureStateShard(textureLocation, false, false))
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
//                    .setAlphaState(DEFAULT_ALPHA) //TODO
                    .setCullState(NO_CULL)
                    .setLightmapState(LIGHTMAP)
                    .setOverlayState(OVERLAY)
                    .createCompositeState(true);
            return create("forge_entity_unlit_translucent", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, sortingEnabled, renderState);
        }

        public static RenderType layeredItemSolid(ResourceLocation locationIn) {
            RenderType.CompositeState rendertype$state = RenderType.CompositeState.builder()
                    .setTextureState(new RenderStateShard.TextureStateShard(locationIn, false, false))
                    .setTransparencyState(NO_TRANSPARENCY)
//                    .setDiffuseLightingState(DIFFUSE_LIGHTING) //TODO
                    .setLightmapState(LIGHTMAP)
                    .setOverlayState(OVERLAY)
                    .createCompositeState(true);
            return create("forge_item_entity_solid", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, false, rendertype$state);
        }

        public static RenderType layeredItemCutout(ResourceLocation locationIn) {
            RenderType.CompositeState rendertype$state = RenderType.CompositeState.builder()
                    .setTextureState(new RenderStateShard.TextureStateShard(locationIn, false, false))
                    .setTransparencyState(NO_TRANSPARENCY)
//                    .setDiffuseLightingState(DIFFUSE_LIGHTING) //TODO
//                    .setAlphaState(DEFAULT_ALPHA) //TODO
                    .setLightmapState(LIGHTMAP)
                    .setOverlayState(OVERLAY)
                    .createCompositeState(true);
            return create("forge_item_entity_cutout", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, false, rendertype$state);
        }

        public static RenderType layeredItemCutoutMipped(ResourceLocation locationIn) {
            RenderType.CompositeState rendertype$state = RenderType.CompositeState.builder()
                    .setTextureState(new RenderStateShard.TextureStateShard(locationIn, false, true))
                    .setTransparencyState(NO_TRANSPARENCY)
//                    .setDiffuseLightingState(DIFFUSE_LIGHTING) //TODO
//                    .setAlphaState(DEFAULT_ALPHA) //TODO
                    .setLightmapState(LIGHTMAP)
                    .setOverlayState(OVERLAY)
                    .createCompositeState(true);
            return create("forge_item_entity_cutout_mipped", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, false, rendertype$state);
        }

        public static RenderType layeredItemTranslucent(ResourceLocation locationIn) {
            RenderType.CompositeState rendertype$state = RenderType.CompositeState.builder()
                    .setTextureState(new RenderStateShard.TextureStateShard(locationIn, false, false))
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
//                    .setDiffuseLightingState(DIFFUSE_LIGHTING) //TODO
//                    .setAlphaState(DEFAULT_ALPHA) //TODO
                    .setLightmapState(LIGHTMAP)
                    .setOverlayState(OVERLAY)
                    .createCompositeState(true);
            return create("forge_item_entity_translucent_cull", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, true, rendertype$state);
        }

        public static RenderType getText(ResourceLocation locationIn) {
            RenderType.CompositeState rendertype$state = RenderType.CompositeState.builder()
                    .setTextureState(new CustomizableTextureState(locationIn, () -> ForgeRenderTypes.enableTextTextureLinearFiltering, () -> false))
//                    .setAlphaState(DEFAULT_ALPHA) //TODO
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .setLightmapState(LIGHTMAP)
                    .createCompositeState(false);
            return create("forge_text", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS, 256, false, true, rendertype$state);
        }

        public static RenderType getTextSeeThrough(ResourceLocation locationIn) {
            RenderType.CompositeState rendertype$state = RenderType.CompositeState.builder()
                    .setTextureState(new CustomizableTextureState(locationIn, () -> ForgeRenderTypes.enableTextTextureLinearFiltering, () -> false))
//                    .setAlphaState(DEFAULT_ALPHA) //TODO
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .setLightmapState(LIGHTMAP)
                    .setDepthTestState(NO_DEPTH_TEST)
                    .setWriteMaskState(COLOR_WRITE)
                    .createCompositeState(false);
            return create("forge_text_see_through", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS, 256, false, true, rendertype$state);
        }
    }

    private static class CustomizableTextureState extends TextureStateShard
    {
        private CustomizableTextureState(ResourceLocation resLoc, Supplier<Boolean> blur, Supplier<Boolean> mipmap)
        {
            super(resLoc, blur.get(), mipmap.get());
            this.setupState = () -> {
                this.blur = blur.get();
                this.mipmap = mipmap.get();
                RenderSystem.enableTexture();
                TextureManager texturemanager = Minecraft.getInstance().getTextureManager();
                texturemanager.bindForSetup(resLoc);
                texturemanager.getTexture(resLoc).setFilter(this.blur, this.mipmap);
            };
        }
    }
}
