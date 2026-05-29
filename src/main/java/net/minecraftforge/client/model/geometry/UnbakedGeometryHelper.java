/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.client.model.geometry;

import com.mojang.math.Quadrant;
import com.mojang.math.Transformation;
import net.minecraft.client.renderer.block.dispatch.ModelState;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.cuboid.CuboidFace;
import net.minecraft.client.resources.model.cuboid.FaceBakery;
import net.minecraft.client.resources.model.cuboid.ItemModelGenerator;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.client.resources.model.sprite.TextureSlots;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraftsingularity.client.model.SimpleModelState;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.BitSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helper for dealing with unbaked models and geometries.
 */
@ApiStatus.Internal
public class UnbakedGeometryHelper {
    /**
     * Explanation:
     * This takes anything that looks like a valid resourcepack texture location, and tries to extract a Identifier out of it.
     *  1. it will ignore anything up to and including an /assets/ folder,
     *  2. it will take the next path component as a namespace,
     *  3. it will match but skip the /textures/ part of the path,
     *  4. it will take the rest of the path up to but excluding the .png extension as the resource path
     * It's a best-effort situation, to allow model files exported by modelling software to be used without post-processing.
     * Example:
     *   C:\Something\Or Other\src\main\resources\assets\mymodid\textures\item\my_thing.png
     *   ........................................--------_______----------_____________----
     *                                                 <namespace>        <path>
     * Result after replacing '\' to '/': mymodid:item/my_thing
     */
    private static final Pattern FILESYSTEM_PATH_TO_RESLOC =
            Pattern.compile("(?:.*[\\\\/]assets[\\\\/](?<namespace>[a-z_-]+)[\\\\/]textures[\\\\/])?(?<path>[a-z_\\\\/-]+)\\.png");

    private static final Material MISSING_MATERIAL = getMaterial(MissingTextureAtlasSprite.getLocation().toString());

    /**
     * Resolves a material that may have been defined with a filesystem path instead of a proper {@link Identifier}.
     * <p>
     * The target atlas will always be {@link TextureAtlas#LOCATION_BLOCKS}.
     */
    public static Material resolveDirtyMaterial(@Nullable String tex, TextureSlots textures) {
        if (tex == null)
            return MISSING_MATERIAL;

        if (tex.startsWith("#"))
            return textures.getMaterial(tex);

        // Attempt to convert a common (windows/linux/mac) filesystem path to a Identifier.
        // This makes no promises, if it doesn't work, too bad, fix your mtl file.
        Matcher match = FILESYSTEM_PATH_TO_RESLOC.matcher(tex);
        if (match.matches()) {
            String namespace = match.group("namespace");
            String path = match.group("path").replace("\\", "/");
            tex = namespace != null ? namespace + ":" + path : path;
        }

        return getMaterial(tex);
    }

    /**
     * Builds the model who's {@link Direction#NORTH} and {@link Direction#SOUTH} faces take up only the pixels the texture uses.
     *
     * This is basically ItemModelGenrator#bakeExtrudedSprite except for the front faces are not spread across the entire side
     *
     * Takes in a template MaterialInfo for the geometry, and a separate one to actually apply the texture from
     */
    public static void bakeMaskedSprite(
        final QuadCollection.Builder builder,
        final ModelBaker.Interner interner,
        final ModelState state,
        final BakedQuad.MaterialInfo texture,
        final BakedQuad.MaterialInfo template
    ) {
        var sprite = template.sprite().contents();
        int width = sprite.width();
        int height = sprite.height();
        var bits = new BitSet(width * height);

        // For every frame in the texture, mark all the opaque pixels (this is what vanilla does too)
        sprite.getUniqueFrames().forEach(frame -> {
            for (int x = 0; x < width; x++)
                for (int y = 0; y < height; y++)
                    if (!sprite.isTransparent(frame, x, y))
                        bits.set(x + y * width);
        });

        // Scan in search of opaque pixels
        for (int y = 0; y < height; y++) {
            int xStart = -1;
            for (int x = 0; x < width; x++) {
                var opaque = bits.get(x + y * width);
                if (opaque == (xStart == -1)) { // (opaque && -1) || (!opaque && !-1)
                    if (xStart == -1) {
                        // We have found the start of a new segment, continue
                        xStart = x;
                        continue;
                    }

                    // The segment is over, expand down as far as possible
                    int yEnd = y + 1;
                    expand:
                    for (; yEnd < height; yEnd++)
                        for (int x2 = xStart; x2 <= x; x2++)
                            if (!bits.get(x2 + yEnd * width))
                                break expand;

                    // Mark all pixels in the area as visited
                    for (int i = xStart; i < x; i++)
                        for (int j = y; j < yEnd; j++)
                            bits.clear(i + j * width);

                    var from = new Vector3f(16 * xStart / (float) width, 16 - 16 * yEnd / (float) height, 7.5F);
                    var to = new Vector3f(16 * x / (float) width, 16 - 16 * y / (float) height, 8.5F);

                    // Create element
                    var southUvs = new CuboidFace.UVs(from.x, from.y, to.x, to.y);
                    var northUvs = new CuboidFace.UVs(to.x, from.y, from.x, to.y);
                    builder.addUnculledFace(FaceBakery.bakeQuad(interner, from, to, southUvs, Quadrant.R0, texture, Direction.SOUTH, state, null));
                    builder.addUnculledFace(FaceBakery.bakeQuad(interner, from, to, northUvs, Quadrant.R0, texture, Direction.NORTH, state, null));

                    // Reset xStart
                    xStart = -1;
                }
            }
        }

        // Re-use vanilla code to create the side geometry
        ItemModelGenerator.bakeSideFaces(builder, interner, state, texture, template);
    }

    @SuppressWarnings("deprecation")
    private static Material getMaterial(String texture) {
        return new Material(Identifier.parse(texture));
    }

    /**
     * Create an {@link IQuadTransformer} to apply a {@link Transformation} that undoes the {@link ModelState}
     * transform (blockstate transform), applies the given root transform and then re-applies the
     * blockstate transform.
     *
     * @return an {@code IQuadTransformer} that applies the root transform to a baked quad that already has the
     * transformation of the given {@code ModelState} applied to it
     */
    /*
    public static IQuadTransformer applyRootTransform(ModelState modelState, Transformation rootTransform) {
        // Move the origin of the ModelState transform and its inverse from the negative corner to the block center
        // to replicate the way the ModelState transform is applied in the FaceBakery by moving the vertices such that
        // the negative corner acts as the block center
        Transformation transform = modelState.transformation().applyOrigin(new Vector3f(.5F, .5F, .5F));
        return QuadTransformers.applying(transform.compose(rootTransform).compose(transform.inverse()));
    }
    */

    /**
     * {@return a {@link ModelState} that combines the existing model state and the {@linkplain Transformation root transform}}
     */
    public static ModelState composeRootTransformIntoModelState(ModelState modelState, Transformation rootTransform) {
        // Move the origin of the root transform as if the negative corner were the block center to match the way the
        // ModelState transform is applied in the FaceBakery by moving the vertices to be centered on that corner
        rootTransform = rootTransform.applyOrigin(new Vector3f(-.5F, -.5F, -.5F));
        return new SimpleModelState(modelState.transformation().compose(rootTransform));
    }
}
