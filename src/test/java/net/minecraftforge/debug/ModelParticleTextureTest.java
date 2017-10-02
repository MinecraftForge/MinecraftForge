package net.minecraftforge.debug;

import net.minecraft.block.Block;
import net.minecraft.block.BlockColored;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.common.property.PropertyFloat;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Mod.EventBusSubscriber
@Mod(modid = ModelParticleTextureTest.MOD_ID, name = "Test mod for state-aware particles", version = "1.0")
public class ModelParticleTextureTest
{
    static final String MOD_ID = "multi_particle_model_test";

    @GameRegistry.ObjectHolder(TestBlock.NAME)
    public static final Block TEST_BLOCK = null;

    private static final class TestBlock extends Block
    {
        static final String NAME = "test_block";
        static final IUnlistedProperty<Float> PROPERTY = new PropertyFloat("value", f -> f != null && f >= 0f && f < 1f);

        TestBlock()
        {
            super(Material.ROCK);
            setUnlocalizedName(MOD_ID + "." + NAME);
            setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
            setHardness(5.0f);
        }

        @Override
        protected BlockStateContainer createBlockState()
        {
            return new BlockStateContainer.Builder(this).add(PROPERTY).build();
        }

        @Override
        public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos)
        {
            long seed = MathHelper.getPositionRandom(pos);
            float value = new Random(seed).nextFloat();
            return ((IExtendedBlockState) state).withProperty(PROPERTY, value);
        }
    }

    private static final class TestBlockModel implements IBakedModel
    {
        static final ModelResourceLocation LOCATION = new ModelResourceLocation(new ResourceLocation(MOD_ID, TestBlock.NAME), "custom");

        private IBakedModel model;
        private final List<TextureAtlasSprite> textures = new ArrayList<>();
        private boolean init = false;

        private void initModel()
        {
            BlockModelShapes blockModelShapes = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes();

            IBlockState modelState = Blocks.CONCRETE.getDefaultState();
            model = blockModelShapes.getModelForState(modelState);

            textures.clear();
            for (EnumDyeColor color : EnumDyeColor.values())
            {
                textures.add(blockModelShapes.getTexture(modelState.withProperty(BlockColored.COLOR, color)));
            }

            init = true;
        }

        @Override
        public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand)
        {
            if (!init) initModel();
            return model.getQuads(state, side, rand);
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
            if (!init) initModel();
            return model.getParticleTexture();
        }

        @Override
        public ItemOverrideList getOverrides()
        {
            return ItemOverrideList.NONE;
        }

        @Override
        public TextureAtlasSprite getParticleTexture(IBlockState state)
        {
            if (!init) initModel();
            Float value = ((IExtendedBlockState) state).getValue(TestBlock.PROPERTY);
            if (value != null)
            {
                return textures.get((int) (value * textures.size()));
            }
            return getParticleTexture();
        }
    }

    @SubscribeEvent
    public static void registerBlock(RegistryEvent.Register<Block> event)
    {
        event.getRegistry().register(new TestBlock().setRegistryName(MOD_ID, TestBlock.NAME));
    }

    @SubscribeEvent
    public static void registerItem(RegistryEvent.Register<Item> event)
    {
        event.getRegistry().register(new ItemBlock(TEST_BLOCK).setRegistryName(TEST_BLOCK.getRegistryName()));
    }

    @Mod.EventBusSubscriber(value = Side.CLIENT, modid = MOD_ID)
    public static final class ClientEventHandler
    {
        @SubscribeEvent
        public static void handleModel(ModelRegistryEvent event)
        {
            ModelLoader.setCustomStateMapper(TEST_BLOCK, new StateMapperBase()
            {
                @Override
                protected ModelResourceLocation getModelResourceLocation(IBlockState state)
                {
                    return TestBlockModel.LOCATION;
                }
            });
        }

        @SubscribeEvent
        public static void onModelBake(ModelBakeEvent event)
        {
            if (event.getModelRegistry().getObject(TestBlockModel.LOCATION) != null)
            {
                event.getModelRegistry().putObject(TestBlockModel.LOCATION, new TestBlockModel());
            }
        }
    }
}
