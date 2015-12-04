package net.minecraftforge.client.model;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

public class ModelDynBucket implements IModel, IModelCustomData, IRetexturableModel {
  public static final ModelResourceLocation LOCATION = new ModelResourceLocation(new ResourceLocation("forge", "dynbucket"), "inventory");

  // minimal Z offset to prevent depth-fighting
  private static final float NORTH_Z_BASE  = 7.496f / 16f;
  private static final float SOUTH_Z_BASE  = 8.504f / 16f;
  private static final float NORTH_Z_FLUID = 7.498f / 16f;
  private static final float SOUTH_Z_FLUID = 8.502f / 16f;

  public static final IModel MODEL = new ModelDynBucket();

  protected final ResourceLocation baseLocation;
  protected final ResourceLocation liquidLocation;
  protected final ResourceLocation coverLocation;

  protected final Fluid fluid;
  protected final boolean flipGas;

  public ModelDynBucket() {
    this(null, null, null, FluidRegistry.WATER, false);
  }

  public ModelDynBucket(ResourceLocation baseLocation, ResourceLocation liquidLocation, ResourceLocation coverLocation, Fluid fluid, boolean flipGas) {
    this.baseLocation = baseLocation;
    this.liquidLocation = liquidLocation;
    this.coverLocation = coverLocation;
    this.fluid = fluid;
    this.flipGas = flipGas;
  }

  @Override
  public Collection<ResourceLocation> getDependencies() {
    return ImmutableList.of();
  }

  @Override
  public Collection<ResourceLocation> getTextures() {
    ImmutableSet.Builder<ResourceLocation> builder = ImmutableSet.builder();
    if(baseLocation != null)
      builder.add(baseLocation);
    if(liquidLocation != null)
      builder.add(liquidLocation);
    if(coverLocation != null)
      builder.add(coverLocation);

    return builder.build();
  }

  @Override
  public IFlexibleBakedModel bake(IModelState state, VertexFormat format,
                                  Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {

    // if the fluid is a gas wi manipulate the initial state to be rotated 180° to turn it upside down
    if(flipGas && fluid.isGaseous()) {
      ImmutableMap.Builder<ItemCameraTransforms.TransformType, IModelState> map = ImmutableMap.builder();
      TRSRTransformation flip = TRSRTransformation.blockCenterToCorner(new TRSRTransformation(null, null, null, TRSRTransformation.quatFromYXZ(0f, 0f, (float)Math.PI)));

      if(state instanceof IPerspectiveState) {
        IPerspectiveState ps = (IPerspectiveState) state;

        for(ItemCameraTransforms.TransformType type : ItemCameraTransforms.TransformType.values())
          map.put(type, ps.forPerspective(type).apply(this).compose(flip));
      }
      else {
        for(ItemCameraTransforms.TransformType type : ItemCameraTransforms.TransformType.values())
          map.put(type, flip);
      }
      state = new IPerspectiveState.Impl(state, map.build());
    }

    TRSRTransformation transform = state.apply(this);
    TextureAtlasSprite fluidSprite = bakedTextureGetter.apply(fluid.getStill());
    ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();

    if(baseLocation != null) {
      // build base (insidest)
      IFlexibleBakedModel model = (new ItemLayerModel(ImmutableList.of(baseLocation))).bake(state, format, bakedTextureGetter);
      builder.addAll(model.getGeneralQuads());
    }
    if(liquidLocation != null) {
      TextureAtlasSprite liquid = bakedTextureGetter.apply(liquidLocation);
      // build liquid layer (inside)
      builder.addAll(ItemTextureQuadConverter
                         .convertTexture(format, transform, liquid, fluidSprite, NORTH_Z_FLUID, EnumFacing.NORTH, fluid.getColor()));
      builder.addAll(ItemTextureQuadConverter
                         .convertTexture(format, transform, liquid, fluidSprite, SOUTH_Z_FLUID, EnumFacing.SOUTH, fluid.getColor()));
    }
    if(coverLocation != null) {
      // cover (the actual item around the other two)
      TextureAtlasSprite base = bakedTextureGetter.apply(coverLocation);
      builder.add(ItemTextureQuadConverter
                      .genQuad(format, transform, 0, 0, 16, 16, NORTH_Z_BASE, base, EnumFacing.NORTH, 0xffffffff));
      builder.add(ItemTextureQuadConverter
                      .genQuad(format, transform, 0, 0, 16, 16, SOUTH_Z_BASE, base, EnumFacing.SOUTH, 0xffffffff));
    }

    if(state instanceof IPerspectiveState)
    {
      IPerspectiveState ps = (IPerspectiveState)state;
      ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> map = IPerspectiveAwareModel.MapWrapper.getTransforms(ps, this);
      return new BakedDynBucket(this, builder.build(), fluidSprite, format, Maps.immutableEnumMap(map), Maps.<String, IFlexibleBakedModel>newHashMap());
    }
    return new BakedDynBucket(this, builder.build(), fluidSprite, format, ImmutableMap.<ItemCameraTransforms.TransformType, TRSRTransformation>of(), Maps.<String, IFlexibleBakedModel>newHashMap());
  }

  @Override
  public IModelState getDefaultState() {
    return TRSRTransformation.identity();
  }

  /**
   * Sets the liquid in the model.
   * fluid - Name of the fluid in the FluidRegistry
   * flipGas - If "true" the model will be flipped upside down if the liquid is a gas. If "false" it wont
   *
   * If the fluid can't be found, water is used
   */
  @Override
  public IModel process(ImmutableMap<String, String> customData) {
    String fluidName = customData.get("fluid");
    Fluid fluid = FluidRegistry.getFluid(fluidName);

    if(fluid == null) fluid = this.fluid;

    boolean flip = flipGas;
    if(customData.containsKey("flipGas")) {
      String flipStr = customData.get("flipGas");
      if(flipStr.equals("true")) flip = true;
      else if(flipStr.equals("false")) flip = false;
      else throw new IllegalArgumentException(String.format("DynBucket custom data \"flipGas\" must have value \'true\' or \'false\' (was \'%s\')", flipStr));
    }

    // create new model with correct liquid
    return new ModelDynBucket(baseLocation, liquidLocation, coverLocation, fluid, flip);
  }

  /**
   * Allows to use different textures for the model.
   * There are 3 layers:
   * base - The empty bucket/container
   * fluid - A texture representing the liquid portion. Non-transparent = liquid
   * cover - An overlay that's put over the liquid (optional)
   *
   * If no liquid is given a hardcoded variant for the bucket is used.
   */
  @Override
  public IModel retexture(ImmutableMap<String, String> textures) {

    ResourceLocation base = baseLocation;
    ResourceLocation liquid = liquidLocation;
    ResourceLocation cover = coverLocation;

    if(textures.containsKey("base"))
      base = new ResourceLocation(textures.get("base"));
    if(textures.containsKey("fluid"))
      liquid = new ResourceLocation(textures.get("fluid"));
    if(textures.containsKey("cover"))
      cover = new ResourceLocation(textures.get("cover"));

    return new ModelDynBucket(base, liquid, cover, fluid, flipGas);
  }

  public enum LoaderDynBucket implements ICustomModelLoader {
    instance;

    @Override
    public boolean accepts(ResourceLocation modelLocation) {
      return modelLocation.getResourceDomain().equals("forge") && modelLocation.getResourcePath().contains("forgebucket");
    }

    @Override
    public IModel loadModel(ResourceLocation modelLocation) throws IOException {
      return MODEL;
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
      // no need to clear cache since we create a new model instance
    }
  }

  // the dynamic bucket is based on the empty bucket
  protected static class BakedDynBucket extends ItemLayerModel.BakedModel implements ISmartItemModel, IPerspectiveAwareModel {

    private final ModelDynBucket parent;
    private final Map<String, IFlexibleBakedModel> cache; // contains all the baked models since they'll never change
    private final ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> transforms;

    public BakedDynBucket(ModelDynBucket parent,
                          ImmutableList<BakedQuad> quads, TextureAtlasSprite particle, VertexFormat format, ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> transforms,
                          Map<String, IFlexibleBakedModel> cache) {
      super(quads, particle, format, transforms);
      this.parent = parent;
      this.transforms = transforms;
      this.cache = cache;
    }

    @Override
    public IBakedModel handleItemState(ItemStack stack) {
      FluidStack fluidStack = FluidContainerRegistry.getFluidForFilledItem(stack);
      if(fluidStack == null) {
        if(stack.getItem() instanceof IFluidContainerItem) {
          fluidStack = ((IFluidContainerItem) stack.getItem()).getFluid(stack);
        }
      }

      // not a fluid item apparently
      if(fluidStack == null)
        return this;


      Fluid fluid = fluidStack.getFluid();
      String name = fluid.getName();

      if(!cache.containsKey(name)) {
        IModel model = parent.process(ImmutableMap.of("fluid", name));
        Function<ResourceLocation, TextureAtlasSprite> textureGetter;
        textureGetter = new Function<ResourceLocation, TextureAtlasSprite>() {
          public TextureAtlasSprite apply(ResourceLocation location) {
            return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString());
          }
        };


        ImmutableMap.Builder<ItemCameraTransforms.TransformType, IModelState> builder = ImmutableMap.builder();

        for(Map.Entry<ItemCameraTransforms.TransformType, TRSRTransformation> entry : transforms.entrySet()) {
          builder.put(entry.getKey(), entry.getValue());
        }

        IModelState state = new IPerspectiveState.Impl(parent.getDefaultState(), builder.build());

        IFlexibleBakedModel bakedModel = model.bake(state, this.getFormat(), textureGetter);
        cache.put(name, bakedModel);
        return bakedModel;
      }

      return cache.get(name);
    }
  }
}
