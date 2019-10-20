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

package net.minecraftforge.client.model.generators;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.*;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

@SuppressWarnings("deprecation")
public class ModelBuilder<T extends ModelBuilder<T>> extends ModelFile {

    @Nullable
    protected ModelFile parent;
    protected final Map<String, String> textures = new HashMap<>();
    protected final TransformsBuilder transforms = new TransformsBuilder();
    protected final ExistingFileHelper existingFileHelper;

    protected boolean ambientOcclusion = true;
    protected boolean gui3d = false;

    protected final List<ElementBuilder> elements = new ArrayList<>();

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

    public T parent(ModelFile parent) {
        parent.assertExistence();
        this.parent = parent;
        return self();
    }

    public T texture(String key, String texture) {
        if (texture.charAt(0)=='#') {
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

    public T texture(String key, ResourceLocation texture) {
        Preconditions.checkArgument(existingFileHelper.exists(texture, ResourcePackType.CLIENT_RESOURCES, ".png", "textures"),
                "Texture "+texture+" exists in none of the specified directories!");
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

    public T gui3d(boolean gui3d) {
        this.gui3d = gui3d;
        return self();
    }

    public ElementBuilder element() {
        ElementBuilder ret = new ElementBuilder();
        elements.add(ret);
        return ret;
    }

    public ElementBuilder element(int index) {
        return elements.get(index);
    }

    public JsonObject serialize() {
        JsonObject root = new JsonObject();
        if (this.parent != null) {
            root.addProperty("parent", this.parent.getLocation().toString());
        }

        if (!this.ambientOcclusion) {
            root.addProperty("ambientocclusion", this.ambientOcclusion);
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
                textures.addProperty(e.getKey(), e.getValue());
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
                    rotation.addProperty("axis", part.partRotation.axis.getName());
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
                    faceObj.addProperty("texture", face.texture);
                    if (!Arrays.equals(face.blockFaceUV.uvs, part.getFaceUvs(dir))) {
                        faceObj.add("uv", new Gson().toJsonTree(face.blockFaceUV.uvs));
                    }
                    if (face.cullFace != null) {
                        faceObj.addProperty("cullface", face.cullFace.getName());
                    }
                    if (face.blockFaceUV.rotation != 0) {
                        faceObj.addProperty("rotation", face.blockFaceUV.rotation);
                    }
                    if (face.tintIndex != -1) {
                        faceObj.addProperty("tintindex", face.tintIndex);
                    }
                    faces.add(dir.getName(), faceObj);
                }
                if (!part.mapFaces.isEmpty()) {
                    partObj.add("faces", faces);
                }
                elements.add(partObj);
            });
            root.add("elements", elements);
        }

        return root;
    }

    private JsonArray serializeVector3f(Vector3f vec) {
        JsonArray ret = new JsonArray();
        ret.add(vec.getX());
        ret.add(vec.getY());
        ret.add(vec.getZ());
        return ret;
    }

    public class ElementBuilder {

        private Vector3f from = new Vector3f();
        private Vector3f to = new Vector3f(16, 16, 16);
        private final Map<Direction, FaceBuilder> faces = new EnumMap<>(Direction.class);
        private BlockPartRotation rotation;
        private boolean shade = true;

        public ElementBuilder from(float x, float y, float z) {
            this.from = new Vector3f(x, y, z);
            return this;
        }

        public ElementBuilder to(float x, float y, float z) {
            this.to = new Vector3f(x, y, z);
            return this;
        }

        public FaceBuilder face(Direction dir) {
            return faces.computeIfAbsent(dir, FaceBuilder::new);
        }

        public ElementBuilder rotation(BlockPartRotation rotation) {
            this.rotation = rotation;
            return this;
        }

        public ElementBuilder shade(boolean shade) {
            this.shade = shade;
            return this;
        }

        public ElementBuilder allFaces(BiConsumer<Direction, FaceBuilder> action) {
            Arrays.stream(Direction.values())
                .forEach(d -> action.accept(d, face(d)));
            return this;
        }

        public ElementBuilder faces(BiConsumer<Direction, FaceBuilder> action) {
            faces.entrySet().stream()
                .forEach(e -> action.accept(e.getKey(), e.getValue()));
            return this;
        }

        public ElementBuilder textureAll(String texture) {
            return allFaces(addTexture(texture));
        }

        public ElementBuilder texture(String texture) {
            return faces(addTexture(texture));
        }

        public ElementBuilder cube(String texture) {
            return allFaces(addTexture(texture).andThen((dir, f) -> f.cullface(dir)));
        }

        private BiConsumer<Direction, FaceBuilder> addTexture(String texture) {
            return ($, f) -> f.texture(texture);
        }

        BlockPart build() {
            Map<Direction, BlockPartFace> faces = this.faces.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().build()));
            return new BlockPart(from, to, faces, rotation, shade);
        }

        public T end() { return self(); }

        public class FaceBuilder {

            private Direction cullface;
            private int tintindex = -1;
            private String texture;
            private float[] uvs;
            private FaceRotation rotation = FaceRotation.ZERO;

            FaceBuilder(Direction dir) {
                // param unused for functional match
            }

            public FaceBuilder cullface(Direction dir) {
                this.cullface = dir;
                return this;
            }

            public FaceBuilder tintindex(int index) {
                this.tintindex = index;
                return this;
            }

            public FaceBuilder texture(String texture) {
                this.texture = texture;
                return this;
            }

            public FaceBuilder uvs(float u1, float v1, float u2, float v2) {
                this.uvs = new float[] { u1, v1, u2, v2 };
                return this;
            }

            public FaceBuilder rotation(FaceRotation rot) {
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

        THRIDPERSON_RIGHT("thirdperson_righthand"),
        THIRDPERSON_LEFT("thirdperson_lefthand"),
        FIRSTPERSON_RIGHT("firstperson_righthand"),
        FIRSTPERSON_LEFT("firstperson_lefthand"),
        HEAD("head"),
        GUI("gui"),
        GROUND("ground"),
        FIXED("fixed"),
        ;

        final String name;

        private Perspective(String name) {
            this.name = name;
        }
    }

    public class TransformsBuilder {

        private final Map<Perspective, TransformVecBuilder> transforms = new EnumMap<>(Perspective.class);

        public TransformVecBuilder transform(Perspective type) {
            return transforms.computeIfAbsent(type, TransformVecBuilder::new);
        }

        Map<Perspective, ItemTransformVec3f> build() {
            return this.transforms.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().build()));
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
