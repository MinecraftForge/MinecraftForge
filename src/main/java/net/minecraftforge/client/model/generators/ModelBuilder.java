/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model.generators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.math.Transformation;
import com.mojang.serialization.JsonOps;

import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.BlockModel.GuiLight;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockElementRotation;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraftforge.client.model.ForgeFaceData;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.util.TransformationHelper;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * General purpose model builder, contains all the commonalities between item
 * and block models.
 *
 * @see ModelProvider
 * @see BlockModelBuilder
 * @see ItemModelBuilder
 *
 * @param <T> Self type, for simpler chaining of methods.
 */
public class ModelBuilder<T extends ModelBuilder<T>> extends ModelFile {

    @Nullable
    protected ModelFile parent;
    protected final Map<String, String> textures = new LinkedHashMap<>();
    protected final TransformsBuilder transforms = new TransformsBuilder();
    protected final ExistingFileHelper existingFileHelper;

    protected String renderType = null;
    protected boolean ambientOcclusion = true;
    protected GuiLight guiLight = null;

    protected final List<ElementBuilder> elements = new ArrayList<>();

    protected CustomLoaderBuilder<T> customLoader = null;

    private final RootTransformsBuilder rootTransforms = new RootTransformsBuilder();

    protected ModelBuilder(ResourceLocation outputLocation, ExistingFileHelper existingFileHelper)
    {
        super(outputLocation);
        this.existingFileHelper = existingFileHelper;
    }

    @SuppressWarnings("unchecked")
    private T self() { return (T) this; }

    @Override
    protected boolean exists() {
        return true;
    }

    /**
     * Set the parent model for the current model.
     *
     * @param parent the parent model
     * @return this builder
     * @throws NullPointerException  if {@code parent} is {@code null}
     * @throws IllegalStateException if {@code parent} does not {@link ModelFile#assertExistence() exist}
     */
    public T parent(ModelFile parent) {
        Preconditions.checkNotNull(parent, "Parent must not be null");
        parent.assertExistence();
        this.parent = parent;
        return self();
    }

    /**
     * Set the texture for a given dictionary key.
     *
     * @param key     the texture key
     * @param texture the texture, can be another key e.g. {@code "#all"}
     * @return this builder
     * @throws NullPointerException  if {@code key} is {@code null}
     * @throws NullPointerException  if {@code texture} is {@code null}
     * @throws IllegalStateException if {@code texture} is not a key (does not start
     *                               with {@code '#'}) and does not exist in any
     *                               known resource pack
     */
    public T texture(String key, String texture) {
        Preconditions.checkNotNull(key, "Key must not be null");
        Preconditions.checkNotNull(texture, "Texture must not be null");
        if (texture.charAt(0) == '#') {
            this.textures.put(key, texture);
            return self();
        } else {
            ResourceLocation asLoc;
            if (texture.contains(":")) {
                asLoc = new ResourceLocation(texture);
            } else {
                asLoc = new ResourceLocation(getLocation().getNamespace(), texture);
            }
            return texture(key, asLoc);
        }
    }

    /**
     * Set the texture for a given dictionary key.
     *
     * @param key     the texture key
     * @param texture the texture
     * @return this builder
     * @throws NullPointerException  if {@code key} is {@code null}
     * @throws NullPointerException  if {@code texture} is {@code null}
     * @throws IllegalStateException if {@code texture} is not a key (does not start
     *                               with {@code '#'}) and does not exist in any
     *                               known resource pack
     */
    public T texture(String key, ResourceLocation texture) {
        Preconditions.checkNotNull(key, "Key must not be null");
        Preconditions.checkNotNull(texture, "Texture must not be null");
        Preconditions.checkArgument(existingFileHelper.exists(texture, ModelProvider.TEXTURE),
                "Texture %s does not exist in any known resource pack", texture);
        this.textures.put(key, texture.toString());
        return self();
    }

    /**
     * Set the render type for this model.
     *
     * @param renderType the render type. Must be registered via
     *                   {@link net.minecraftforge.client.event.RegisterNamedRenderTypesEvent}
     * @return this builder
     * @throws NullPointerException  if {@code renderType} is {@code null}
     */
    public T renderType(String renderType) {
        Preconditions.checkNotNull(renderType, "Render type must not be null");
        return renderType(new ResourceLocation(renderType));
    }

    /**
     * Set the render type for this model.
     *
     * @param renderType the render type. Must be registered via
     *                   {@link net.minecraftforge.client.event.RegisterNamedRenderTypesEvent}
     * @return this builder
     * @throws NullPointerException  if {@code renderType} is {@code null}
     */
    public T renderType(ResourceLocation renderType) {
        Preconditions.checkNotNull(renderType, "Render type must not be null");
        this.renderType = renderType.toString();
        return self();
    }

    public TransformsBuilder transforms() {
        return transforms;
    }

    public T ao(boolean ao) {
        this.ambientOcclusion = ao;
        return self();
    }

    public T guiLight(GuiLight light) {
        this.guiLight = light;
        return self();
    }

    public ElementBuilder element() {
        Preconditions.checkState(customLoader == null, "Cannot use elements and custom loaders at the same time");
        ElementBuilder ret = new ElementBuilder();
        elements.add(ret);
        return ret;
    }

    /**
     * Get an existing element builder
     *
     * @param index the index of the existing element builder
     * @return the element builder
     * @throws IndexOutOfBoundsException if {@code} index is out of bounds
     */
    public ElementBuilder element(int index) {
        Preconditions.checkState(customLoader == null, "Cannot use elements and custom loaders at the same time");
        Preconditions.checkElementIndex(index, elements.size(), "Element index");
        return elements.get(index);
    }

    /**
     * {@return the number of elements in this model builder}
     */
    public int getElementCount()
    {
        return elements.size();
    }

    /**
     * Use a custom loader instead of the vanilla elements.
     * @param customLoaderFactory function that returns the custom loader to set, given this and the {@link #existingFileHelper}
     * @return the custom loader builder
     */
    public <L extends CustomLoaderBuilder<T>> L customLoader(BiFunction<T, ExistingFileHelper, L> customLoaderFactory)
    {
        Preconditions.checkState(elements.size() == 0, "Cannot use elements and custom loaders at the same time");
        Preconditions.checkNotNull(customLoaderFactory, "customLoaderFactory must not be null");
        L customLoader  = customLoaderFactory.apply(self(), existingFileHelper);
        this.customLoader = customLoader;
        return customLoader;
    }

    public RootTransformsBuilder rootTransforms()
    {
        return rootTransforms;
    }

    @VisibleForTesting
    public JsonObject toJson() {
        JsonObject root = new JsonObject();

        if (this.parent != null) {
            root.addProperty("parent", this.parent.getLocation().toString());
        }

        if (!this.ambientOcclusion) {
            root.addProperty("ambientocclusion", this.ambientOcclusion);
        }

        if (this.guiLight != null) {
            root.addProperty("gui_light", this.guiLight.getSerializedName());
        }

        if (this.renderType != null) {
            root.addProperty("render_type", this.renderType);
        }

        Map<ItemDisplayContext, ItemTransform> transforms = this.transforms.build();
        if (!transforms.isEmpty()) {
            JsonObject display = new JsonObject();
            for (Entry<ItemDisplayContext, ItemTransform> e : transforms.entrySet()) {
                JsonObject transform = new JsonObject();
                ItemTransform vec = e.getValue();
                if (vec.equals(ItemTransform.NO_TRANSFORM)) continue;
                var hasRightRotation = !vec.rightRotation.equals(ItemTransform.Deserializer.DEFAULT_ROTATION);
                if (!vec.translation.equals(ItemTransform.Deserializer.DEFAULT_TRANSLATION)) {
                    transform.add("translation", serializeVector3f(e.getValue().translation));
                }
                if (!vec.rotation.equals(ItemTransform.Deserializer.DEFAULT_ROTATION)) {
                    transform.add(hasRightRotation ? "left_rotation" : "rotation", serializeVector3f(vec.rotation));
                }
                if (!vec.scale.equals(ItemTransform.Deserializer.DEFAULT_SCALE)) {
                    transform.add("scale", serializeVector3f(e.getValue().scale));
                }
                if (hasRightRotation) {
                    transform.add("right_rotation", serializeVector3f(vec.rightRotation));
                }
                display.add(e.getKey().getSerializedName(), transform);
            }
            root.add("display", display);
        }

        if (!this.textures.isEmpty()) {
            JsonObject textures = new JsonObject();
            for (Entry<String, String> e : this.textures.entrySet()) {
                textures.addProperty(e.getKey(), serializeLocOrKey(e.getValue()));
            }
            root.add("textures", textures);
        }

        if (!this.elements.isEmpty()) {
            JsonArray elements = new JsonArray();
            this.elements.stream().map(ElementBuilder::build).forEach(part -> {
                JsonObject partObj = new JsonObject();
                partObj.add("from", serializeVector3f(part.from));
                partObj.add("to", serializeVector3f(part.to));

                if (part.rotation != null) {
                    JsonObject rotation = new JsonObject();
                    rotation.add("origin", serializeVector3f(part.rotation.origin()));
                    rotation.addProperty("axis", part.rotation.axis().getSerializedName());
                    rotation.addProperty("angle", part.rotation.angle());
                    if (part.rotation.rescale()) {
                        rotation.addProperty("rescale", part.rotation.rescale());
                    }
                    partObj.add("rotation", rotation);
                }

                if (!part.shade) {
                    partObj.addProperty("shade", part.shade);
                }

                JsonObject faces = new JsonObject();
                for (Direction dir : Direction.values()) {
                    BlockElementFace face = part.faces.get(dir);
                    if (face == null) continue;

                    JsonObject faceObj = new JsonObject();
                    faceObj.addProperty("texture", serializeLocOrKey(face.texture));
                    if (!Arrays.equals(face.uv.uvs, part.uvsByFace(dir))) {
                        faceObj.add("uv", new Gson().toJsonTree(face.uv.uvs));
                    }
                    if (face.cullForDirection != null) {
                        faceObj.addProperty("cullface", face.cullForDirection.getSerializedName());
                    }
                    if (face.uv.rotation != 0) {
                        faceObj.addProperty("rotation", face.uv.rotation);
                    }
                    if (face.tintIndex != -1) {
                        faceObj.addProperty("tintindex", face.tintIndex);
                    }
                    if (!face.getFaceData().equals(ForgeFaceData.DEFAULT)) {
                        faceObj.add("forge_data", ForgeFaceData.CODEC.encodeStart(JsonOps.INSTANCE, face.getFaceData()).result().get());
                    }
                    faces.add(dir.getSerializedName(), faceObj);
                }
                if (!part.faces.isEmpty()) {
                    partObj.add("faces", faces);
                }
                elements.add(partObj);
            });
            root.add("elements", elements);
        }

        // If there were any transform properties set, add them to the output.
        JsonObject transform = rootTransforms.toJson();
        if (transform.size() > 0)
        {
            root.add("transform", transform);
        }

        if (customLoader != null)
            return customLoader.toJson(root);

        return root;
    }

    private String serializeLocOrKey(String tex) {
        if (tex.charAt(0) == '#') {
            return tex;
        }
        return new ResourceLocation(tex).toString();
    }

    private JsonArray serializeVector3f(Vector3f vec) {
        JsonArray ret = new JsonArray();
        ret.add(serializeFloat(vec.x()));
        ret.add(serializeFloat(vec.y()));
        ret.add(serializeFloat(vec.z()));
        return ret;
    }

    private Number serializeFloat(float f) {
        if ((int) f == f) {
            return (int) f;
        }
        return f;
    }

    public class ElementBuilder {

        private Vector3f from = new Vector3f();
        private Vector3f to = new Vector3f(16, 16, 16);
        private final Map<Direction, FaceBuilder> faces = new LinkedHashMap<>();
        private RotationBuilder rotation;
        private boolean shade = true;
        private int color = 0xFFFFFFFF;
        private int blockLight = 0, skyLight = 0;
        private boolean hasAmbientOcclusion = true;

        private void validateCoordinate(float coord, char name) {
            Preconditions.checkArgument(!(coord < -16.0F) && !(coord > 32.0F), "Position " + name + " out of range, must be within [-16, 32]. Found: %d", coord);
        }

        private void validatePosition(Vector3f pos) {
            validateCoordinate(pos.x(), 'x');
            validateCoordinate(pos.y(), 'y');
            validateCoordinate(pos.z(), 'z');
        }

        /**
         * Set the "from" position for this element.
         *
         * @param x x-position for this vector
         * @param y y-position for this vector
         * @param z z-position for this vector
         * @return this builder
         * @throws IllegalArgumentException if the vector is out of bounds (any
         *                                  coordinate not between -16 and 32,
         *                                  inclusive)
         */
        public ElementBuilder from(float x, float y, float z) {
            this.from = new Vector3f(x, y, z);
            validatePosition(this.from);
            return this;
        }

        /**
         * Set the "to" position for this element.
         *
         * @param x x-position for this vector
         * @param y y-position for this vector
         * @param z z-position for this vector
         * @return this builder
         * @throws IllegalArgumentException if the vector is out of bounds (any
         *                                  coordinate not between -16 and 32,
         *                                  inclusive)
         */
        public ElementBuilder to(float x, float y, float z) {
            this.to = new Vector3f(x, y, z);
            validatePosition(this.to);
            return this;
        }

        /**
         * Return or create the face builder for the given direction.
         *
         * @param dir the direction
         * @return the face builder for the given direction
         * @throws NullPointerException if {@code dir} is {@code null}
         */
        public FaceBuilder face(Direction dir) {
            Preconditions.checkNotNull(dir, "Direction must not be null");
            return faces.computeIfAbsent(dir, FaceBuilder::new);
        }

        public RotationBuilder rotation() {
            if (this.rotation == null) {
                this.rotation = new RotationBuilder();
            }
            return this.rotation;
        }

        public ElementBuilder shade(boolean shade) {
            this.shade = shade;
            return this;
        }

        /**
         * Modify all <em>possible</em> faces dynamically using a function, creating new
         * faces as necessary.
         *
         * @param action the function to apply to each direction
         * @return this builder
         * @throws NullPointerException if {@code action} is {@code null}
         */
        public ElementBuilder allFaces(BiConsumer<Direction, FaceBuilder> action) {
            Arrays.stream(Direction.values())
                .forEach(d -> action.accept(d, face(d)));
            return this;
        }

        /**
         * Modify all <em>existing</em> faces dynamically using a function.
         *
         * @param action the function to apply to each direction
         * @return this builder
         * @throws NullPointerException if {@code action} is {@code null}
         */
        public ElementBuilder faces(BiConsumer<Direction, FaceBuilder> action) {
            faces.entrySet().stream()
                .forEach(e -> action.accept(e.getKey(), e.getValue()));
            return this;
        }

        /**
         * Texture all <em>possible</em> faces in the current element with the given
         * texture, creating new faces where necessary.
         *
         * @param texture the texture
         * @return this builder
         * @throws NullPointerException if {@code texture} is {@code null}
         */
        public ElementBuilder textureAll(String texture) {
            return allFaces(addTexture(texture));
        }

        /**
         * Texture all <em>existing</em> faces in the current element with the given
         * texture.
         *
         * @param texture the texture
         * @return this builder
         * @throws NullPointerException if {@code texture} is {@code null}
         */
        public ElementBuilder texture(String texture) {
            return faces(addTexture(texture));
        }

        /**
         * Create a typical cube element, creating new faces as needed, applying the
         * given texture, and setting the cullface.
         *
         * @param texture the texture
         * @return this builder
         * @throws NullPointerException if {@code texture} is {@code null}
         */
        public ElementBuilder cube(String texture) {
            return allFaces(addTexture(texture).andThen((dir, f) -> f.cullface(dir)));
        }

        /**
         * Set the block and sky light of the element (0-15).
         * Traditional "emissivity" values were set both of these to the same value.
         *
         * @param blockLight the block light
         * @param skyLight the sky light
         * @return this builder
         */
        public ElementBuilder emissivity(int blockLight, int skyLight) {
            this.blockLight = blockLight;
            this.skyLight = skyLight;
            return this;
        }

        /**
         * Sets the color of the element.
         *
         * @param color the color in ARGB format.
         * @return this builder
         */
        public ElementBuilder color(int color) {
            this.color = color;
            return this;
        }

        /**
         * Set the ambient occlusion of the element.
         *
         * @param ao the ambient occlusion
         * @return this builder
         */
        public ElementBuilder ao(boolean ao) {
            this.hasAmbientOcclusion = ao;
            return this;
        }

        private BiConsumer<Direction, FaceBuilder> addTexture(String texture) {
            return ($, f) -> f.texture(texture);
        }

        BlockElement build() {
            Map<Direction, BlockElementFace> faces = this.faces.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().build(), (k1, k2) -> { throw new IllegalArgumentException(); }, LinkedHashMap::new));
            return new BlockElement(from, to, faces, rotation == null ? null : rotation.build(), shade, new ForgeFaceData(this.color, this.blockLight, this.skyLight, this.hasAmbientOcclusion));
        }

        public T end() { return self(); }

        public class FaceBuilder {

            private Direction cullface;
            private int tintindex = -1;
            private String texture = MissingTextureAtlasSprite.getLocation().toString();
            private float[] uvs;
            private FaceRotation rotation = FaceRotation.ZERO;
            private int color = 0xFFFFFFFF;
            private int blockLight = 0, skyLight = 0;
            private boolean hasAmbientOcclusion = true;

            FaceBuilder(Direction dir) {
                // param unused for functional match
            }

            public FaceBuilder cullface(@Nullable Direction dir) {
                this.cullface = dir;
                return this;
            }

            public FaceBuilder tintindex(int index) {
                this.tintindex = index;
                return this;
            }

            /**
             * Set the texture for the current face.
             *
             * @param texture the texture
             * @return this builder
             * @throws NullPointerException if {@code texture} is {@code null}
             */
            public FaceBuilder texture(String texture) {
                Preconditions.checkNotNull(texture, "Texture must not be null");
                this.texture = texture;
                return this;
            }

            public FaceBuilder uvs(float u1, float v1, float u2, float v2) {
                this.uvs = new float[] { u1, v1, u2, v2 };
                return this;
            }

            /**
             * Set the texture rotation for the current face.
             *
             * @param rot the rotation
             * @return this builder
             * @throws NullPointerException if {@code rot} is {@code null}
             */
            public FaceBuilder rotation(FaceRotation rot) {
                Preconditions.checkNotNull(rot, "Rotation must not be null");
                this.rotation = rot;
                return this;
            }

            /**
             * Set the block and sky light of the face (0-15).
             * Traditional "emissivity" values set both of these to the same value.
             *
             * @param blockLight the block light
             * @param skyLight the sky light
             * @return this builder
             */
            public FaceBuilder emissivity(int blockLight, int skyLight) {
                this.blockLight = blockLight;
                this.skyLight = skyLight;
                return this;
            }

            /**
             * Sets the color of the face.
             *
             * @param color the color in ARGB format.
             * @return this builder
             */
            public FaceBuilder color(int color) {
                this.color = color;
                return this;
            }

            /**
             * Set the ambient occlusion of the face.
             *
             * @param ao the ambient occlusion
             * @return this builder
             */
            public FaceBuilder ao(boolean ao) {
                this.hasAmbientOcclusion = ao;
                return this;
            }

            BlockElementFace build() {
                if (this.texture == null) {
                    throw new IllegalStateException("A model face must have a texture");
                }
                return new BlockElementFace(cullface, tintindex, texture, new BlockFaceUV(uvs, rotation.rotation), new ForgeFaceData(this.color, this.blockLight, this.skyLight, this.hasAmbientOcclusion));
            }

            public ElementBuilder end() { return ElementBuilder.this; }
        }

        public class RotationBuilder {

            private Vector3f origin;
            private Direction.Axis axis;
            private float angle;
            private boolean rescale;

            public RotationBuilder origin(float x, float y, float z) {
                this.origin = new Vector3f(x, y, z);
                return this;
            }

            /**
             * @param axis the axis of rotation
             * @return this builder
             * @throws NullPointerException if {@code axis} is {@code null}
             */
            public RotationBuilder axis(Direction.Axis axis) {
                Preconditions.checkNotNull(axis, "Axis must not be null");
                this.axis = axis;
                return this;
            }

            /**
             * @param angle the rotation angle
             * @return this builder
             * @throws IllegalArgumentException if {@code angle} is invalid (not one of 0, +/-22.5, +/-45)
             */
            public RotationBuilder angle(float angle) {
                // Same logic from BlockPart.Deserializer#parseAngle
                Preconditions.checkArgument(angle == 0.0F || Mth.abs(angle) == 22.5F || Mth.abs(angle) == 45.0F, "Invalid rotation %f found, only -45/-22.5/0/22.5/45 allowed", angle);
                this.angle = angle;
                return this;
            }

            public RotationBuilder rescale(boolean rescale) {
                this.rescale = rescale;
                return this;
            }

            BlockElementRotation build() {
                return new BlockElementRotation(origin, axis, angle, rescale);
            }

            public ElementBuilder end() { return ElementBuilder.this; }
        }
    }

    public enum FaceRotation {
        ZERO(0),
        CLOCKWISE_90(90),
        UPSIDE_DOWN(180),
        COUNTERCLOCKWISE_90(270),
        ;

        final int rotation;

        private FaceRotation(int rotation) {
            this.rotation = rotation;
        }
    }

    public class TransformsBuilder {

        private final Map<ItemDisplayContext, TransformVecBuilder> transforms = new LinkedHashMap<>();

        /**
         * Begin building a new transform for the given perspective.
         *
         * @param type the perspective to create or return the builder for
         * @return the builder for the given perspective
         * @throws NullPointerException if {@code type} is {@code null}
         */
        public TransformVecBuilder transform(ItemDisplayContext type) {
            Preconditions.checkNotNull(type, "Perspective cannot be null");
            return transforms.computeIfAbsent(type, TransformVecBuilder::new);
        }

        Map<ItemDisplayContext, ItemTransform> build() {
            return this.transforms.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().build(), (k1, k2) -> { throw new IllegalArgumentException(); }, LinkedHashMap::new));
        }

        public T end() { return self(); }

        public class TransformVecBuilder {

            private Vector3f rotation = new Vector3f(ItemTransform.Deserializer.DEFAULT_ROTATION);
            private Vector3f translation = new Vector3f(ItemTransform.Deserializer.DEFAULT_TRANSLATION);
            private Vector3f scale = new Vector3f(ItemTransform.Deserializer.DEFAULT_SCALE);
            private Vector3f rightRotation = new Vector3f(ItemTransform.Deserializer.DEFAULT_ROTATION);

            TransformVecBuilder(ItemDisplayContext type) {
                // param unused for functional match
            }

            public TransformVecBuilder rotation(float x, float y, float z) {
                this.rotation = new Vector3f(x, y, z);
                return this;
            }

            public TransformVecBuilder leftRotation(float x, float y, float z) {
                return rotation(x, y, z);
            }

            public TransformVecBuilder translation(float x, float y, float z) {
                this.translation = new Vector3f(x, y, z);
                return this;
            }

            public TransformVecBuilder scale(float sc) {
                return scale(sc, sc, sc);
            }

            public TransformVecBuilder scale(float x, float y, float z) {
                this.scale = new Vector3f(x, y, z);
                return this;
            }

            public TransformVecBuilder rightRotation(float x, float y, float z) {
                this.rightRotation = new Vector3f(x, y, z);
                return this;
            }

            ItemTransform build() {
                return new ItemTransform(rotation, translation, scale, rightRotation);
            }

            public TransformsBuilder end() { return TransformsBuilder.this; }
        }
    }

    public class RootTransformsBuilder
    {
        private static final Vector3f ONE = new Vector3f(1, 1, 1);

        private Vector3f translation = new Vector3f();
        private Quaternionf leftRotation = new Quaternionf();
        private Quaternionf rightRotation = new Quaternionf();
        private Vector3f scale = ONE;

        private @Nullable TransformationHelper.TransformOrigin origin;
        private @Nullable Vector3f originVec;

        RootTransformsBuilder()
        {
        }

        /**
         * Sets the translation of the root transform.
         *
         * @param translation the translation
         * @return this builder
         * @throws NullPointerException if {@code translation} is {@code null}
         */
        public RootTransformsBuilder translation(Vector3f translation)
        {
            this.translation = Preconditions.checkNotNull(translation, "Translation must not be null");
            return this;
        }

        /**
         * Sets the translation of the root transform.
         *
         * @param x x translation
         * @param y y translation
         * @param z z translation
         * @return this builder
         */
        public RootTransformsBuilder translation(float x, float y, float z)
        {
            return translation(new Vector3f(x, y, z));
        }

        /**
         * Sets the left rotation of the root transform.
         *
         * @param rotation the left rotation
         * @return this builder
         * @throws NullPointerException if {@code rotation} is {@code null}
         */
        public RootTransformsBuilder rotation(Quaternionf rotation)
        {
            this.leftRotation = Preconditions.checkNotNull(rotation, "Rotation must not be null");
            return this;
        }

        /**
         * Sets the left rotation of the root transform.
         *
         * @param x x rotation
         * @param y y rotation
         * @param z z rotation
         * @param isDegrees whether the rotation is in degrees or radians
         * @return this builder
         */
        public RootTransformsBuilder rotation(float x, float y, float z, boolean isDegrees)
        {
            return rotation(TransformationHelper.quatFromXYZ(x, y, z, isDegrees));
        }

        /**
         * Sets the left rotation of the root transform.
         *
         * @param leftRotation the left rotation
         * @return this builder
         * @throws NullPointerException if {@code leftRotation} is {@code null}
         */
        public RootTransformsBuilder leftRotation(Quaternionf leftRotation)
        {
            return rotation(leftRotation);
        }

        /**
         * Sets the left rotation of the root transform.
         *
         * @param x x rotation
         * @param y y rotation
         * @param z z rotation
         * @param isDegrees whether the rotation is in degrees or radians
         * @return this builder
         */
        public RootTransformsBuilder leftRotation(float x, float y, float z, boolean isDegrees)
        {
            return leftRotation(TransformationHelper.quatFromXYZ(x, y, z, isDegrees));
        }

        /**
         * Sets the right rotation of the root transform.
         *
         * @param rightRotation the right rotation
         * @return this builder
         * @throws NullPointerException if {@code rightRotation} is {@code null}
         */
        public RootTransformsBuilder rightRotation(Quaternionf rightRotation)
        {
            this.rightRotation = Preconditions.checkNotNull(rightRotation, "Rotation must not be null");
            return this;
        }

        /**
         * Sets the right rotation of the root transform.
         *
         * @param x x rotation
         * @param y y rotation
         * @param z z rotation
         * @param isDegrees whether the rotation is in degrees or radians
         * @return this builder
         */
        public RootTransformsBuilder rightRotation(float x, float y, float z, boolean isDegrees)
        {
            return rightRotation(TransformationHelper.quatFromXYZ(x, y, z, isDegrees));
        }

        /**
         * Sets the right rotation of the root transform.
         *
         * @param postRotation the right rotation
         * @return this builder
         * @throws NullPointerException if {@code rightRotation} is {@code null}
         */
        public RootTransformsBuilder postRotation(Quaternionf postRotation)
        {
            return rightRotation(postRotation);
        }

        /**
         * Sets the right rotation of the root transform.
         *
         * @param x x rotation
         * @param y y rotation
         * @param z z rotation
         * @param isDegrees whether the rotation is in degrees or radians
         * @return this builder
         */
        public RootTransformsBuilder postRotation(float x, float y, float z, boolean isDegrees)
        {
            return postRotation(TransformationHelper.quatFromXYZ(x, y, z, isDegrees));
        }

        /**
         * Sets the scale of the root transform.
         *
         * @param scale the scale
         * @return this builder
         */
        public RootTransformsBuilder scale(float scale)
        {
            return scale(new Vector3f(scale, scale, scale));
        }

        /**
         * Sets the scale of the root transform.
         *
         * @param xScale x scale
         * @param yScale y scale
         * @param zScale z scale
         * @return this builder
         */
        public RootTransformsBuilder scale(float xScale, float yScale, float zScale)
        {
            return scale(new Vector3f(xScale, yScale, zScale));
        }

        /**
         * Sets the scale of the root transform.
         *
         * @param scale the scale vector
         * @return this builder
         * @throws NullPointerException if {@code scale} is {@code null}
         */
        public RootTransformsBuilder scale(Vector3f scale)
        {
            this.scale = Preconditions.checkNotNull(scale, "Scale must not be null");
            return this;
        }

        /**
         * Sets the root transform.
         *
         * @param transformation the transformation to use
         * @return this builder
         * @throws NullPointerException if {@code transformation} is {@code null}
         */
        public RootTransformsBuilder transform(Transformation transformation)
        {
            Preconditions.checkNotNull(transformation, "Transformation must not be null");
            this.translation = transformation.getTranslation();
            this.leftRotation = transformation.getLeftRotation();
            this.rightRotation = transformation.getRightRotation();
            this.scale = transformation.getScale();
            return this;
        }

        /**
         * Sets the origin of the root transform.
         *
         * @param origin the origin vector
         * @return this builder
         * @throws NullPointerException if {@code origin} is {@code null}
         */
        public RootTransformsBuilder origin(Vector3f origin)
        {
            this.originVec = Preconditions.checkNotNull(origin, "Origin must not be null");
            this.origin = null;
            return this;
        }

        /**
         * Sets the origin of the root transform.
         *
         * @param origin the origin name
         * @return this builder
         * @throws NullPointerException if {@code origin} is {@code null}
         * @throws IllegalArgumentException if {@code origin} is not {@code center}, {@code corner} or {@code opposing-corner}
         */
        public RootTransformsBuilder origin(TransformationHelper.TransformOrigin origin)
        {
            this.origin = Preconditions.checkNotNull(origin, "Origin must not be null");
            this.originVec = null;
            return this;
        }

        /**
         * Finish configuring the parent builder
         * @return the parent block model builder
         */
        public ModelBuilder<T> end()
        {
            return ModelBuilder.this;
        }

        public JsonObject toJson() {
            // Write the transform to an object
            JsonObject transform = new JsonObject();

            if (!translation.equals(0, 0, 0))
            {
                transform.add("translation", writeVec3(translation));
            }

            if (!scale.equals(ONE))
            {
                transform.add("scale", writeVec3(scale));
            }

            if (!leftRotation.equals(0, 0, 0, 1))
            {
                transform.add("rotation", writeQuaternion(leftRotation));
            }

            if (!rightRotation.equals(0, 0, 0, 1))
            {
                transform.add("post_rotation", writeQuaternion(rightRotation));
            }

            if (origin != null)
            {
                transform.addProperty("origin", origin.getSerializedName());
            }
            else if (originVec != null && !originVec.equals(0, 0, 0))
            {
                transform.add("origin", writeVec3(originVec));
            }

            return transform;
        }

        private static JsonArray writeVec3(Vector3f vector)
        {
            JsonArray array = new JsonArray();
            array.add(vector.x());
            array.add(vector.y());
            array.add(vector.z());
            return array;
        }

        private static JsonArray writeQuaternion(Quaternionf quaternion)
        {
            JsonArray array = new JsonArray();
            array.add(quaternion.x());
            array.add(quaternion.y());
            array.add(quaternion.z());
            array.add(quaternion.w());
            return array;
        }
    }
}
