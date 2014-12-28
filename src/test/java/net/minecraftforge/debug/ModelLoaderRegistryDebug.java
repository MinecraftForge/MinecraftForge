package net.minecraftforge.debug;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.Attributes;
import net.minecraftforge.client.model.ICameraTransformations;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.IModelTransformation;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import com.google.common.base.Function;
import com.google.common.primitives.Ints;

@Mod(modid = ModelLoaderRegistryDebug.MODID, version = ModelLoaderRegistryDebug.VERSION)
public class ModelLoaderRegistryDebug
{
    public static final String MODID = "ForgeDebugModelLoaderRegistry";
    public static final String VERSION = "1.0";

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        ModelLoaderRegistry.registerLoader(DummyModelLoader.instance);
        GameRegistry.registerBlock(CustomModelBlock.instance, CustomModelBlock.name);
        ModelBakery.addVariantName(Item.getItemFromBlock(CustomModelBlock.instance), "forgedebug:dummymodel");
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        Item item = Item.getItemFromBlock(CustomModelBlock.instance);
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0, new ModelResourceLocation("forgedebug:dummymodel", "inventory"));
    }

    public static class CustomModelBlock extends Block
    {
        public static final CustomModelBlock instance = new CustomModelBlock();
        public static final String name = "CustomModelBlock";

        private CustomModelBlock()
        {
            super(Material.iron);
            setCreativeTab(CreativeTabs.tabBlock);
            setUnlocalizedName(MODID + ":" + name);
        }

        @Override
        public boolean isOpaqueCube() { return false; }

        @Override
        public boolean isFullCube() { return false; }

        @Override
        public boolean isVisuallyOpaque() { return false; }
    }

    public static class DummyModelLoader implements ICustomModelLoader
    {
        public static final DummyModelLoader instance = new DummyModelLoader();
        public static final ResourceLocation dummyTexture = new ResourceLocation("minecraft:blocks/dirt");

        public boolean accepts(ResourceLocation modelLocation)
        {
            return modelLocation.getResourceDomain().equals("forgedebug") && modelLocation.getResourcePath().contains("dummymodel");
        }

        public IModel loadModel(ResourceLocation model)
        {
            return DummyModel.instance;
        }

        public static enum DummyModel implements IModel
        {
            instance;

            public Collection<ResourceLocation> getDependencies()
            {
                return Collections.emptyList();
            }

            public Collection<ResourceLocation> getTextures()
            {
                return Collections.singletonList(dummyTexture);
            }

            public IFlexibleBakedModel bake(IModelTransformation transformation, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> textures)
            {
                // transformation is ignored for this example
                return new DummyBakedModel(textures.apply(dummyTexture));
            }

            public IModelTransformation getDefaultTransformation()
            {
                return ModelRotation.X0_Y0;
            }
        }

        public static class DummyBakedModel implements IFlexibleBakedModel
        {
            private final TextureAtlasSprite texture;

            public DummyBakedModel(TextureAtlasSprite texture)
            {
                this.texture = texture;
            }

            public List<BakedQuad> getFaceQuads(EnumFacing side)
            {
                return Collections.emptyList();
            }

            private int[] vertexToInts(float x, float y, float z, int color, float u, float v)
            {
                return new int[] {
                    Float.floatToRawIntBits(x),
                    Float.floatToRawIntBits(y),
                    Float.floatToRawIntBits(z),
                    color,
                    Float.floatToRawIntBits(texture.getInterpolatedU(u)),
                    Float.floatToRawIntBits(texture.getInterpolatedV(v)),
                    0
                };
            }

            public List<BakedQuad> getGeneralQuads()
            {
                List<BakedQuad> ret = new ArrayList<BakedQuad>();
                // 1 half-way rotated quad looking UP
                ret.add(new BakedQuad(Ints.concat(
                    vertexToInts(0, .5f, .5f, -1, 0, 0),
                    vertexToInts(.5f, .5f, 1, -1, 0, 16),
                    vertexToInts(1, .5f, .5f, -1, 16, 16),
                    vertexToInts(.5f, .5f, 0, -1, 16, 0)
                ), -1, EnumFacing.UP));
                return ret;
            }

            public boolean isGui3d() { return true; }

            public boolean isAmbientOcclusion() { return true; }

            public boolean isBuiltInRenderer() { return false; }

            public TextureAtlasSprite getTexture() { return this.texture; }

            public ItemCameraTransforms getItemCameraTransforms()
            {
                return ItemCameraTransforms.DEFAULT;
            }

            public ICameraTransformations getCameraTransforms()
            {
                return getItemCameraTransforms();
            }

            public VertexFormat getFormat()
            {
                return Attributes.DEFAULT_BAKED_FORMAT;
            }
        }

        public void onResourceManagerReload(IResourceManager resourceManager) {}
    }
}
