
package net.minecraftforge.debug;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.client.model.ISmartBlockModel;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

import com.google.common.primitives.Ints;

@SuppressWarnings( "deprecation" )
@Mod( modid = "LayerBreakingTest", name = "LayerBreakingTest", version = "0.0.0" )
public class LayerBreakingTest
{
    public static final boolean ENABLE = true;
    public static TestBlock testBlock;

    class TestBakedModel implements IFlexibleBakedModel
    {
        TextureAtlasSprite texture;
        List<BakedQuad> list = new ArrayList<BakedQuad>();

        private int[] vertexToInts(float x, float y, float z, int color, TextureAtlasSprite texture, int u, int v)
        {
            return new int[] { Float.floatToRawIntBits(x), Float.floatToRawIntBits(y), Float.floatToRawIntBits(z), color, Float.floatToRawIntBits(texture.getInterpolatedU(u)), Float.floatToRawIntBits(texture.getInterpolatedV(v)), 0 };
        }

        public TestBakedModel(TextureAtlasSprite sprite, boolean top)
        {
            texture = sprite;
            if (top)
            {
                list.add(new BakedQuad(Ints.concat(vertexToInts(0.5f, 1.0f, 0, -1, texture, 8, 0), vertexToInts(0.5f, 1.0f, 1, -1, texture, 8, 16), vertexToInts(1, 1.0f, 1, -1, texture, 16, 16), vertexToInts(1, 1.0f, 0, -1, texture, 16, 0)), -1, EnumFacing.UP));
            }
            else
            {
                list.add(new BakedQuad(Ints.concat(vertexToInts(0, 1.0f, 0, -1, texture, 0, 0), vertexToInts(0, 1.0f, 1, -1, texture, 0, 16), vertexToInts(0.5f, 1.0f, 1, -1, texture, 8, 16), vertexToInts(0.5f, 1.0f, 0, -1, texture, 8, 0)), -1, EnumFacing.DOWN));
            }
        }

        @Override
        public boolean isAmbientOcclusion()
        {
            return true;
        }

        @Override
        public boolean isGui3d()
        {
            return true;
        }

        @Override
        public boolean isBuiltInRenderer()
        {
            return false;
        }

        @Override
        public TextureAtlasSprite getParticleTexture()
        {
            return texture;
        }

        @Override
        public ItemCameraTransforms getItemCameraTransforms()
        {
            return ItemCameraTransforms.DEFAULT;
        }

        @Override
        public List<BakedQuad> getFaceQuads(EnumFacing side)
        {
            return Collections.emptyList();
        }

        @Override
        public List<BakedQuad> getGeneralQuads()
        {
            return list;
        }

        @Override
        public VertexFormat getFormat()
        {
            return null;
        }
    };

    class TestBlock extends Block
    {
        protected TestBlock()
        {
            super(Material.glass);
            setHardness(7);
            setCreativeTab(CreativeTabs.tabBlock);
        }

        @Override
        public boolean isOpaqueCube()
        {
            return false;
        }

        public boolean isFullCube()
        {
            return false;
        }

        @Override
        public boolean canRenderInLayer(EnumWorldBlockLayer layer)
        {
            return layer == EnumWorldBlockLayer.SOLID || layer == EnumWorldBlockLayer.TRANSLUCENT;
        }
    };

    class SmartModel implements IBakedModel, ISmartBlockModel
    {
        IFlexibleBakedModel solid;
        IFlexibleBakedModel translucent;

        @Override
        public List<BakedQuad> getFaceQuads(EnumFacing p_177551_1_)
        {
            return Collections.emptyList();
        }

        @Override
        public List<BakedQuad> getGeneralQuads()
        {
            return Collections.emptyList();
        }

        @Override
        public boolean isAmbientOcclusion()
        {
            return true;
        }

        @Override
        public boolean isGui3d()
        {
            return true;
        }

        @Override
        public boolean isBuiltInRenderer()
        {
            return false;
        }

        @Override
        public TextureAtlasSprite getParticleTexture()
        {
            return null;
        }

        @Override
        public ItemCameraTransforms getItemCameraTransforms()
        {
            return ItemCameraTransforms.DEFAULT;
        }

        @Override
        public IBakedModel handleBlockState(IBlockState state)
        {
            if (solid == null)
            {
                BlockModelShapes models =  Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes();
                translucent = new TestBakedModel(models.getModelForState(Blocks.stained_glass.getStateFromMeta(3)).getParticleTexture(), true);
                solid = new TestBakedModel(models.getModelForState(Blocks.cobblestone.getDefaultState()).getParticleTexture(), false);
            }

            if (net.minecraftforge.client.MinecraftForgeClient.getRenderLayer() == EnumWorldBlockLayer.SOLID)
            {
                return solid;
            }
            else
            {
                return translucent;
            }
        }
    };

    @SubscribeEvent
    public void onModelBakeEvent(ModelBakeEvent event)
    {
        event.modelRegistry.putObject(new ModelResourceLocation("layerbreakingtest:layer_breaking_test"), new SmartModel());
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        if (ENABLE && event.getSide() == Side.CLIENT)
        {
            MinecraftForge.EVENT_BUS.register(this);
            GameRegistry.registerBlock(testBlock = new TestBlock(), "layer_breaking_test");
        }
    }
}
