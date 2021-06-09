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
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderState.TextureState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderType.State;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.NonNullLazy;
import net.minecraftforge.common.util.NonNullSupplier;
import org.lwjgl.opengl.GL11;
import java.util.function.Supplier;

@SuppressWarnings("deprecation")
public enum ForgeRenderTypes
{
    ITEM_LAYERED_SOLID(()-> getItemLayeredSolid(AtlasTexture.LOCATION_BLOCKS)),
    ITEM_LAYERED_CUTOUT(()-> getItemLayeredCutout(AtlasTexture.LOCATION_BLOCKS)),
    ITEM_LAYERED_CUTOUT_MIPPED(()-> getItemLayeredCutoutMipped(AtlasTexture.LOCATION_BLOCKS)),
    ITEM_LAYERED_TRANSLUCENT(()-> getItemLayeredTranslucent(AtlasTexture.LOCATION_BLOCKS)),
    ITEM_UNSORTED_TRANSLUCENT(()-> getUnsortedTranslucent(AtlasTexture.LOCATION_BLOCKS)),
    ITEM_UNLIT_TRANSLUCENT(()-> getUnlitTranslucent(AtlasTexture.LOCATION_BLOCKS)),
    ITEM_UNSORTED_UNLIT_TRANSLUCENT(()-> getUnlitTranslucent(AtlasTexture.LOCATION_BLOCKS, false));

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
        private Internal(String name, VertexFormat fmt, int glMode, int size, boolean doCrumbling, boolean depthSorting, Runnable onEnable, Runnable onDisable)
        {
            super(name, fmt, glMode, size, doCrumbling, depthSorting, onEnable, onDisable);
            throw new IllegalStateException("This class must not be instantiated");
        }

        public static RenderType unsortedTranslucent(ResourceLocation textureLocation)
        {
            final boolean sortingEnabled = false;
            State renderState = State.builder()
                    .setTextureState(new TextureState(textureLocation, false, false))
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .setDiffuseLightingState(DIFFUSE_LIGHTING)
                    .setAlphaState(DEFAULT_ALPHA)
                    .setCullState(NO_CULL)
                    .setLightmapState(LIGHTMAP)
                    .setOverlayState(OVERLAY)
                    .createCompositeState(true);
            return create("forge_entity_unsorted_translucent", DefaultVertexFormats.NEW_ENTITY, GL11.GL_QUADS, 256, true, sortingEnabled, renderState);
        }

        public static RenderType unlitTranslucent(ResourceLocation textureLocation, boolean sortingEnabled)
        {
            State renderState = State.builder()
                    .setTextureState(new TextureState(textureLocation, false, false))
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .setAlphaState(DEFAULT_ALPHA)
                    .setCullState(NO_CULL)
                    .setLightmapState(LIGHTMAP)
                    .setOverlayState(OVERLAY)
                    .createCompositeState(true);
            return create("forge_entity_unlit_translucent", DefaultVertexFormats.NEW_ENTITY, GL11.GL_QUADS, 256, true, sortingEnabled, renderState);
        }

        public static RenderType layeredItemSolid(ResourceLocation locationIn) {
            RenderType.State rendertype$state = RenderType.State.builder()
                    .setTextureState(new RenderState.TextureState(locationIn, false, false))
                    .setTransparencyState(NO_TRANSPARENCY)
                    .setDiffuseLightingState(DIFFUSE_LIGHTING)
                    .setLightmapState(LIGHTMAP)
                    .setOverlayState(OVERLAY)
                    .createCompositeState(true);
            return create("forge_item_entity_solid", DefaultVertexFormats.NEW_ENTITY, 7, 256, true, false, rendertype$state);
        }

        public static RenderType layeredItemCutout(ResourceLocation locationIn) {
            RenderType.State rendertype$state = RenderType.State.builder()
                    .setTextureState(new RenderState.TextureState(locationIn, false, false))
                    .setTransparencyState(NO_TRANSPARENCY)
                    .setDiffuseLightingState(DIFFUSE_LIGHTING)
                    .setAlphaState(DEFAULT_ALPHA)
                    .setLightmapState(LIGHTMAP)
                    .setOverlayState(OVERLAY)
                    .createCompositeState(true);
            return create("forge_item_entity_cutout", DefaultVertexFormats.NEW_ENTITY, 7, 256, true, false, rendertype$state);
        }

        public static RenderType layeredItemCutoutMipped(ResourceLocation locationIn) {
            RenderType.State rendertype$state = RenderType.State.builder()
                    .setTextureState(new RenderState.TextureState(locationIn, false, true))
                    .setTransparencyState(NO_TRANSPARENCY)
                    .setDiffuseLightingState(DIFFUSE_LIGHTING)
                    .setAlphaState(DEFAULT_ALPHA)
                    .setLightmapState(LIGHTMAP)
                    .setOverlayState(OVERLAY)
                    .createCompositeState(true);
            return create("forge_item_entity_cutout_mipped", DefaultVertexFormats.NEW_ENTITY, 7, 256, true, false, rendertype$state);
        }

        public static RenderType layeredItemTranslucent(ResourceLocation locationIn) {
            RenderType.State rendertype$state = RenderType.State.builder()
                    .setTextureState(new RenderState.TextureState(locationIn, false, false))
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .setDiffuseLightingState(DIFFUSE_LIGHTING)
                    .setAlphaState(DEFAULT_ALPHA)
                    .setLightmapState(LIGHTMAP)
                    .setOverlayState(OVERLAY)
                    .createCompositeState(true);
            return create("forge_item_entity_translucent_cull", DefaultVertexFormats.NEW_ENTITY, 7, 256, true, true, rendertype$state);
        }

        public static RenderType getText(ResourceLocation locationIn) {
            RenderType.State rendertype$state = RenderType.State.builder()
                    .setTextureState(new CustomizableTextureState(locationIn, () -> ForgeRenderTypes.enableTextTextureLinearFiltering, () -> false))
                    .setAlphaState(DEFAULT_ALPHA)
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .setLightmapState(LIGHTMAP)
                    .createCompositeState(false);
            return create("forge_text", DefaultVertexFormats.POSITION_COLOR_TEX_LIGHTMAP, 7, 256, false, true, rendertype$state);
        }

        public static RenderType getTextSeeThrough(ResourceLocation locationIn) {
            RenderType.State rendertype$state = RenderType.State.builder()
                    .setTextureState(new CustomizableTextureState(locationIn, () -> ForgeRenderTypes.enableTextTextureLinearFiltering, () -> false))
                    .setAlphaState(DEFAULT_ALPHA)
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .setLightmapState(LIGHTMAP)
                    .setDepthTestState(NO_DEPTH_TEST)
                    .setWriteMaskState(COLOR_WRITE)
                    .createCompositeState(false);
            return create("forge_text_see_through", DefaultVertexFormats.POSITION_COLOR_TEX_LIGHTMAP, 7, 256, false, true, rendertype$state);
        }
    }

    private static class CustomizableTextureState extends TextureState
    {
        private CustomizableTextureState(ResourceLocation resLoc, Supplier<Boolean> blur, Supplier<Boolean> mipmap)
        {
            super(resLoc, blur.get(), mipmap.get());
            this.setupState = () -> {
                this.blur = blur.get();
                this.mipmap = mipmap.get();
                RenderSystem.enableTexture();
                TextureManager texturemanager = Minecraft.getInstance().getTextureManager();
                texturemanager.bind(resLoc);
                texturemanager.getTexture(resLoc).setFilter(this.blur, this.mipmap);
            };
        }
    }
}
