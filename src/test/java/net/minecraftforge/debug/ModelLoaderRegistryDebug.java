package net.minecraftforge.debug;

import java.util.ArrayList;
import java.util.List;

import scala.actors.threadpool.Arrays;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.b3d.B3DLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.client.model.obj.OBJModel;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = ModelLoaderRegistryDebug.MODID, version = ModelLoaderRegistryDebug.VERSION)
public class ModelLoaderRegistryDebug
{
    public static final String MODID = "ForgeDebugModelLoaderRegistry";
    public static final String VERSION = "1.0";

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        GameRegistry.registerBlock(CustomModelBlock.instance, CustomModelBlock.name);
        GameRegistry.registerBlock(CustomModelBlock2.instance, CustomModelBlock2.name);
        if (event.getSide() == Side.CLIENT)
            clientPreInit();
    }

    private void clientPreInit()
    {
        B3DLoader.instance.addDomain(MODID.toLowerCase());
        Item item = Item.getItemFromBlock(CustomModelBlock.instance);
        ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(MODID.toLowerCase() + ":" + CustomModelBlock.name, "inventory"));
        
        OBJLoader.instance.addDomain(MODID.toLowerCase());
        Item item2 = Item.getItemFromBlock(CustomModelBlock2.instance);
        ModelLoader.setCustomModelResourceLocation(item2, 0, new ModelResourceLocation(MODID.toLowerCase() + ":" + CustomModelBlock2.name, "inventory"));
    }

    public static class CustomModelBlock extends Block
    {
        public static final CustomModelBlock instance = new CustomModelBlock();
        public static final String name = "CustomModelBlock";
        private int counter = 1;
        private ExtendedBlockState state = new ExtendedBlockState(this, new IProperty[0], new IUnlistedProperty[]{ B3DLoader.B3DFrameProperty.instance });

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

        @Override
        public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos)
        {
            B3DLoader.B3DState newState = new B3DLoader.B3DState(null, counter);
            return ((IExtendedBlockState)this.state.getBaseState()).withProperty(B3DLoader.B3DFrameProperty.instance, newState);
        }

        @Override
        public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ)
        {
            if(world.isRemote)
            {
                System.out.println("click " + counter);
                if(player.isSneaking()) counter--;
                else counter++;
                //if(counter >= model.getNode().getKeys().size()) counter = 0;
                world.markBlockRangeForRenderUpdate(pos, pos);
            }
            return false;
        }
    }
    
    public static class CustomModelBlock2 extends Block
    {
        public static final CustomModelBlock2 instance = new CustomModelBlock2();
        public static final String name = "CustomModelBlock2";
        private int counter = 0;
        private ExtendedBlockState state = new ExtendedBlockState(this, new IProperty[0], new IUnlistedProperty[]{OBJModel.OBJModelProperty.instance});
        
        private CustomModelBlock2()
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
        
        @Override
        public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
            //FIXME make these camera transforms work!!!
//            ItemTransformVec3f thirdPerson = new ItemTransformVec3f(new Vector3f(0f, 0f, 0f), new Vector3f(0f, 0f, 0f), new Vector3f(0.1f, 0.1f, 0.1f));
//            ItemTransformVec3f firstPerson = new ItemTransformVec3f(new Vector3f(0f, 0f, 0f), new Vector3f(0f, 0f, 0f), new Vector3f(0.1f, 0.1f, 0.1f));
//            ItemTransformVec3f head = new ItemTransformVec3f(new Vector3f(0f, 0f, 0f), new Vector3f(0f, 0f, 0f), new Vector3f(0.1f, 0.1f, 0.1f));
//            ItemTransformVec3f gui = new ItemTransformVec3f(new Vector3f(0f, 0f, 0f), new Vector3f(0f, 0f, 0f), new Vector3f(0.1f, 0.1f, 0.1f));
            
            String[] all = new String[] {"all"};
            String[] none = new String[] {"none"};
            String[] all_except = new String[] {"all except", "one", "two", "three", "four"};
            String[] first = new String[] {"one", "two", "three", "four", "five", "six", "seven", "eight"};
            String[] last = new String[] {"thirtytwo", "thirtyone", "thirty", "twentynine", "twentyeight", "twentyseven", "twentysix", "twentyfive"};
            List<String[]> elements = new ArrayList<String[]>();
            elements.add(all);
            elements.add(all_except);
            elements.add(none);
            elements.add(first);
            elements.add(last);
            IModel model = ModelLoaderRegistry.getModel(new ResourceLocation(MODID.toLowerCase(), "block/cmb2_tesseract.obj"));
            OBJModel.OBJState defaultState = ((OBJModel) model).getDefaultState();
            OBJModel.OBJState newState = ((OBJModel) model).new OBJState(Arrays.asList(elements.get(counter)), ItemCameraTransforms.DEFAULT, (OBJModel) model);
            if (world instanceof World) {
                World wrld = (World) world;
                wrld.markBlockRangeForRenderUpdate(pos, pos);
            }
            return ((IExtendedBlockState) this.state.getBaseState()).withProperty(OBJModel.OBJModelProperty.instance, newState);
        }
        
        @Override
        public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
            if (counter < 4) counter++;
            else counter = 0;
            world.markBlockRangeForRenderUpdate(pos, pos);
            return false;
        }
    }
}
