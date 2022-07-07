/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model.geometry;

import com.google.common.base.Predicates;
import com.mojang.math.Transformation;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.textures.UnitTextureAtlasSprite;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A {@linkplain IGeometryBakingContext geometry baking context} that is not bound to block/item model loading.
 */
public class StandaloneGeometryBakingContext implements IGeometryBakingContext
{
    public static final ResourceLocation LOCATION = new ResourceLocation("forge", "standalone");

    public static final StandaloneGeometryBakingContext INSTANCE = create(LOCATION);

    public static StandaloneGeometryBakingContext create(ResourceLocation modelName)
    {
        return builder().build(modelName);
    }

    public static StandaloneGeometryBakingContext create(Map<String, ResourceLocation> textures)
    {
        return create(LOCATION, textures);
    }

    public static StandaloneGeometryBakingContext create(ResourceLocation modelName, Map<String, ResourceLocation> textures)
    {
        return builder().withTextures(textures, MissingTextureAtlasSprite.getLocation()).build(modelName);
    }

    private final ResourceLocation modelName;
    private final Predicate<String> textureCheck;
    private final Function<String, ResourceLocation> textureLookup;
    private final boolean isGui3d;
    private final boolean useBlockLight;
    private final boolean useAmbientOcclusion;
    private final ItemTransforms transforms;
    private final Transformation rootTransform;
    @Nullable
    private final ResourceLocation renderTypeHint;
    private final BiPredicate<String, Boolean> visibilityTest;

    private StandaloneGeometryBakingContext(ResourceLocation modelName, Predicate<String> textureCheck,
                                            Function<String, ResourceLocation> textureLookup, boolean isGui3d,
                                            boolean useBlockLight, boolean useAmbientOcclusion,
                                            ItemTransforms transforms, Transformation rootTransform,
                                            @Nullable ResourceLocation renderTypeHint,
                                            BiPredicate<String, Boolean> visibilityTest)
    {
        this.modelName = modelName;
        this.textureCheck = textureCheck;
        this.textureLookup = textureLookup;
        this.isGui3d = isGui3d;
        this.useBlockLight = useBlockLight;
        this.useAmbientOcclusion = useAmbientOcclusion;
        this.transforms = transforms;
        this.rootTransform = rootTransform;
        this.renderTypeHint = renderTypeHint;
        this.visibilityTest = visibilityTest;
    }

    @Override
    public String getModelName()
    {
        return modelName.toString();
    }

    @Override
    public boolean hasMaterial(String name)
    {
        return textureCheck.test(name);
    }

    @Override
    public Material getMaterial(String name)
    {
        return new Material(UnitTextureAtlasSprite.LOCATION, textureLookup.apply(name));
    }

    @Override
    public boolean isGui3d()
    {
        return isGui3d;
    }

    @Override
    public boolean useBlockLight()
    {
        return useBlockLight;
    }

    @Override
    public boolean useAmbientOcclusion()
    {
        return useAmbientOcclusion;
    }

    @Override
    public ItemTransforms getTransforms()
    {
        return transforms;
    }

    @Override
    public Transformation getRootTransform()
    {
        return rootTransform;
    }

    @Nullable
    @Override
    public ResourceLocation getRenderTypeHint()
    {
        return renderTypeHint;
    }

    @Override
    public boolean isComponentVisible(String component, boolean fallback)
    {
        return visibilityTest.test(component, fallback);
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static Builder builder(IGeometryBakingContext parent)
    {
        return new Builder(parent);
    }

    public static final class Builder
    {
        private Predicate<String> textureCheck = Predicates.alwaysFalse();
        private Function<String, ResourceLocation> textureLookup = $ -> MissingTextureAtlasSprite.getLocation();
        private boolean isGui3d = true;
        private boolean useBlockLight = true;
        private boolean useAmbientOcclusion = true;
        private ItemTransforms transforms = ItemTransforms.NO_TRANSFORMS;
        private Transformation rootTransform = Transformation.identity();
        @Nullable
        private ResourceLocation renderTypeHint;
        private BiPredicate<String, Boolean> visibilityTest = (c, def) -> def;

        private Builder()
        {
        }

        private Builder(IGeometryBakingContext parent)
        {
            this.textureCheck = parent::hasMaterial;
            this.textureLookup = name -> parent.getMaterial(name).texture();
            this.isGui3d = parent.isGui3d();
            this.useBlockLight = parent.useBlockLight();
            this.useAmbientOcclusion = parent.useAmbientOcclusion();
            this.transforms = parent.getTransforms();
            this.rootTransform = parent.getRootTransform();
            this.renderTypeHint = parent.getRenderTypeHint();
            this.visibilityTest = parent::isComponentVisible;
        }

        public Builder withTextures(Map<String, ResourceLocation> textures, ResourceLocation defaultTexture)
        {
            this.textureCheck = textures::containsKey;
            this.textureLookup = name -> textures.getOrDefault(name, defaultTexture);
            return this;
        }

        public Builder withGui3d(boolean isGui3d)
        {
            this.isGui3d = isGui3d;
            return this;
        }

        public Builder withUseBlockLight(boolean useBlockLight)
        {
            this.useBlockLight = useBlockLight;
            return this;
        }

        public Builder withUseAmbientOcclusion(boolean useAmbientOcclusion)
        {
            this.useAmbientOcclusion = useAmbientOcclusion;
            return this;
        }

        public Builder withTransforms(ItemTransforms transforms)
        {
            this.transforms = transforms;
            return this;
        }

        public Builder withRootTransform(Transformation rootTransform)
        {
            this.rootTransform = rootTransform;
            return this;
        }

        public Builder withRenderTypeHint(ResourceLocation renderTypeHint)
        {
            this.renderTypeHint = renderTypeHint;
            return this;
        }

        public Builder withVisibleComponents(Object2BooleanMap<String> parts)
        {
            this.visibilityTest = parts::getOrDefault;
            return this;
        }

        public StandaloneGeometryBakingContext build(ResourceLocation modelName)
        {
            return new StandaloneGeometryBakingContext(modelName, textureCheck, textureLookup, isGui3d, useBlockLight, useAmbientOcclusion, transforms, rootTransform, renderTypeHint, visibilityTest);
        }
    }
}
