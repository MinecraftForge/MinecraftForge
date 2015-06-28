package net.minecraftforge.client.model;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BreakingFour;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.vecmath.Matrix4f;

public class ModelDynBucket implements IModel {
  public static final ModelResourceLocation LOCATION = new ModelResourceLocation(new ResourceLocation("forge", "dynbucket"), "inventory");
    
  protected final IModel bucket;
  protected static final ResourceLocation interior = new ResourceLocation("forge", "items/bucket_interior");
  protected static final ResourceLocation front = new ResourceLocation("forge", "items/bucket_front");

  public ModelDynBucket(IModel bucket) {
    this.bucket = bucket;
  }

  @Override
  public Collection<ResourceLocation> getDependencies() {
    return bucket.getDependencies();
  }

  @Override
  public Collection<ResourceLocation> getTextures() {
    ImmutableSet.Builder<ResourceLocation> builder = ImmutableSet.builder();

    builder.add(interior);
    builder.add(front);

    return builder.build();
  }

  @Override
  public IFlexibleBakedModel bake(IModelState state, VertexFormat format,
                                  Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
    // bucket model
    IFlexibleBakedModel bakedBucket = bucket.bake(bucket.getDefaultState(), format, bakedTextureGetter);

    return new BakedDynBucket(bakedBucket, FluidRegistry.WATER);
  }

  @Override
  public IModelState getDefaultState() {
    return bucket.getDefaultState();
  }

  public enum LoaderDynBucket implements ICustomModelLoader {
    instance;

    @Override
    public boolean accepts(ResourceLocation modelLocation) {
      return modelLocation.getResourceDomain().equals("forge") && modelLocation.getResourcePath().equals("models/item/dynbucket");
    }

    @Override
    public IModel loadModel(ResourceLocation modelLocation) throws IOException {
      IModel bucketModel = ModelLoaderRegistry.getModel(new ResourceLocation("minecraft", "item/bucket"));

      return new ModelDynBucket(bucketModel);
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {}
  }

  // the dynamic bucket is based on the empty bucket
  protected static class BakedDynBucket implements IFlexibleBakedModel, ISmartItemModel, IPerspectiveAwareModel {

    private final IFlexibleBakedModel vanillaBakedBucket;
    private final Map<String, IFlexibleBakedModel> cache = Maps.newHashMap(); // contains all the baked models since they'll never change

    private final List<BakedQuad> generalQuads;

    public BakedDynBucket(IFlexibleBakedModel bakedBucket, Fluid fluid) {
      this.vanillaBakedBucket = bakedBucket;

      TextureAtlasSprite interiorSprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(interior.toString());
      TextureAtlasSprite frontSprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(front.toString());

      this.generalQuads = bakeModel(interiorSprite, frontSprite, fluid);
    }

    @Override
    public IBakedModel handleItemState(ItemStack stack) {
      FluidStack fluidStack = FluidContainerRegistry.getFluidForFilledItem(stack);
      if(fluidStack == null) {
        return this;
      }
      Fluid fluid = fluidStack.getFluid();
      String name = fluid.getName();

      cache.clear();
      
      if(!cache.containsKey(name)) {
        cache.put(name, new BakedDynBucket(vanillaBakedBucket, fluidStack.getFluid()));
      }

      return cache.get(name);
    }

    private List<BakedQuad> bakeModel(TextureAtlasSprite interior, TextureAtlasSprite front, Fluid fluid) {
      ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();

      // back
      builder.addAll(vanillaBakedBucket.getGeneralQuads());

      TextureAtlasSprite fluidSprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(fluid.getStill().toString());

      builder.addAll(ModelHelper.convertTexture(interior, fluidSprite, fluid.getColor()));

      // add the front cover
      builder.add(ModelHelper.genQuad(0, 0, 16, 16, front, 0.03f, EnumFacing.NORTH, 0xffffffff));
      builder.add(ModelHelper.genQuad(0, 0, 16, 16, front, 0.03f, EnumFacing.SOUTH, 0xffffffff));

      return builder.build();
    }

    @Override
    public List<BakedQuad> getFaceQuads(EnumFacing side) {
      return vanillaBakedBucket.getFaceQuads(side);
    }

    @Override
    public List<BakedQuad> getGeneralQuads() {
      return generalQuads;
    }

    @Override
    public boolean isAmbientOcclusion() {
      return vanillaBakedBucket.isAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
      return vanillaBakedBucket.isGui3d();
    }

    @Override
    public boolean isBuiltInRenderer() {
      return vanillaBakedBucket.isBuiltInRenderer();
    }

    @Override
    public TextureAtlasSprite getTexture() {
      return vanillaBakedBucket.getTexture();
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
      return vanillaBakedBucket.getItemCameraTransforms();
    }

    @Override
    public VertexFormat getFormat() {
      return vanillaBakedBucket.getFormat();
    }

    @Override
    public Pair<IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {
      Matrix4f matrix4f = ((IPerspectiveAwareModel) vanillaBakedBucket).handlePerspective(cameraTransformType).getRight();
      return Pair.of((IBakedModel)this, matrix4f);
    }
  }
}
