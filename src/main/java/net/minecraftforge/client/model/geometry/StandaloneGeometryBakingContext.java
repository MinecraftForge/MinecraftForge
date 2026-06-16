/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model.geometry;

import com.mojang.math.Transformation;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import net.minecraft.client.resources.model.cuboid.ItemTransforms;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiPredicate;

/**
 * A {@linkplain IGeometryBakingContext geometry baking context} that is not bound to block/item model loading.
 */
public class StandaloneGeometryBakingContext implements IGeometryBakingContext {
    public static final Identifier LOCATION = Identifier.fromNamespaceAndPath("forge", "standalone");

    public static final StandaloneGeometryBakingContext INSTANCE = builder().build();

    private final boolean isGui3d;
    private final boolean useBlockLight;
    private final boolean useAmbientOcclusion;
    private final ItemTransforms transforms;
    private final Transformation rootTransform;
    @Nullable
    private final Identifier renderTypeHint;
    @Nullable
    private final Identifier renderTypeFastHint;
    private final BiPredicate<String, Boolean> visibilityTest;

    private StandaloneGeometryBakingContext(
        boolean isGui3d,
        boolean useBlockLight, boolean useAmbientOcclusion,
        ItemTransforms transforms, Transformation rootTransform,
        @Nullable Identifier renderTypeHint,
        BiPredicate<String, Boolean> visibilityTest
    ) {
        this(isGui3d, useBlockLight, useAmbientOcclusion, transforms, rootTransform, renderTypeHint, null, visibilityTest);
    }

    private StandaloneGeometryBakingContext(
        boolean isGui3d,
        boolean useBlockLight, boolean useAmbientOcclusion,
        ItemTransforms transforms, Transformation rootTransform,
        @Nullable Identifier renderTypeHint,
        @Nullable Identifier renderTypeFastHint,
        BiPredicate<String, Boolean> visibilityTest
    ) {
        this.isGui3d = isGui3d;
        this.useBlockLight = useBlockLight;
        this.useAmbientOcclusion = useAmbientOcclusion;
        this.transforms = transforms;
        this.rootTransform = rootTransform;
        this.renderTypeHint = renderTypeHint;
        this.renderTypeFastHint = renderTypeFastHint;
        this.visibilityTest = visibilityTest;
    }

    @Override
    public boolean isGui3d() {
        return isGui3d;
    }

    @Override
    public boolean useBlockLight() {
        return useBlockLight;
    }

    @Override
    public boolean useAmbientOcclusion() {
        return useAmbientOcclusion;
    }

    @Override
    public ItemTransforms getTransforms() {
        return transforms;
    }

    @Override
    public Transformation getRootTransform() {
        return rootTransform;
    }

    @Nullable
    @Override
    public Identifier getRenderTypeHint() {
        return renderTypeHint;
    }

    @Nullable
    @Override
    public Identifier getRenderTypeFastHint() {
        return renderTypeFastHint;
    }

    @Override
    public boolean isComponentVisible(String component, boolean fallback) {
        return visibilityTest.test(component, fallback);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(IGeometryBakingContext parent) {
        return new Builder(parent);
    }

    public static final class Builder {
        private boolean isGui3d = true;
        private boolean useBlockLight = true;
        private boolean useAmbientOcclusion = true;
        private ItemTransforms transforms = ItemTransforms.NO_TRANSFORMS;
        private Transformation rootTransform = Transformation.IDENTITY;
        @Nullable
        private Identifier renderTypeHint;
        @Nullable
        private Identifier renderTypeFastHint;
        private BiPredicate<String, Boolean> visibilityTest = (_, def) -> def;

        private Builder() { }

        private Builder(IGeometryBakingContext parent) {
            this.isGui3d = parent.isGui3d();
            this.useBlockLight = parent.useBlockLight();
            this.useAmbientOcclusion = parent.useAmbientOcclusion();
            this.transforms = parent.getTransforms();
            this.rootTransform = parent.getRootTransform();
            this.renderTypeHint = parent.getRenderTypeHint();
            this.renderTypeFastHint = parent.getRenderTypeFastHint();
            this.visibilityTest = parent::isComponentVisible;
        }

        public Builder withGui3d(boolean isGui3d) {
            this.isGui3d = isGui3d;
            return this;
        }

        public Builder withUseBlockLight(boolean useBlockLight) {
            this.useBlockLight = useBlockLight;
            return this;
        }

        public Builder withUseAmbientOcclusion(boolean useAmbientOcclusion) {
            this.useAmbientOcclusion = useAmbientOcclusion;
            return this;
        }

        public Builder withTransforms(ItemTransforms transforms) {
            this.transforms = transforms;
            return this;
        }

        public Builder withRootTransform(Transformation rootTransform) {
            this.rootTransform = rootTransform;
            return this;
        }

        public Builder withRenderTypeHint(Identifier renderTypeHint) {
            this.renderTypeHint = renderTypeHint;
            return this;
        }

        public Builder withRenderTypeHint(Identifier renderTypeHint, Identifier renderTypeFastHint) {
            this.renderTypeHint = renderTypeHint;
            this.renderTypeFastHint = renderTypeFastHint;
            return this;
        }

        @SuppressWarnings("deprecation")
        public Builder withVisibleComponents(Object2BooleanMap<String> parts) {
            this.visibilityTest = parts::getOrDefault;
            return this;
        }

        public StandaloneGeometryBakingContext build() {
            return new StandaloneGeometryBakingContext(isGui3d, useBlockLight, useAmbientOcclusion, transforms, rootTransform, renderTypeHint, renderTypeFastHint, visibilityTest);
        }
    }
}
