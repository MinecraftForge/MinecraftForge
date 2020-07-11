/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

package net.minecraftforge.client.model.b3d;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.TransformationMatrix;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraftforge.client.model.*;
import net.minecraftforge.common.model.*;
import net.minecraftforge.resource.IResourceType;
import net.minecraftforge.resource.ISelectiveResourceReloadListener;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Objects;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.model.b3d.B3DModel.Animation;
import net.minecraftforge.client.model.b3d.B3DModel.Face;
import net.minecraftforge.client.model.b3d.B3DModel.Key;
import net.minecraftforge.client.model.b3d.B3DModel.Mesh;
import net.minecraftforge.client.model.b3d.B3DModel.Node;
import net.minecraftforge.client.model.b3d.B3DModel.Texture;
import net.minecraftforge.client.model.b3d.B3DModel.Vertex;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.pipeline.BakedQuadBuilder;
import net.minecraftforge.client.model.pipeline.IVertexConsumer;
import net.minecraftforge.common.model.animation.IClip;
import net.minecraftforge.common.model.animation.IJoint;
import net.minecraftforge.common.property.Properties;

/*
 * Loader for Blitz3D models.
 * To enable for your mod call instance.addDomain(modId).
 * If you need more control over accepted resources - extend the class, and register a new instance with ModelLoaderRegistry.
 */
// TODO: Implement as a new model loader
public enum B3DLoader implements ISelectiveResourceReloadListener
{
    INSTANCE;

    private static final Logger LOGGER = LogManager.getLogger();

    private IResourceManager manager;

    private final Set<String> enabledDomains = new HashSet<>();
    private final Map<ResourceLocation, B3DModel> cache = new HashMap<>();

    @Override
    public void onResourceManagerReload(IResourceManager manager, Predicate<IResourceType> resourcePredicate)
    {
        this.manager = manager;
        cache.clear();
    }

    @SuppressWarnings("unchecked")
    public IUnbakedModel loadModel(ResourceLocation modelLocation) throws Exception
    {
        ResourceLocation file = new ResourceLocation(modelLocation.getNamespace(), modelLocation.getPath());
        if(!cache.containsKey(file))
        {
            IResource resource = null;
            try
            {
                try
                {
                    resource = manager.getResource(file);
                }
                catch(FileNotFoundException e)
                {
                    if(modelLocation.getPath().startsWith("models/block/"))
                        resource = manager.getResource(new ResourceLocation(file.getNamespace(), "models/item/" + file.getPath().substring("models/block/".length())));
                    else if(modelLocation.getPath().startsWith("models/item/"))
                        resource = manager.getResource(new ResourceLocation(file.getNamespace(), "models/block/" + file.getPath().substring("models/item/".length())));
                    else throw e;
                }
                B3DModel.Parser parser = new B3DModel.Parser(resource.getInputStream());
                B3DModel model = parser.parse();
                cache.put(file, model);
            }
            catch(IOException e)
            {
                cache.put(file, null);
                throw e;
            }
            finally
            {
                IOUtils.closeQuietly(resource);
            }
        }
        B3DModel model = cache.get(file);
        if(model == null) throw new ModelLoadingException("Error loading model previously: " + file);
        if(!(model.getRoot().getKind() instanceof Mesh))
        {
            return new ModelWrapper(modelLocation, model, ImmutableSet.of(), true, true, true, 1);
        }
        return new ModelWrapper(modelLocation, model, ImmutableSet.of(model.getRoot().getName()), true, true, true, 1);
    }

    public static final class B3DState implements IModelTransform
    {
        @Nullable
        private final Animation animation;
        private final int frame;
        private final int nextFrame;
        private final float progress;
        @Nullable
        private final IModelTransform parent;

        public B3DState(@Nullable Animation animation, int frame)
        {
            this(animation, frame, frame, 0);
        }

        public B3DState(@Nullable Animation animation, int frame, IModelTransform parent)
        {
            this(animation, frame, frame, 0, parent);
        }

        public B3DState(@Nullable Animation animation, int frame, int nextFrame, float progress)
        {
            this(animation, frame, nextFrame, progress, null);
        }

        public B3DState(@Nullable Animation animation, int frame, int nextFrame, float progress, @Nullable IModelTransform parent)
        {
            this.animation = animation;
            this.frame = frame;
            this.nextFrame = nextFrame;
            this.progress = MathHelper.clamp(progress, 0, 1);
            this.parent = getParent(parent);
        }

        @Nullable
        private IModelTransform getParent(@Nullable IModelTransform parent)
        {
            if (parent == null) return null;
            else if (parent instanceof B3DState) return ((B3DState)parent).parent;
            return parent;
        }

        @Nullable
        public Animation getAnimation()
        {
            return animation;
        }

        public int getFrame()
        {
            return frame;
        }

        public int getNextFrame()
        {
            return nextFrame;
        }

        public float getProgress()
        {
            return progress;
        }

        @Nullable
        public IModelTransform getParent()
        {
            return parent;
        }


        @Override
        public TransformationMatrix getRotation()
        {
            if(parent != null)
            {
                return parent.getRotation();
            }
            return TransformationMatrix.identity();
        }

        @Override
        public TransformationMatrix getPartTransformation(Object part)
        {
            // TODO make more use of Optional

            if(!(part instanceof NodeJoint))
            {
                return TransformationMatrix.identity();
            }
            Node<?> node = ((NodeJoint)part).getNode();
            TransformationMatrix nodeTransform;
            if(progress < 1e-5 || frame == nextFrame)
            {
                nodeTransform = getNodeMatrix(node, frame);
            }
            else if(progress > 1 - 1e-5)
            {
                nodeTransform = getNodeMatrix(node, nextFrame);
            }
            else
            {
                nodeTransform = getNodeMatrix(node, frame);
                nodeTransform = TransformationHelper.slerp(nodeTransform,getNodeMatrix(node, nextFrame), progress);
            }
            if(parent != null && node.getParent() == null)
            {
                return parent.getPartTransformation(part).compose(nodeTransform);
            }
            return nodeTransform;
        }

        private static LoadingCache<Triple<Animation, Node<?>, Integer>, TransformationMatrix> cache = CacheBuilder.newBuilder()
            .maximumSize(16384)
            .expireAfterAccess(2, TimeUnit.MINUTES)
            .build(new CacheLoader<Triple<Animation, Node<?>, Integer>, TransformationMatrix>()
            {
                @Override
                public TransformationMatrix load(Triple<Animation, Node<?>, Integer> key) throws Exception
                {
                    return getNodeMatrix(key.getLeft(), key.getMiddle(), key.getRight());
                }
            });

        public TransformationMatrix getNodeMatrix(Node<?> node)
        {
            return getNodeMatrix(node, frame);
        }

        public TransformationMatrix getNodeMatrix(Node<?> node, int frame)
        {
            return cache.getUnchecked(Triple.of(animation, node, frame));
        }

        public static TransformationMatrix getNodeMatrix(@Nullable Animation animation, Node<?> node, int frame)
        {
            TransformationMatrix ret = TransformationMatrix.identity();
            Key key = null;
            if(animation != null) key = animation.getKeys().get(frame, node);
            else if(node.getAnimation() != null) key = node.getAnimation().getKeys().get(frame, node);
            if(key != null)
            {
                Node<?> parent = node.getParent();
                if(parent != null)
                {
                    // parent model-global current pose
                    TransformationMatrix pm = cache.getUnchecked(Triple.of(animation, node.getParent(), frame));
                    ret = ret.compose(pm);
                    // joint offset in the parent coords
                    ret = ret.compose(new TransformationMatrix(parent.getPos(), parent.getRot(), parent.getScale(), null));
                }
                // current node local pose
                ret = ret.compose(new TransformationMatrix(key.getPos(), key.getRot(), key.getScale(), null));
                // this part moved inside the model
                // inverse bind of the current node
                /*Matrix4f rm = new TRSRTransformation(node.getPos(), node.getRot(), node.getScale(), null).getMatrix();
                rm.invert();
                ret = ret.compose(new TRSRTransformation(rm));
                if(parent != null)
                {
                    // inverse bind of the parent
                    rm = new TRSRTransformation(parent.getPos(), parent.getRot(), parent.getScale(), null).getMatrix();
                    rm.invert();
                    ret = ret.compose(new TRSRTransformation(rm));
                }*/
                // TODO cache
                TransformationMatrix invBind = new NodeJoint(node).getInvBindPose();
                ret = ret.compose(invBind);
            }
            else
            {
                Node<?> parent = node.getParent();
                if(parent != null)
                {
                    // parent model-global current pose
                    TransformationMatrix pm = cache.getUnchecked(Triple.of(animation, node.getParent(), frame));
                    ret = ret.compose(pm);
                    // joint offset in the parent coords
                    ret = ret.compose(new TransformationMatrix(parent.getPos(), parent.getRot(), parent.getScale(), null));
                }
                ret = ret.compose(new TransformationMatrix(node.getPos(), node.getRot(), node.getScale(), null));
                // TODO cache
                TransformationMatrix invBind = new NodeJoint(node).getInvBindPose();
                ret = ret.compose(invBind);
            }
            return ret;
        }
    }

    static final class NodeJoint implements IJoint
    {
        private final Node<?> node;

        public NodeJoint(Node<?> node)
        {
            this.node = node;
        }

        @Override
        public TransformationMatrix getInvBindPose()
        {
            Matrix4f m = new TransformationMatrix(node.getPos(), node.getRot(), node.getScale(), null).getMatrix();
            m.invert();
            TransformationMatrix pose = new TransformationMatrix(m);

            if(node.getParent() != null)
            {
                TransformationMatrix parent = new NodeJoint(node.getParent()).getInvBindPose();
                pose = pose.compose(parent);
            }
            return pose;
        }

        @Override
        public Optional<NodeJoint> getParent()
        {
            // FIXME cache?
            if(node.getParent() == null) return Optional.empty();
            return Optional.of(new NodeJoint(node.getParent()));
        }

        public Node<?> getNode()
        {
            return node;
        }

        @Override
        public int hashCode()
        {
            return node.hashCode();
        }

        @Override
        public boolean equals(Object obj)
        {
            if (this == obj) return true;
            if (!super.equals(obj)) return false;
            if (getClass() != obj.getClass()) return false;
            NodeJoint other = (NodeJoint) obj;
            return Objects.equal(node, other.node);
        }
    }

    private static final class ModelWrapper implements IUnbakedModel
    {
        private final ResourceLocation modelLocation;
        private final B3DModel model;
        private final ImmutableSet<String> meshes;
        private final ImmutableMap<String, String> textures;
        private final boolean smooth;
        private final boolean gui3d;
        private final boolean isSideLit;
        private final int defaultKey;

        public ModelWrapper(ResourceLocation modelLocation, B3DModel model, ImmutableSet<String> meshes, boolean smooth, boolean gui3d, boolean isSideLit, int defaultKey)
        {
            this(modelLocation, model, meshes, smooth, gui3d, isSideLit, defaultKey, buildTextures(model.getTextures()));
        }

        public ModelWrapper(ResourceLocation modelLocation, B3DModel model, ImmutableSet<String> meshes, boolean smooth, boolean gui3d, boolean isSideLit, int defaultKey, ImmutableMap<String, String> textures)
        {
            this.modelLocation = modelLocation;
            this.model = model;
            this.meshes = meshes;
            this.isSideLit = isSideLit;
            this.textures = textures;
            this.smooth = smooth;
            this.gui3d = gui3d;
            this.defaultKey = defaultKey;
        }

        private static ImmutableMap<String, String> buildTextures(List<Texture> textures)
        {
            ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();

            for(Texture t : textures)
            {
                String path = t.getPath();
                String location = getLocation(path);
                if(!location.startsWith("#")) location = "#" + location;
                builder.put(path, location);
            }
            return builder.build();
        }

        private static String getLocation(String path)
        {
            if(path.endsWith(".png")) path = path.substring(0, path.length() - ".png".length());
            return path;
        }

        @Override
        public Collection<RenderMaterial> getTextures(Function<ResourceLocation, IUnbakedModel> modelGetter, Set<com.mojang.datafixers.util.Pair<String, String>> missingTextureErrors)
        {
            return textures.values().stream().filter(loc -> !loc.startsWith("#"))
                    .map(t -> new RenderMaterial(AtlasTexture.LOCATION_BLOCKS_TEXTURE, new ResourceLocation(t)))
                    .collect(Collectors.toList());
        }

        @Override
        public Collection<ResourceLocation> getDependencies()
        {
            return Collections.emptyList();
        }

        @Nullable
        @Override
        public IBakedModel bakeModel(ModelBakery bakery, Function<RenderMaterial, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform, ResourceLocation modelLocation)
        {
            ImmutableMap.Builder<String, TextureAtlasSprite> builder = ImmutableMap.builder();
            TextureAtlasSprite missing = spriteGetter.apply(new RenderMaterial(AtlasTexture.LOCATION_BLOCKS_TEXTURE, MissingTextureSprite.getLocation()));
            for(Map.Entry<String, String> e : textures.entrySet())
            {
                if(e.getValue().startsWith("#"))
                {
                    LOGGER.fatal("unresolved texture '{}' for b3d model '{}'", e.getValue(), this.modelLocation);
                    builder.put(e.getKey(), missing);
                }
                else
                {
                    builder.put(e.getKey(), spriteGetter.apply(new RenderMaterial(AtlasTexture.LOCATION_BLOCKS_TEXTURE, new ResourceLocation(e.getValue()))));
                }
            }
            builder.put("missingno", missing);
            return new BakedWrapper(model.getRoot(), modelTransform, smooth, gui3d, isSideLit, meshes, builder.build());
        }

        public ModelWrapper retexture(ImmutableMap<String, String> textures)
        {
            ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
            for(Map.Entry<String, String> e : this.textures.entrySet())
            {
                String path = e.getKey();
                String loc = getLocation(path);
                // FIXME: Backward compatibilty: support finding textures that start with #, even though this is not how vanilla works
                if(loc.startsWith("#") && (textures.containsKey(loc) || textures.containsKey(loc.substring(1))))
                {
                    String alt = loc.substring(1);
                    String newLoc = textures.get(loc);
                    if(newLoc == null) newLoc = textures.get(alt);
                    if(newLoc == null) newLoc = path.substring(1);
                    builder.put(e.getKey(), newLoc);
                }
                else
                {
                    builder.put(e);
                }
            }
            return new ModelWrapper(modelLocation, model, meshes, smooth, gui3d, isSideLit, defaultKey, builder.build());
        }

        public ModelWrapper process(ImmutableMap<String, String> data)
        {
            ImmutableSet<String> newMeshes = this.meshes;
            int newDefaultKey = this.defaultKey;
            boolean hasChanged = false;
            if(data.containsKey("mesh"))
            {
                JsonElement e = new JsonParser().parse(data.get("mesh"));
                if(e.isJsonPrimitive() && e.getAsJsonPrimitive().isString())
                {
                    return new ModelWrapper(modelLocation, model, ImmutableSet.of(e.getAsString()), smooth, gui3d, isSideLit, defaultKey, textures);
                }
                else if (e.isJsonArray())
                {
                    ImmutableSet.Builder<String> builder = ImmutableSet.builder();
                    for(JsonElement s : e.getAsJsonArray())
                    {
                        if(s.isJsonPrimitive() && s.getAsJsonPrimitive().isString())
                        {
                            builder.add(s.getAsString());
                        }
                        else
                        {
                            LOGGER.fatal("unknown mesh definition '{}' in array for b3d model '{}'", s.toString(), modelLocation);
                            return this;
                        }
                    }
                    newMeshes = builder.build();
                    hasChanged = true;
                }
                else
                {
                    LOGGER.fatal("unknown mesh definition '{}' for b3d model '{}'", e.toString(), modelLocation);
                    return this;
                }
            }
            if(data.containsKey("key"))
            {
                JsonElement e = new JsonParser().parse(data.get("key"));
                if(e.isJsonPrimitive() && e.getAsJsonPrimitive().isNumber())
                {
                    newDefaultKey = e.getAsNumber().intValue();
                    hasChanged = true;
                }
                else
                {
                    LOGGER.fatal("unknown keyframe definition '{}' for b3d model '{}'", e.toString(), modelLocation);
                    return this;
                }
            }
            return hasChanged ? new ModelWrapper(modelLocation, model, newMeshes, smooth, gui3d, isSideLit, newDefaultKey, textures) : this;
        }

        @Override
        public Optional<IClip> getClip(String name)
        {
            if(name.equals("main"))
            {
                return Optional.of(B3DClip.INSTANCE);
            }
            return Optional.empty();
        }

        public IModelTransform getDefaultState()
        {
            return new B3DState(model.getRoot().getAnimation(), defaultKey, defaultKey, 0);
        }

        public ModelWrapper smoothLighting(boolean value)
        {
            if(value == smooth)
            {
                return this;
            }
            return new ModelWrapper(modelLocation, model, meshes, value, gui3d, isSideLit, defaultKey, textures);
        }

        public ModelWrapper gui3d(boolean value)
        {
            if(value == gui3d)
            {
                return this;
            }
            return new ModelWrapper(modelLocation, model, meshes, smooth, value, isSideLit, defaultKey, textures);
        }
    }

    private static final class BakedWrapper implements IDynamicBakedModel
    {
        private final Node<?> node;
        private final IModelTransform state;
        private final boolean smooth;
        private final boolean gui3d;
        private final boolean isSideLit;
        private final ImmutableSet<String> meshes;
        private final ImmutableMap<String, TextureAtlasSprite> textures;
        private final LoadingCache<Integer, B3DState> cache;

        private ImmutableList<BakedQuad> quads;

        public BakedWrapper(final Node<?> node, final IModelTransform state, final boolean smooth, final boolean gui3d, boolean isSideLit, final ImmutableSet<String> meshes, final ImmutableMap<String, TextureAtlasSprite> textures)
        {
            this(node, state, smooth, gui3d, isSideLit, meshes, textures, CacheBuilder.newBuilder()
                .maximumSize(128)
                .expireAfterAccess(2, TimeUnit.MINUTES)
                .build(new CacheLoader<Integer, B3DState>()
                {
                    @Override
                    public B3DState load(Integer frame) throws Exception
                    {
                        IModelTransform parent = state;
                        Animation newAnimation = node.getAnimation();
                        if(parent instanceof B3DState)
                        {
                            B3DState ps = (B3DState)parent;
                            parent = ps.getParent();
                        }
                        return new B3DState(newAnimation, frame, frame, 0, parent);
                    }
                }));
        }

        public BakedWrapper(Node<?> node, IModelTransform state, boolean smooth, boolean gui3d, boolean isSideLit, ImmutableSet<String> meshes, ImmutableMap<String, TextureAtlasSprite> textures, LoadingCache<Integer, B3DState> cache)
        {
            this.node = node;
            this.state = state;
            this.smooth = smooth;
            this.gui3d = gui3d;
            this.isSideLit = isSideLit;
            this.meshes = meshes;
            this.textures = textures;
            this.cache = cache;
        }

        @Override
        public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand, IModelData data)
        {
            if(side != null) return ImmutableList.of();
            IModelTransform modelState = this.state;
            IModelTransform newState = data.getData(Properties.AnimationProperty);
            if(newState != null)
            {
                // FIXME: should animation state handle the parent state, or should it remain here?
                IModelTransform parent = this.state;
                if(parent instanceof B3DState)
                {
                    B3DState ps = (B3DState)parent;
                    parent = ps.getParent();
                }
                if (parent == null)
                {
                    modelState = newState;
                }
                else
                {
                    modelState = new ModelTransformComposition(parent, newState);
                }
            }
            if(quads == null)
            {
                ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();
                generateQuads(builder, node, this.state, ImmutableList.of());
                quads = builder.build();
            }
            // TODO: caching?
            if(this.state != modelState)
            {
                ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();
                generateQuads(builder, node, modelState, ImmutableList.of());
                return builder.build();
            }
            return quads;
        }

        private void generateQuads(ImmutableList.Builder<BakedQuad> builder, Node<?> node, final IModelTransform state, ImmutableList<String> path)
        {
            ImmutableList.Builder<String> pathBuilder = ImmutableList.builder();
            pathBuilder.addAll(path);
            pathBuilder.add(node.getName());
            ImmutableList<String> newPath = pathBuilder.build();
            for(Node<?> child : node.getNodes().values())
            {
                generateQuads(builder, child, state, newPath);
            }
            if(node.getKind() instanceof Mesh && meshes.contains(node.getName()) && state.getPartTransformation(Models.getHiddenModelPart(newPath)).isIdentity())
            {
                Mesh mesh = (Mesh)node.getKind();
                Collection<Face> faces = mesh.bake(new Function<Node<?>, Matrix4f>()
                {
                    private final TransformationMatrix global = state.getRotation();
                    private final LoadingCache<Node<?>, TransformationMatrix> localCache = CacheBuilder.newBuilder()
                        .maximumSize(32)
                        .build(new CacheLoader<Node<?>, TransformationMatrix>()
                        {
                            @Override
                            public TransformationMatrix load(Node<?> node) throws Exception
                            {
                                return state.getPartTransformation(new NodeJoint(node));
                            }
                        });

                    @Override
                    public Matrix4f apply(Node<?> node)
                    {
                        return global.compose(localCache.getUnchecked(node)).getMatrix();
                    }
                });
                for(Face f : faces)
                {
                    List<Texture> textures = null;
                    if(f.getBrush() != null) textures = f.getBrush().getTextures();
                    TextureAtlasSprite sprite;
                    if(textures == null || textures.isEmpty()) sprite = this.textures.get("missingno");
                    else if(textures.get(0) == B3DModel.Texture.White) sprite = ModelLoader.White.instance();
                    else sprite = this.textures.get(textures.get(0).getPath());
                    BakedQuadBuilder quadBuilder = new BakedQuadBuilder(sprite);
                    quadBuilder.setContractUVs(true);
                    quadBuilder.setQuadOrientation(Direction.getFacingFromVector(f.getNormal().getX(), f.getNormal().getY(), f.getNormal().getZ()));
                    putVertexData(quadBuilder, f.getV1(), f.getNormal(), sprite);
                    putVertexData(quadBuilder, f.getV2(), f.getNormal(), sprite);
                    putVertexData(quadBuilder, f.getV3(), f.getNormal(), sprite);
                    putVertexData(quadBuilder, f.getV3(), f.getNormal(), sprite);
                    builder.add(quadBuilder.build());
                }
            }
        }

        private final void putVertexData(IVertexConsumer consumer, Vertex v, Vector3f faceNormal, TextureAtlasSprite sprite)
        {
            // TODO handle everything not handled (texture transformations, bones, transformations, normals, e.t.c)
            ImmutableList<VertexFormatElement> vertexFormatElements = consumer.getVertexFormat().getElements();
            for(int e = 0; e < vertexFormatElements.size(); e++)
            {
                switch(vertexFormatElements.get(e).getUsage())
                {
                case POSITION:
                    consumer.put(e, v.getPos().getX(), v.getPos().getY(), v.getPos().getZ(), 1);
                    break;
                case COLOR:
                    if(v.getColor() != null)
                    {
                        consumer.put(e, v.getColor().getX(), v.getColor().getY(), v.getColor().getZ(), v.getColor().getW());
                    }
                    else
                    {
                        consumer.put(e, 1, 1, 1, 1);
                    }
                    break;
                case UV:
                    // TODO handle more brushes
                    if(vertexFormatElements.get(e).getIndex() < v.getTexCoords().length)
                    {
                        consumer.put(e,
                            sprite.getInterpolatedU(v.getTexCoords()[0].getX() * 16),
                            sprite.getInterpolatedV(v.getTexCoords()[0].getY() * 16),
                            0,
                            1
                        );
                    }
                    else
                    {
                        consumer.put(e, 0, 0, 0, 1);
                    }
                    break;
                case NORMAL:
                    if(v.getNormal() != null)
                    {
                        consumer.put(e, v.getNormal().getX(), v.getNormal().getY(), v.getNormal().getZ(), 0);
                    }
                    else
                    {
                        consumer.put(e, faceNormal.getX(), faceNormal.getY(), faceNormal.getZ(), 0);
                    }
                    break;
                default:
                    consumer.put(e);
                }
            }
        }

        @Override
        public boolean isAmbientOcclusion()
        {
            return smooth;
        }

        @Override
        public boolean isGui3d()
        {
            return gui3d;
        }

        @Override
        public boolean func_230044_c_()
        {
            return isSideLit;
        }

        @Override
        public boolean isBuiltInRenderer()
        {
            return false;
        }

        @Override
        public TextureAtlasSprite getParticleTexture()
        {
            // FIXME somehow specify particle texture in the model
            return textures.values().asList().get(0);
        }

        @Override
        public boolean doesHandlePerspectives()
        {
            return true;
        }

        @Override
        public IBakedModel handlePerspective(TransformType cameraTransformType, MatrixStack mat)
        {
            return PerspectiveMapWrapper.handlePerspective(this, state, cameraTransformType, mat);
        }

        @Override
        public ItemOverrideList getOverrides()
        {
            // TODO handle items
            return ItemOverrideList.EMPTY;
        }
    }
}
