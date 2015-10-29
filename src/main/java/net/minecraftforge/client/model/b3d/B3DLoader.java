package net.minecraftforge.client.model.b3d;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.IModelCustomData;
import net.minecraftforge.client.model.IModelPart;
import net.minecraftforge.client.model.IModelState;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.client.model.IPerspectiveState;
import net.minecraftforge.client.model.IRetexturableModel;
import net.minecraftforge.client.model.ISmartBlockModel;
import net.minecraftforge.client.model.ISmartItemModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.TRSRTransformation;
import net.minecraftforge.client.model.b3d.B3DModel.Animation;
import net.minecraftforge.client.model.b3d.B3DModel.Face;
import net.minecraftforge.client.model.b3d.B3DModel.IKind;
import net.minecraftforge.client.model.b3d.B3DModel.Key;
import net.minecraftforge.client.model.b3d.B3DModel.Mesh;
import net.minecraftforge.client.model.b3d.B3DModel.Node;
import net.minecraftforge.client.model.b3d.B3DModel.Texture;
import net.minecraftforge.client.model.b3d.B3DModel.Vertex;
import net.minecraftforge.client.model.pipeline.LightUtil;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.common.FMLLog;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/*
 * Loader for Blitz3D models.
 * To enable for your mod call instance.addDomain(modid).
 * If you need more control over accepted resources - extend the class, and register a new instance with ModelLoaderRegistry.
 */
public class B3DLoader implements ICustomModelLoader
{
    public static final B3DLoader instance = new B3DLoader();

    private IResourceManager manager;

    private final Set<String> enabledDomains = new HashSet<String>();
    private final Map<ResourceLocation, B3DModel> cache = new HashMap<ResourceLocation, B3DModel>();

    public void addDomain(String domain)
    {
        enabledDomains.add(domain.toLowerCase());
    }

    public void onResourceManagerReload(IResourceManager manager)
    {
        this.manager = manager;
        cache.clear();
    }

    public boolean accepts(ResourceLocation modelLocation)
    {
        return enabledDomains.contains(modelLocation.getResourceDomain()) && modelLocation.getResourcePath().endsWith(".b3d");
    }

    @SuppressWarnings("unchecked")
    public IModel loadModel(ResourceLocation modelLocation) throws IOException
    {
        ResourceLocation file = new ResourceLocation(modelLocation.getResourceDomain(), modelLocation.getResourcePath());
        if(!cache.containsKey(file))
        {
            try
            {
                IResource resource = null;
                try
                {
                    resource = manager.getResource(file);
                }
                catch(FileNotFoundException e)
                {
                    if(modelLocation.getResourcePath().startsWith("models/block/"))
                        resource = manager.getResource(new ResourceLocation(file.getResourceDomain(), "models/item/" + file.getResourcePath().substring("models/block/".length())));
                    else if(modelLocation.getResourcePath().startsWith("models/item/"))
                        resource = manager.getResource(new ResourceLocation(file.getResourceDomain(), "models/block/" + file.getResourcePath().substring("models/item/".length())));
                    else throw e;
                }
                B3DModel.Parser parser = new B3DModel.Parser(resource.getInputStream());
                B3DModel model = parser.parse();
                cache.put(file, model);
            }
            catch(IOException e)
            {
                //FMLLog.log(Level.ERROR, e, "Exception loading model %s with B3D loader, skipping", modelLocation);
                cache.put(file, null);
                throw e;
            }
        }
        B3DModel model = cache.get(file);
        if(model == null) return ModelLoaderRegistry.getMissingModel();
        if(modelLocation instanceof B3DMeshLocation)
        {
            String mesh = ((B3DMeshLocation)modelLocation).getMesh();
            if(!model.getMeshes().containsKey(mesh))
            {
                FMLLog.severe("No mesh named %s in model %s, skipping", mesh, modelLocation);
                return ModelLoaderRegistry.getMissingModel();
            }
            return new Wrapper(modelLocation, model.getTextures(), model.getMeshes().get(mesh));
        }
        if(!(model.getRoot().getKind() instanceof Mesh))
        {
            FMLLog.severe("No root mesh in model %s and no mesh name in location, skipping", modelLocation);
            return ModelLoaderRegistry.getMissingModel();
        }
        return new Wrapper(modelLocation, model.getTextures(), (Node<Mesh>)model.getRoot());
    }

    public static class B3DMeshLocation extends ResourceLocation
    {
        public final String mesh;

        public B3DMeshLocation(String domain, String path, String mesh)
        {
            super(domain, path);
            this.mesh = mesh;
        }

        public String getMesh()
        {
            return mesh;
        }

        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = super.hashCode();
            result = prime * result + ((mesh == null) ? 0 : mesh.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (this == obj) return true;
            if (!super.equals(obj)) return false;
            if (getClass() != obj.getClass()) return false;
            B3DMeshLocation other = (B3DMeshLocation) obj;
            if (mesh == null)
            {
                if (other.mesh != null) return false;
            }
            else if (!mesh.equals(other.mesh)) return false;
            return true;
        }
    }

    public static class B3DState implements IModelState
    {
        private final Animation animation;
        private final int frame;
        private final IModelState parent;

        public B3DState(Animation animation, int frame)
        {
            this(animation, frame, null);
        }

        public B3DState(Animation animation, int frame, IModelState parent)
        {
            this.animation = animation;
            this.frame = frame;
            this.parent = getParent(parent);
        }

        private IModelState getParent(IModelState parent)
        {
            if (parent == null) return null;
            else if (parent instanceof B3DState) return ((B3DState)parent).parent;
            return parent;
        }

        public Animation getAnimation()
        {
            return animation;
        }

        public int getFrame()
        {
            return frame;
        }

        public TRSRTransformation apply(IModelPart part)
        {
            if(!(part instanceof PartWrapper<?>))
            {
                throw new IllegalArgumentException("B3DState can only be applied to b3d models");
            }
            if(parent != null)
            {
                return parent.apply(part).compose(getNodeMatrix(((PartWrapper<?>)part).getNode()));
            }
            return getNodeMatrix(((PartWrapper<?>)part).getNode());
        }

        private static LoadingCache<Triple<Animation, Node<?>, Integer>, TRSRTransformation> cache = CacheBuilder.newBuilder()
            .maximumSize(16384)
            .expireAfterAccess(2, TimeUnit.MINUTES)
            .build(new CacheLoader<Triple<Animation, Node<?>, Integer>, TRSRTransformation>()
            {
                public TRSRTransformation load(Triple<Animation, Node<?>, Integer> key) throws Exception
                {
                    return getNodeMatrix(key.getLeft(), key.getMiddle(), key.getRight());
                }
            });

        public TRSRTransformation getNodeMatrix(Node<?> node)
        {
            return cache.getUnchecked(Triple.<Animation, Node<?>, Integer>of(animation, node, frame));
        }

        public static TRSRTransformation getNodeMatrix(Animation animation, Node<?> node, int frame)
        {
            TRSRTransformation ret = TRSRTransformation.identity();
            Key key = null;
            if(animation != null) key = animation.getKeys().get(frame, node);
            else if(key == null && node.getAnimation() != null && node.getAnimation() != animation) key = node.getAnimation().getKeys().get(frame, node);
            if(key != null)
            {
                Node<?> parent = node.getParent();
                if(parent != null)
                {
                    TRSRTransformation pm = cache.getUnchecked(Triple.<Animation, Node<?>, Integer>of(animation, node.getParent(), frame));
                    ret = ret.compose(pm);
                    ret = ret.compose(new TRSRTransformation(parent.getPos(), parent.getRot(), parent.getScale(), null));
                }
                ret = ret.compose(new TRSRTransformation(key.getPos(), key.getRot(), key.getScale(), null));
                Matrix4f rm = new TRSRTransformation(node.getPos(), node.getRot(), node.getScale(), null).getMatrix();
                rm.invert();
                ret = ret.compose(new TRSRTransformation(rm));
                if(parent != null)
                {
                    rm = new TRSRTransformation(parent.getPos(), parent.getRot(), parent.getScale(), null).getMatrix();
                    rm.invert();
                    ret = ret.compose(new TRSRTransformation(rm));
                }
            }
            return ret;
        }
    }

    public static enum B3DFrameProperty implements IUnlistedProperty<B3DState>
    {
        instance;
        public String getName()
        {
            return "B3DFrame";
        }

        public boolean isValid(B3DState value)
        {
            return value instanceof B3DState;
        }

        public Class<B3DState> getType()
        {
            return B3DState.class;
        }

        public String valueToString(B3DState value)
        {
            return value.toString();
        }
    }

    public static class PartWrapper<K extends IKind<K>> implements IModelPart
    {
        private final Node<K> node;

        public static <K extends IKind<K>> PartWrapper<K> create(Node<K> node)
        {
            return new PartWrapper<K>(node);
        }

        public PartWrapper(Node<K> node)
        {
            this.node = node;
        }

        public Node<K> getNode()
        {
            return node;
        }
    }

    public static class Wrapper extends PartWrapper<Mesh> implements IRetexturableModel, IModelCustomData
    {
        private final ResourceLocation location;
        private final ImmutableMap<String, ResourceLocation> textures;

        public Wrapper(ResourceLocation location, List<Texture> textures, B3DModel.Node<Mesh> mesh)
        {
            this(location, buildTextures(textures), mesh);
        }

        public Wrapper(ResourceLocation location, ImmutableMap<String, ResourceLocation> textures, B3DModel.Node<Mesh> mesh)
        {
            super(mesh);
            this.location = location;
            this.textures = textures;
        }

        private static ImmutableMap<String, ResourceLocation> buildTextures(List<Texture> textures)
        {
            ImmutableMap.Builder<String, ResourceLocation> builder = ImmutableMap.builder();

            for(Texture t : textures)
            {
                String path = t.getPath();
                String location = getLocation(path);
                if(!location.startsWith("#")) location = "#" + location;
                builder.put(path, new ResourceLocation(location));
            }
            return builder.build();
        }

        private static String getLocation(String path)
        {
            if(path.endsWith(".png")) path = path.substring(0, path.length() - ".png".length());
            return path;
        }

        public Collection<ResourceLocation> getDependencies()
        {
            // no dependencies for in-file models
            // FIXME maybe add child meshes
            return Collections.emptyList();
        }

        public Collection<ResourceLocation> getTextures()
        {
            return Collections2.filter(textures.values(), new Predicate<ResourceLocation>()
            {
                public boolean apply(ResourceLocation loc)
                {
                    return !loc.getResourcePath().startsWith("#");
                }
            });
        }

        public IFlexibleBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter)
        {
            ImmutableMap.Builder<String, TextureAtlasSprite> builder = ImmutableMap.builder();
            TextureAtlasSprite missing = bakedTextureGetter.apply(new ResourceLocation("missingno"));
            for(Map.Entry<String, ResourceLocation> e : textures.entrySet())
            {
                if(e.getValue().getResourcePath().startsWith("#"))
                {
                    FMLLog.severe("unresolved texture '%s' for b3d model '%s'", e.getValue().getResourcePath(), location);
                    builder.put(e.getKey(), missing);
                }
                else
                {
                    builder.put(e.getKey(), bakedTextureGetter.apply(e.getValue()));
                }
            }
            builder.put("missingno", missing);
            return new BakedWrapper(this, state, format, builder.build());
        }

        public B3DState getDefaultState()
        {
            return new B3DState(getNode().getAnimation(), 1);
        }

        public ResourceLocation getLocation()
        {
            return location;
        }

        public ImmutableMap<String, ResourceLocation> getTextureMap()
        {
            return textures;
        }

        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((location == null) ? 0 : location.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            Wrapper other = (Wrapper) obj;
            if (location == null)
            {
                if (other.location != null) return false;
            }
            else if (!location.equals(other.location)) return false;
            return true;
        }

        @Override
        public IModel retexture(ImmutableMap<String, String> textures)
        {
            ImmutableMap.Builder<String, ResourceLocation> builder = ImmutableMap.builder();
            for(Map.Entry<String, ResourceLocation> e : this.textures.entrySet())
            {
                String path = e.getKey();
                String loc = getLocation(path);
                if(textures.containsKey(loc))
                {
                    String newLoc = textures.get(loc);
                    if(newLoc == null) newLoc = getLocation(path);
                    builder.put(e.getKey(), new ResourceLocation(newLoc));
                }
                else
                {
                    builder.put(e);
                }
            }
            return new Wrapper(location, builder.build(), getNode());
        }

        @Override
        public IModel process(ImmutableMap<String, String> customData)
        {
            // TODO keyframe
            return null;
        }
    }

    private static class BakedWrapper implements IFlexibleBakedModel, ISmartBlockModel, ISmartItemModel, IPerspectiveAwareModel
    {
        private final B3DLoader.Wrapper model;
        private final IModelState state;
        private final VertexFormat format;
        private final ImmutableMap<String, TextureAtlasSprite> textures;

        private ImmutableList<BakedQuad> quads;

        private static final int BYTES_IN_INT = Integer.SIZE / Byte.SIZE;
        private static final int VERTICES_IN_QUAD = 4;

        public BakedWrapper(B3DLoader.Wrapper model, IModelState state, VertexFormat format, ImmutableMap<String, TextureAtlasSprite> textures)
        {
            this.model = model;
            this.state = state;
            this.format = format;
            this.textures = textures;
        }

        public List<BakedQuad> getFaceQuads(EnumFacing side)
        {
            return Collections.emptyList();
        }

        @SuppressWarnings("unchecked")
        public List<BakedQuad> getGeneralQuads()
        {
            if(quads == null)
            {
                Node<Mesh> mesh = model.getNode();
                ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();
                for(Node<?> child : mesh.getNodes().values())
                {
                    if(child.getKind() instanceof Mesh)
                    {
                        Node<Mesh> childMesh = (Node<Mesh>)child;
                        builder.addAll(new BakedWrapper(new B3DLoader.Wrapper(model.getLocation(), model.getTextureMap(), childMesh), state, format, textures).getGeneralQuads());
                    }
                }
                mesh.getKind().getWeightMap();
                Collection<Face> faces = mesh.getKind().getFaces();
                faces = mesh.getKind().bake(new Function<Node<?>, Matrix4f>()
                {
                    // gets transformation in global space
                    public Matrix4f apply(Node<?> node)
                    {
                        return state.apply(PartWrapper.create(node)).getMatrix();
                    }
                });
                for(Face f : faces)
                {
                    UnpackedBakedQuad.Builder quadBuilder = new UnpackedBakedQuad.Builder(format);
                    quadBuilder.setQuadOrientation(EnumFacing.getFacingFromVector(f.getNormal().x, f.getNormal().y, f.getNormal().z));
                    quadBuilder.setQuadColored();
                    List<Texture> textures = null;
                    if(f.getBrush() != null) textures = f.getBrush().getTextures();
                    TextureAtlasSprite sprite;
                    if(textures == null || textures.isEmpty()) sprite = this.textures.get("missingno");
                    else if(textures.get(0) == B3DModel.Texture.White) sprite = ModelLoader.White.instance;
                    else sprite = this.textures.get(textures.get(0).getPath());
                    putVertexData(quadBuilder, f.getV1(), f.getNormal(), sprite);
                    putVertexData(quadBuilder, f.getV2(), f.getNormal(), sprite);
                    putVertexData(quadBuilder, f.getV3(), f.getNormal(), sprite);
                    putVertexData(quadBuilder, f.getV3(), f.getNormal(), sprite);
                    builder.add(quadBuilder.build());
                }
                quads = builder.build();
            }
            return quads;
        }

        private final void putVertexData(UnpackedBakedQuad.Builder builder, Vertex v, Vector3f faceNormal, TextureAtlasSprite sprite)
        {
            // TODO handle everything not handled (texture transformations, bones, transformations, normals, e.t.c)
            for(int e = 0; e < format.getElementCount(); e++)
            {
                switch(format.getElement(e).getUsage())
                {
                case POSITION:
                    builder.put(e, v.getPos().x, v.getPos().y, v.getPos().z, 1);
                    break;
                case COLOR:
                    float d = LightUtil.diffuseLight(faceNormal.x, faceNormal.y, faceNormal.z);
                    if(v.getColor() != null)
                    {
                        builder.put(e, d * v.getColor().x, d * v.getColor().y, d * v.getColor().z, v.getColor().w);
                    }
                    else
                    {
                        builder.put(e, d, d, d, 1);
                    }
                    break;
                case UV:
                    // TODO handle more brushes
                    if(format.getElement(e).getIndex() < v.getTexCoords().length)
                    {
                        builder.put(e,
                            sprite.getInterpolatedU(v.getTexCoords()[0].x * 16),
                            sprite.getInterpolatedV(v.getTexCoords()[0].y * 16),
                            0,
                            1
                        );
                    }
                    else
                    {
                        builder.put(e, 0, 0, 0, 1);
                    }
                    break;
                case NORMAL:
                    if(v.getNormal() != null)
                    {
                        builder.put(e, v.getNormal().x, v.getNormal().y, v.getNormal().z, 0);
                    }
                    else
                    {
                        builder.put(e, faceNormal.x, faceNormal.y, faceNormal.z, 0);
                    }
                    break;
                default:
                    builder.put(e);
                }
            }
        }

        public boolean isAmbientOcclusion()
        {
            return true;
        }

        public boolean isGui3d()
        {
            return true;
        }

        public boolean isBuiltInRenderer()
        {
            return false;
        }

        public TextureAtlasSprite getTexture()
        {
            // FIXME somehow specify particle texture in the model
            return textures.values().asList().get(0);
        }

        public ItemCameraTransforms getItemCameraTransforms()
        {
            return ItemCameraTransforms.DEFAULT;
        }

        @Override
        public BakedWrapper handleBlockState(IBlockState state)
        {
            if(state instanceof IExtendedBlockState)
            {
                IExtendedBlockState exState = (IExtendedBlockState)state;
                if(exState.getUnlistedNames().contains(B3DFrameProperty.instance))
                {
                    B3DState s = exState.getValue(B3DFrameProperty.instance);
                    if(s != null)
                    {
                        return getCachedModel(s.getFrame());
                    }
                }
            }
            return this;
        }

        private final Map<Integer, BakedWrapper> cache = new HashMap<Integer, BakedWrapper>();

        public BakedWrapper getCachedModel(int frame)
        {
            if(!cache.containsKey(frame))
            {
                cache.put(frame, new BakedWrapper(model, new B3DState(model.getNode().getAnimation(), frame, state), format, textures));
            }
            return cache.get(frame);
        }

        public VertexFormat getFormat()
        {
            return format;
        }

        public BakedWrapper handleItemState(ItemStack stack)
        {
            // TODO specify how to get B3DState from ItemStack
            return this;
        }

        public Pair<IBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType)
        {
            if(state instanceof IPerspectiveState)
            {
                return Pair.of((IBakedModel)this, TRSRTransformation.blockCornerToCenter(((IPerspectiveState)state).forPerspective(cameraTransformType).apply(model)).getMatrix());
            }
            return Pair.of((IBakedModel)this, null);
        }
    }
}
