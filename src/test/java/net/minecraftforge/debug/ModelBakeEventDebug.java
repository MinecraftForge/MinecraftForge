package net.minecraftforge.debug;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.ISmartBlockModel;
import net.minecraftforge.client.model.ISmartItemModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.common.property.Properties;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import com.google.common.primitives.Ints;

@SuppressWarnings("deprecation")
@Mod(modid = ModelBakeEventDebug.MODID, version = ModelBakeEventDebug.VERSION)
public class ModelBakeEventDebug
{
    public static final String MODID = "ForgeDebugModelBakeEvent";
    public static final String VERSION = "1.0";
    public static final int cubeSize = 3;

    private static String blockName = MODID.toLowerCase() + ":" + CustomModelBlock.name;

    @SuppressWarnings("unchecked")
    public static final IUnlistedProperty<Integer>[] properties = new IUnlistedProperty[6];

    static
    {
        for(EnumFacing f : EnumFacing.values())
        {
            properties[f.ordinal()] = Properties.toUnlisted(PropertyInteger.create(f.getName(), 0, (1 << (cubeSize * cubeSize)) - 1));
        }
    }

    @SidedProxy
    public static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) { proxy.preInit(event); }

    public static class CommonProxy
    {
        public void preInit(FMLPreInitializationEvent event)
        {
            GameRegistry.registerBlock(CustomModelBlock.instance, CustomModelBlock.name);
            GameRegistry.registerTileEntity(CustomTileEntity.class, MODID.toLowerCase() + ":custom_tile_entity");
        }
    }

    public static class ServerProxy extends CommonProxy {}

    public static class ClientProxy extends CommonProxy
    {
        private static ModelResourceLocation blockLocation = new ModelResourceLocation(blockName, "normal");
        private static ModelResourceLocation itemLocation = new ModelResourceLocation(blockName, "inventory");

        @Override
        public void preInit(FMLPreInitializationEvent event)
        {
            super.preInit(event);
            Item item = Item.getItemFromBlock(CustomModelBlock.instance);
            ModelLoader.setCustomModelResourceLocation(item, 0, itemLocation);
            ModelLoader.setCustomStateMapper(CustomModelBlock.instance, new StateMapperBase(){
                protected ModelResourceLocation getModelResourceLocation(IBlockState p_178132_1_)
                {
                    return blockLocation;
                }
            });
            MinecraftForge.EVENT_BUS.register(BakeEventHandler.instance);
        }
    }

    public static class BakeEventHandler
    {
        public static final BakeEventHandler instance = new BakeEventHandler();

        private BakeEventHandler() {};

        @SubscribeEvent
        public void onModelBakeEvent(ModelBakeEvent event)
        {
            TextureAtlasSprite base = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite("minecraft:blocks/slime");
            TextureAtlasSprite overlay = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite("minecraft:blocks/redstone_block");
            IBakedModel customModel = new CustomModel(base, overlay);
            event.modelRegistry.putObject(ClientProxy.blockLocation, customModel);
            event.modelRegistry.putObject(ClientProxy.itemLocation, customModel);
        }
    }

    public static class CustomModelBlock extends BlockContainer
    {
        public static final CustomModelBlock instance = new CustomModelBlock();
        public static final String name = "custom_model_block";

        private CustomModelBlock()
        {
            super(Material.iron);
            setCreativeTab(CreativeTabs.tabBlock);
            setUnlocalizedName(MODID + ":" + name);
        }

        @Override
        public int getRenderType() { return 3; }

        @Override
        public boolean isOpaqueCube() { return false; }

        @Override
        public boolean isFullCube() { return false; }

        @Override
        public boolean isVisuallyOpaque() { return false; }

        @Override
        public TileEntity createNewTileEntity(World world, int meta)
        {
            return new CustomTileEntity();
        }

        @Override
        public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ)
        {
            TileEntity te = world.getTileEntity(pos);
            if(te instanceof CustomTileEntity)
            {
                CustomTileEntity cte = (CustomTileEntity) te;
                Vec3 vec = revRotate(new Vec3(hitX - .5, hitY - .5, hitZ - .5), side).addVector(.5, .5, .5);
                IUnlistedProperty<Integer> property = properties[side.ordinal()];
                Integer value = cte.getState().getValue(property);
                if(value == null) value = 0;
                value ^= (1 << ( cubeSize * ((int)(vec.xCoord * (cubeSize - .0001))) + ((int)(vec.zCoord * (cubeSize - .0001))) ));
                cte.setState(cte.getState().withProperty(property, value));
                world.markBlockRangeForRenderUpdate(pos, pos);
            }
            return true;
        }

        @Override
        public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos)
        {
            TileEntity te = world.getTileEntity(pos);
            if(te instanceof CustomTileEntity)
            {
                CustomTileEntity cte = (CustomTileEntity) te;
                return cte.getState();
            }
            return state;
        }

        @Override
        protected BlockState createBlockState()
        {
            return new ExtendedBlockState(this, new IProperty[0], properties);
        }
    }

    public static class CustomTileEntity extends TileEntity
    {
        private IExtendedBlockState state;
        public CustomTileEntity() {}

        public IExtendedBlockState getState()
        {
            if(state == null)
            {
                state = (IExtendedBlockState)getBlockType().getDefaultState();
            }
            return state;
        }

        public void setState(IExtendedBlockState state)
        {
            this.state = state;
        }
    }

    public static class CustomModel implements IBakedModel, ISmartBlockModel, ISmartItemModel
    {
        private final TextureAtlasSprite base, overlay;
        //private boolean hasStateSet = false;
        private final IExtendedBlockState state;

        public CustomModel(TextureAtlasSprite base, TextureAtlasSprite overlay)
        {
            this(base, overlay, null);
        }

        public CustomModel(TextureAtlasSprite base, TextureAtlasSprite overlay, IExtendedBlockState state)
        {
            this.base = base;
            this.overlay = overlay;
            this.state = state;
        }

        @Override
        public List<BakedQuad> getFaceQuads(EnumFacing side)
        {
            return Collections.emptyList();
        }

        private int[] vertexToInts(float x, float y, float z, int color, TextureAtlasSprite texture, float u, float v)
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

        private BakedQuad createSidedBakedQuad(float x1, float x2, float z1, float z2, float y, TextureAtlasSprite texture, EnumFacing side)
        {
            Vec3 v1 = rotate(new Vec3(x1 - .5, y - .5, z1 - .5), side).addVector(.5, .5, .5);
            Vec3 v2 = rotate(new Vec3(x1 - .5, y - .5, z2 - .5), side).addVector(.5, .5, .5);
            Vec3 v3 = rotate(new Vec3(x2 - .5, y - .5, z2 - .5), side).addVector(.5, .5, .5);
            Vec3 v4 = rotate(new Vec3(x2 - .5, y - .5, z1 - .5), side).addVector(.5, .5, .5);
            return new BakedQuad(Ints.concat(
                vertexToInts((float)v1.xCoord, (float)v1.yCoord, (float)v1.zCoord, -1, texture, 0, 0),
                vertexToInts((float)v2.xCoord, (float)v2.yCoord, (float)v2.zCoord, -1, texture, 0, 16),
                vertexToInts((float)v3.xCoord, (float)v3.yCoord, (float)v3.zCoord, -1, texture, 16, 16),
                vertexToInts((float)v4.xCoord, (float)v4.yCoord, (float)v4.zCoord, -1, texture, 16, 0)
            ), -1, side);
        }

        @Override
        public List<BakedQuad> getGeneralQuads()
        {
            int len = cubeSize * 5 + 1;
            List<BakedQuad> ret = new ArrayList<BakedQuad>();
            for(EnumFacing f : EnumFacing.values())
            {
                ret.add(createSidedBakedQuad(0, 1, 0, 1, 1, base, f));
                for(int i = 0; i < cubeSize; i++)
                {
                    for(int j = 0; j < cubeSize; j++)
                    {
                        if(state != null)
                        {
                            Integer value = state.getValue(properties[f.ordinal()]);
                            if(value != null && (value & (1 << (i * cubeSize + j))) != 0)
                            {
                                ret.add(createSidedBakedQuad((float)(1 + i * 5) / len, (float)(5 + i * 5) / len, (float)(1 + j * 5) / len, (float)(5 + j * 5) / len, 1.0001f, overlay, f));
                            }
                        }
                    }
                }
            }
            return ret;
        }

        @Override
        public boolean isGui3d() { return true; }

        @Override
        public boolean isAmbientOcclusion() { return true; }

        @Override
        public boolean isBuiltInRenderer() { return false; }

        @Override
        public TextureAtlasSprite getParticleTexture() { return this.base; }

        @Override
        public ItemCameraTransforms getItemCameraTransforms()
        {
            return ItemCameraTransforms.DEFAULT;
        }

        @Override
        public IBakedModel handleBlockState(IBlockState state)
        {
            return new CustomModel(base, overlay, (IExtendedBlockState)state);
        }

        @Override
        public IBakedModel handleItemState(ItemStack stack)
        {
            IExtendedBlockState itemState = ((IExtendedBlockState)CustomModelBlock.instance.getDefaultState()).withProperty(properties[1], (1 << (cubeSize * cubeSize)) - 1);
            return new CustomModel(base, overlay, itemState);
        }
    }

    private static Vec3 rotate(Vec3 vec, EnumFacing side)
    {
        switch(side)
        {
            case DOWN:  return new Vec3( vec.xCoord, -vec.yCoord, -vec.zCoord);
            case UP:    return new Vec3( vec.xCoord,  vec.yCoord,  vec.zCoord);
            case NORTH: return new Vec3( vec.xCoord,  vec.zCoord, -vec.yCoord);
            case SOUTH: return new Vec3( vec.xCoord, -vec.zCoord,  vec.yCoord);
            case WEST:  return new Vec3(-vec.yCoord,  vec.xCoord,  vec.zCoord);
            case EAST:  return new Vec3( vec.yCoord, -vec.xCoord,  vec.zCoord);
        }
        return null;
    }

    private static Vec3 revRotate(Vec3 vec, EnumFacing side)
    {
        switch(side)
        {
            case DOWN:  return new Vec3( vec.xCoord, -vec.yCoord, -vec.zCoord);
            case UP:    return new Vec3( vec.xCoord,  vec.yCoord,  vec.zCoord);
            case NORTH: return new Vec3( vec.xCoord, -vec.zCoord,  vec.yCoord);
            case SOUTH: return new Vec3( vec.xCoord,  vec.zCoord, -vec.yCoord);
            case WEST:  return new Vec3( vec.yCoord, -vec.xCoord,  vec.zCoord);
            case EAST:  return new Vec3(-vec.yCoord,  vec.xCoord,  vec.zCoord);
        }
        return null;
    }
}
