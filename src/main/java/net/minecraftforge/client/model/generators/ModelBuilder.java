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

import javax.annotation.Nullable;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minecraft.client.renderer.model.BlockFaceUV;
import net.minecraft.client.renderer.model.BlockModel.GuiLight;
import net.minecraft.client.renderer.model.BlockPart;
import net.minecraft.client.renderer.model.BlockPartFace;
import net.minecraft.client.renderer.model.BlockPartRotation;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.model.ItemTransformVec3f;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.common.data.ExistingFileHelper;

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
@SuppressWarnings("deprecation")
public class ModelBuilder<T extends ModelBuilder<T>> extends ModelFile {

    @Nullable
    protected ModelFile parent;
    protected final Map<String, String> textures = new LinkedHashMap<>();
    protected final TransformsBuilder transforms = new TransformsBuilder();
    protected final ExistingFileHelper existingFileHelper;

    protected boolean ambientOcclusion = true;
    protected GuiLight guiLight = null;

    protected final List<ElementBuilder> elements = new ArrayList<>();

    protected CustomLoaderBuilder customLoader = null;

    protected ModelBuilder(ResourceLocation outputLocation, ExistingFileHelper existingFileHelper) {
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

    public TransformsBuilder transforms() {
        return transforms;
    }

    public T ao(boolean ao) {
        this.ambientOcclusion = ao;
        return self();
    }

    /**
     * @param gui3d
     * @return this builder
     * @deprecated Unused in 1.15, use {@link #guiLight(GuiLight)} instead.
     */
    @Deprecated
    public T gui3d(boolean gui3d) {
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
     * Use a custom loader instead of the vanilla elements.
     * @param customLoaderFactory
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

        Map<Perspective, ItemTransformVec3f> transforms = this.transforms.build();
        if (!transforms.isEmpty()) {
            JsonObject display = new JsonObject();
            for (Entry<Perspective, ItemTransformVec3f> e : transforms.entrySet()) {
                JsonObject transform = new JsonObject();
                ItemTransformVec3f vec = e.getValue();
                if (vec.equals(ItemTransformVec3f.DEFAULT)) continue;
                if (!vec.rotation.equals(ItemTransformVec3f.Deserializer.ROTATION_DEFAULT)) {
                    transform.add("rotation", serializeVector3f(vec.rotation));
                }
                if (!vec.translation.equals(ItemTransformVec3f.Deserializer.TRANSLATION_DEFAULT)) {
                    transform.add("translation", serializeVector3f(e.getValue().translation));
                }
                if (!vec.scale.equals(ItemTransformVec3f.Deserializer.SCALE_DEFAULT)) {
                    transform.add("scale", serializeVector3f(e.getValue().scale));
                }
                display.add(e.getKey().name, transform);
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
                partObj.add("from", serializeVector3f(part.positionFrom));
                partObj.add("to", serializeVector3f(part.positionTo));

                if (part.partRotation != null) {
                    JsonObject rotation = new JsonObject();
                    rotation.add("origin", serializeVector3f(part.partRotation.origin));
                    rotation.addProperty("axis", part.partRotation.axis.getString());
                    rotation.addProperty("angle", part.partRotation.angle);
                    if (part.partRotation.rescale) {
                        rotation.addProperty("rescale", part.partRotation.rescale);
                    }
                    partObj.add("rotation", rotation);
                }

                if (!part.shade) {
                    partObj.addProperty("shade", part.shade);
                }

                JsonObject faces = new JsonObject();
                for (Direction dir : Direction.values()) {
                    BlockPartFace face = part.mapFaces.get(dir);
                    if (face == null) continue;

                    JsonObject faceObj = new JsonObject();
                    faceObj.addProperty("texture", serializeLocOrKey(face.texture));
                    if (!Arrays.equals(face.blockFaceUV.uvs, part.getFaceUvs(dir))) {
                        faceObj.add("uv", new Gson().toJsonTree(face.blockFaceUV.uvs));
                    }
                    if (face.cullFace != null) {
                        faceObj.addProperty("cullface", face.cullFace.getString());
                    }
                    if (face.blockFaceUV.rotation != 0) {
                        faceObj.addProperty("rotation", face.blockFaceUV.rotation);
                    }
                    if (face.tintIndex != -1) {
                        faceObj.addProperty("tintindex", face.tintIndex);
                    }
                    faces.add(dir.getString(), faceObj);
                }
                if (!part.mapFaces.isEmpty()) {
                    partObj.add("faces", faces);
                }
                elements.add(partObj);
            });
            root.add("elements", elements);
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
        ret.add(serializeFloat(vec.getX()));
        ret.add(serializeFloat(vec.getY()));
        ret.add(serializeFloat(vec.getZ()));
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

        private void validateCoordinate(float coord, char name) {
            Preconditions.checkArgument(!(coord < -16.0F) && !(coord > 32.0F), "Position " + name + " out of range, must be within [-16, 32]. Found: %d", coord);
        }

        private void validatePosition(Vector3f pos) {
            validateCoordinate(pos.getX(), 'x');
            validateCoordinate(pos.getY(), 'y');
            validateCoordinate(pos.getZ(), 'z');
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

        private BiConsumer<Direction, FaceBuilder> addTexture(String texture) {
            return ($, f) -> f.texture(texture);
        }

        BlockPart build() {
            Map<Direction, BlockPartFace> faces = this.faces.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().build(), (k1, k2) -> { throw new IllegalArgumentException(); }, LinkedHashMap::new));
            return new BlockPart(from, to, faces, rotation == null ? null : rotation.build(), shade);
        }

        public T end() { return self(); }

        public class FaceBuilder {

            private Direction cullface;
            private int tintindex = -1;
            private String texture = MissingTextureSprite.getLocation().toString();
            private float[] uvs;
            private FaceRotation rotation = FaceRotation.ZERO;

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

            BlockPartFace build() {
                if (this.texture == null) {
                    throw new IllegalStateException("A model face must have a texture");
                }
                return new BlockPartFace(cullface, tintindex, texture, new BlockFaceUV(uvs, rotation.rotation));
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
                Preconditions.checkArgument(angle == 0.0F || MathHelper.abs(angle) == 22.5F || MathHelper.abs(angle) == 45.0F, "Invalid rotation %f found, only -45/-22.5/0/22.5/45 allowed", angle);
                this.angle = angle;
                return this;
            }

            public RotationBuilder rescale(boolean rescale) {
                this.rescale = rescale;
                return this;
            }

            BlockPartRotation build() {
                return new BlockPartRotation(origin, axis, angle, rescale);
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

    // Since vanilla doesn't keep the name in TransformType...
    public enum Perspective {

        THIRDPERSON_RIGHT(TransformType.THIRD_PERSON_RIGHT_HAND, "thirdperson_righthand"),
        THIRDPERSON_LEFT(TransformType.THIRD_PERSON_LEFT_HAND, "thirdperson_lefthand"),
        FIRSTPERSON_RIGHT(TransformType.FIRST_PERSON_RIGHT_HAND, "firstperson_righthand"),
        FIRSTPERSON_LEFT(TransformType.FIRST_PERSON_LEFT_HAND, "firstperson_lefthand"),
        HEAD(TransformType.HEAD, "head"),
        GUI(TransformType.GUI, "gui"),
        GROUND(TransformType.GROUND, "ground"),
        FIXED(TransformType.FIXED, "fixed"),
        ;

        public final TransformType vanillaType;
        final String name;

        private Perspective(TransformType vanillaType, String name) {
            this.vanillaType = vanillaType;
            this.name = name;
        }
    }

    public class TransformsBuilder {

        private final Map<Perspective, TransformVecBuilder> transforms = new LinkedHashMap<>();

        /**
         * Begin building a new transform for the given perspective.
         *
         * @param type the perspective to create or return the builder for
         * @return the builder for the given perspective
         * @throws NullPointerException if {@code type} is {@code null}
         */
        public TransformVecBuilder transform(Perspective type) {
            Preconditions.checkNotNull(type, "Perspective cannot be null");
            return transforms.computeIfAbsent(type, TransformVecBuilder::new);
        }

        Map<Perspective, ItemTransformVec3f> build() {
            return this.transforms.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().build(), (k1, k2) -> { throw new IllegalArgumentException(); }, LinkedHashMap::new));
        }

        public T end() { return self(); }

        public class TransformVecBuilder {

            private Vector3f rotation = new Vector3f();
            private Vector3f translation = new Vector3f();
            private Vector3f scale = new Vector3f();

            TransformVecBuilder(Perspective type) {
                // param unused for functional match
            }

            public TransformVecBuilder rotation(float x, float y, float z) {
                this.rotation = new Vector3f(x, y, z);
                return this;
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

            ItemTransformVec3f build() {
                return new ItemTransformVec3f(rotation, translation, scale);
            }

            public TransformsBuilder end() { return TransformsBuilder.this; }
        }
    }
}
