/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model;

import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.geometry.SimpleUnbakedGeometry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A model composed of vanilla {@linkplain BlockElement block elements}.
 */
public class ElementsModel extends SimpleUnbakedGeometry<ElementsModel>
{
    private static final Logger LOGGER = LogManager.getLogger();

    private final List<BlockElement> elements;
    private final boolean deprecatedLoader;

    public ElementsModel(List<BlockElement> elements)
    {
        this(elements, false);
    }

    private ElementsModel(List<BlockElement> elements, boolean deprecatedLoader)
    {
        this.elements = elements;
        this.deprecatedLoader = deprecatedLoader;
    }

    @Override
    protected void addQuads(IGeometryBakingContext context, IModelBuilder<?> modelBuilder, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ResourceLocation modelLocation)
    {
        if (deprecatedLoader)
            LOGGER.warn("Model \"" + modelLocation + "\" is using the deprecated loader \"minecraft:elements\" instead of \"forge:elements\". This loader will be removed in 1.20.");

        // If there is a root transform, undo the ModelState transform, apply it, then re-apply the ModelState transform.
        // This is necessary because of things like UV locking, which should only respond to the ModelState, and as such
        // that is the only transform that should be applied during face bake.
        var postTransform = context.getRootTransform().isIdentity() ? QuadTransformers.empty() :
                QuadTransformers.applying(modelState.getRotation().compose(context.getRootTransform()).compose(modelState.getRotation().inverse()));

        for (BlockElement element : elements)
        {
            for (Direction direction : element.faces.keySet())
            {
                var face = element.faces.get(direction);
                var sprite = spriteGetter.apply(context.getMaterial(face.texture));
                var quad = BlockModel.bakeFace(element, face, sprite, direction, modelState, modelLocation);
                postTransform.processInPlace(quad);

                if (face.cullForDirection == null)
                    modelBuilder.addUnculledFace(quad);
                else
                    modelBuilder.addCulledFace(modelState.getRotation().rotateTransform(face.cullForDirection), quad);
            }
        }
    }

    public static final class Loader implements IGeometryLoader<ElementsModel>
    {
        public static final Loader INSTANCE = new Loader(false);
        @Deprecated(forRemoval = true, since = "1.19")
        public static final Loader INSTANCE_DEPRECATED = new Loader(true);

        private final boolean deprecated;

        private Loader(boolean deprecated)
        {
            this.deprecated = deprecated;
        }

        @Override
        public ElementsModel read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) throws JsonParseException
        {
            if (!jsonObject.has("elements"))
                throw new JsonParseException("An element model must have an \"elements\" member.");

            List<BlockElement> elements = new ArrayList<>();
            for (JsonElement element : GsonHelper.getAsJsonArray(jsonObject, "elements"))
            {
                elements.add(deserializationContext.deserialize(element, BlockElement.class));
            }

            return new ElementsModel(elements, deprecated);
        }
    }
}
