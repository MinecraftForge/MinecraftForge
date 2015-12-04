package net.minecraftforge.debug;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.vecmath.AxisAngle4d;
import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector4f;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.TRSRTransformation;
import net.minecraftforge.client.model.b3d.B3DLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.client.model.obj.OBJModel;
import net.minecraftforge.client.model.obj.OBJModel.OBJBakedModel;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

@Mod(modid = ModelLoaderRegistryDebug.MODID, version = ModelLoaderRegistryDebug.VERSION)
public class ModelLoaderRegistryDebug
{
    public static final String MODID = "ForgeDebugModelLoaderRegistry";
    public static final String VERSION = "1.0";

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        GameRegistry.registerBlock(CustomModelBlock.instance, CustomModelBlock.name);
        GameRegistry.registerBlock(OBJTesseractBlock.instance, OBJTesseractBlock.name);
        GameRegistry.registerBlock(OBJVertexColoring1.instance, OBJVertexColoring1.name);
        GameRegistry.registerBlock(OBJDirectionEye.instance, OBJDirectionEye.name);
        GameRegistry.registerBlock(OBJVertexColoring2.instance, OBJVertexColoring2.name);
        GameRegistry.registerBlock(OBJDirectionBlock.instance, OBJDirectionBlock.name);
        GameRegistry.registerBlock(OBJCustomDataBlock.instance, OBJCustomDataBlock.name);
        GameRegistry.registerBlock(OBJDynamicEye.instance, OBJDynamicEye.name);
        GameRegistry.registerTileEntity(OBJTesseractTileEntity.class, OBJTesseractBlock.name);
        GameRegistry.registerTileEntity(OBJVertexColoring2TileEntity.class, OBJVertexColoring2.name);
        GameRegistry.registerTileEntity(OBJDynamicEyeTileEntity.class, OBJDynamicEye.name);
        if (event.getSide() == Side.CLIENT)
            clientPreInit();
    }

    private void clientPreInit()
    {
        B3DLoader.instance.addDomain(MODID.toLowerCase());
        Item item = Item.getItemFromBlock(CustomModelBlock.instance);
        ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(MODID.toLowerCase() + ":" + CustomModelBlock.name, "inventory"));
        
        OBJLoader.instance.addDomain(MODID.toLowerCase());
        Item item2 = Item.getItemFromBlock(OBJTesseractBlock.instance);
        ModelLoader.setCustomModelResourceLocation(item2, 0, new ModelResourceLocation(MODID.toLowerCase() + ":" + OBJTesseractBlock.name, "inventory"));
        
        Item item3 = Item.getItemFromBlock(OBJVertexColoring1.instance);
        ModelLoader.setCustomModelResourceLocation(item3, 0, new ModelResourceLocation(MODID.toLowerCase() + ":" + OBJVertexColoring1.name, "inventory"));
        
        Item item4 = Item.getItemFromBlock(OBJDirectionEye.instance);
        ModelLoader.setCustomModelResourceLocation(item4, 0, new ModelResourceLocation(MODID.toLowerCase() + ":" + OBJDirectionEye.name, "inventory"));
        
        Item item5 = Item.getItemFromBlock(OBJVertexColoring2.instance);
        ModelLoader.setCustomModelResourceLocation(item5, 0, new ModelResourceLocation(MODID.toLowerCase() + ":" + OBJVertexColoring2.name, "inventory"));
        
        Item item6 = Item.getItemFromBlock(OBJDirectionBlock.instance);
        ModelLoader.setCustomModelResourceLocation(item6, 0, new ModelResourceLocation(MODID.toLowerCase() + ":" + OBJDirectionBlock.name, "inventory"));
        
        Item item7 = Item.getItemFromBlock(OBJCustomDataBlock.instance);
        ModelLoader.setCustomModelResourceLocation(item7, 0, new ModelResourceLocation(MODID.toLowerCase() + ":" + OBJCustomDataBlock.name, "inventory"));
        
        Item item8 = Item.getItemFromBlock(OBJDynamicEye.instance);
        ModelLoader.setCustomModelResourceLocation(item8, 0, new ModelResourceLocation(MODID.toLowerCase() + ":" + OBJDynamicEye.name, "inventory"));
    }

    public static class CustomModelBlock extends Block
    {
        public static final PropertyDirection FACING = PropertyDirection.create("facing");
        public static final CustomModelBlock instance = new CustomModelBlock();
        public static final String name = "CustomModelBlock";
        private int counter = 1;
        public ExtendedBlockState state = new ExtendedBlockState(this, new IProperty[]{FACING}, new IUnlistedProperty[]{B3DLoader.B3DFrameProperty.instance});

        private CustomModelBlock()
        {
            super(Material.iron);
            this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
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
        public IBlockState onBlockPlaced(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
        {
            return this.getDefaultState().withProperty(FACING, getFacingFromEntity(world, pos, placer));
        }
        
        @Override
        public IBlockState getStateFromMeta(int meta)
        {
            return this.getDefaultState().withProperty(FACING, EnumFacing.getFront(meta));
        }
        
        @Override
        public int getMetaFromState(IBlockState state)
        {
            return ((EnumFacing) state.getValue(FACING)).getIndex();
        }
        
        @Override
        public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos)
        {
            //Only return an IExtendedBlockState from this method and createState(), otherwise block placement might break!
            B3DLoader.B3DState newState = new B3DLoader.B3DState(null, counter);
            return ((IExtendedBlockState) state).withProperty(B3DLoader.B3DFrameProperty.instance, newState);
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
        
        @Override
        public BlockState createBlockState()
        {
            return new ExtendedBlockState(this, new IProperty[]{FACING}, new IUnlistedProperty[]{B3DLoader.B3DFrameProperty.instance});
        }
        
        public static EnumFacing getFacingFromEntity(World worldIn, BlockPos clickedBlock, EntityLivingBase entityIn)
        {
            if (MathHelper.abs((float)entityIn.posX - (float)clickedBlock.getX()) < 2.0F && MathHelper.abs((float)entityIn.posZ - (float)clickedBlock.getZ()) < 2.0F)
            {
                double d0 = entityIn.posY + (double)entityIn.getEyeHeight();

                if (d0 - (double)clickedBlock.getY() > 2.0D)
                {
                    return EnumFacing.UP;
                }

                if ((double)clickedBlock.getY() - d0 > 0.0D)
                {
                    return EnumFacing.DOWN;
                }
            }

            return entityIn.getHorizontalFacing().getOpposite();
        }
    }
    /**
     * This block is intended to demonstrate how to change the visibility of a group(s)
     * from within the block's class.
     * By right clicking on this block the player increments an integer value in the tile entity
     * for this block, which is then added to a list of strings and passed into the constructor
     * for OBJState. NOTE: this trick only works if your groups are named '1', '2', '3', etc.,
     * otherwise they must be added by name.
     * Holding shift decrements the value in the tile entity.
     * @author shadekiller666
     *
     */
    public static class OBJTesseractBlock extends Block implements ITileEntityProvider
    {
        public static final OBJTesseractBlock instance = new OBJTesseractBlock();
        public static final String name = "OBJTesseractBlock";
        private ExtendedBlockState state = new ExtendedBlockState(this, new IProperty[0], new IUnlistedProperty[]{OBJModel.OBJProperty.instance});
        
        private OBJTesseractBlock()
        {
            super(Material.iron);
            setCreativeTab(CreativeTabs.tabBlock);
            setUnlocalizedName(MODID + ":" + name);
        }
        
        @Override
        public TileEntity createNewTileEntity(World worldIn, int meta)
        {
            return new OBJTesseractTileEntity();
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
            OBJTesseractTileEntity tileEntity = (OBJTesseractTileEntity) world.getTileEntity(pos);
            OBJModel.OBJState retState = new OBJModel.OBJState(tileEntity == null ? Lists.newArrayList(OBJModel.Group.ALL) : tileEntity.visible, true);
            return ((IExtendedBlockState) this.state.getBaseState()).withProperty(OBJModel.OBJProperty.instance, retState);
        }
        
        @Override
        public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ)
        {
            if (world.getTileEntity(pos) == null) world.setTileEntity(pos, new OBJTesseractTileEntity());
            OBJTesseractTileEntity tileEntity = (OBJTesseractTileEntity) world.getTileEntity(pos);
            IModel model = ModelLoaderRegistry.getMissingModel();
            try
            {
                model = ModelLoaderRegistry.getModel(new ResourceLocation(MODID.toLowerCase() + ":" + "block/tesseract.obj"));
            }
            catch (IOException e)
            {
                model = ModelLoaderRegistry.getMissingModel();
            }
            
            if (player.isSneaking())
            {
                tileEntity.decrement();
            }
            else
            {
                if (model != ModelLoaderRegistry.getMissingModel())
                {
                    tileEntity.setMax(((OBJModel) model).getMatLib().getGroups().keySet().size() - 1);
                    tileEntity.increment();
                }
            }
            
            if (world.isRemote)
            {
                OBJBakedModel objBaked = (OBJBakedModel) Minecraft.getMinecraft().getBlockRendererDispatcher().getModelFromBlockState(state, world, pos);
                objBaked.scheduleRebake();  //not necessarily needed for this specific case, but is available
            }
            world.markBlockRangeForRenderUpdate(pos, pos);
            return false;
        }
        
        @Override
        public boolean hasTileEntity(IBlockState state)
        {
            return true;
        }
    }
    
    public static class OBJTesseractTileEntity extends TileEntity
    {
        private int counter = 1;
        private int max = 2;
        public List<String> visible = new ArrayList<String>();
        
        public OBJTesseractTileEntity()
        {
            this.visible.add(OBJModel.Group.ALL);
        }
        
        public void increment()
        {
            if (this.visible.contains(OBJModel.Group.ALL)) this.visible.remove(OBJModel.Group.ALL);
            if (this.counter == max)
            {
                this.counter = 0;
                this.visible.clear();
            }
            this.counter++;
            this.visible.add(Integer.toString(this.counter));
            ChatComponentText text = new ChatComponentText("" + this.counter);
            if (this.worldObj.isRemote) Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(text);
        }
        
        public void decrement()
        {
            if (this.visible.contains(OBJModel.Group.ALL)) this.visible.remove(OBJModel.Group.ALL);
            if (this.counter == 1)
            {
                this.counter = max + 1;
                for (int i = 1; i < max; i++) this.visible.add(Integer.toString(i));
            }
            this.visible.remove(Integer.toString(this.counter));
            this.counter--;
            ChatComponentText text = new ChatComponentText("" + this.counter);
            if (this.worldObj.isRemote) Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(text);
        }
        
        public void reset()
        {
            this.counter = 1;
            this.max = 2;
            this.visible.clear();
            this.visible.add(Integer.toString(this.counter));
        }
        
        public int getMax()
        {
            return this.max;
        }
        
        public void setMax(int max)
        {
            this.max = max;
        }
        
        public void setToMax()
        {
            this.counter = this.max;
        }
    }
    
    /**
     * This block demonstrates how to utilize the vertex coloring feature 
     * of the OBJ loader. See 'vertex_coloring.obj' and 'vertex_coloring.mtl' in
     * 'test/resources/assets/forgedebugmodelloaderregistry/models/block/', to properly
     * utilize this feature an obj file must have 1 'usemtl' key before every vertex as shown,
     * having less 'usemtl' lines than 'v' lines will result in the faces having that material's
     * color instead of each vertex.
     * @author shadekiller666
     *
     */
    public static class OBJVertexColoring1 extends Block
    {
        public static final OBJVertexColoring1 instance = new OBJVertexColoring1();
        public static final String name = "OBJVertexColoring1";
        
        private OBJVertexColoring1()
        {
            super(Material.iron);
            setCreativeTab(CreativeTabs.tabBlock);
            setUnlocalizedName(name);
        }
        
        @Override
        public boolean isOpaqueCube() { return false; }

        @Override
        public boolean isFullCube() { return false; }

        @Override
        public boolean isVisuallyOpaque() { return false; }
    }
    
    /**
     * This block demonstrates how to use IProperties and IUnlistedProperties together
     * in the same ExtendedBlockState. Similar to pistons, this block will face the player
     * when placed. Unlike pistons, however; this block's model is an eyeball, because 
     * the OBJ loader can load spheres.
     * @author shadekiller666
     *
     */
    public static class OBJDirectionEye extends Block
    {
        public static final PropertyDirection FACING = PropertyDirection.create("facing");
        public static final OBJDirectionEye instance = new OBJDirectionEye();
        public static final String name = "OBJDirectionEye";
        private ExtendedBlockState state = new ExtendedBlockState(this, new IProperty[] {FACING}, new IUnlistedProperty[]{OBJModel.OBJProperty.instance});
        
        private OBJDirectionEye()
        {
            super(Material.iron);
            setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
            setCreativeTab(CreativeTabs.tabBlock);
            setUnlocalizedName(name);
        }
        
        @Override
        public IBlockState onBlockPlaced(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
        {
            return this.getDefaultState().withProperty(FACING, getFacingFromEntity(world, pos, placer));
        }
        
        @Override
        public IBlockState getStateFromMeta(int meta)
        {
            return this.getDefaultState().withProperty(FACING, EnumFacing.getFront(meta));
        }
        
        @Override
        public int getMetaFromState(IBlockState state)
        {
            return ((EnumFacing) state.getValue(FACING)).getIndex();
        }
        
        @Override
        @SideOnly(Side.CLIENT)
        public IBlockState getStateForEntityRender(IBlockState state)
        {
            return this.getDefaultState().withProperty(FACING, EnumFacing.NORTH);
        }
        
        @Override
        public BlockState createBlockState()
        {
            return new ExtendedBlockState(this, new IProperty[] {FACING}, new IUnlistedProperty[] {OBJModel.OBJProperty.instance});
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
            EnumFacing facing = (EnumFacing) state.getValue(FACING);
            TRSRTransformation transform = new TRSRTransformation(facing);
            OBJModel.OBJState retState = new OBJModel.OBJState(Arrays.asList(new String[]{OBJModel.Group.ALL}), true, transform);
            return ((IExtendedBlockState) state).withProperty(OBJModel.OBJProperty.instance, retState);
        }
        
        public static EnumFacing getFacingFromEntity(World worldIn, BlockPos clickedBlock, EntityLivingBase entityIn)
        {
            if (MathHelper.abs((float)entityIn.posX - (float)clickedBlock.getX()) < 2.0F && MathHelper.abs((float)entityIn.posZ - (float)clickedBlock.getZ()) < 2.0F)
            {
                double d0 = entityIn.posY + (double)entityIn.getEyeHeight();

                if (d0 - (double)clickedBlock.getY() > 2.0D)
                {
                    return EnumFacing.DOWN;
                }

                if ((double)clickedBlock.getY() - d0 > 0.0D)
                {
                    return EnumFacing.UP;
                }
            }

            return entityIn.getHorizontalFacing();
        }
    }
    
    /**
     * This block uses the same model as CustomModelBlock3 does, but 
     * this class allows the player to cycle the colors of each vertex to black
     * and then back to the original color when right clicking on the block.
     * @author shadekiller666
     *
     */
    public static class OBJVertexColoring2 extends Block implements ITileEntityProvider
    {
        public static final OBJVertexColoring2 instance = new OBJVertexColoring2(); 
        public static final String name = "OBJVertexColoring2";
        
        private OBJVertexColoring2()
        {
            super(Material.iron);
            setCreativeTab(CreativeTabs.tabBlock);
            setUnlocalizedName(name);
        }
        
        @Override
        public TileEntity createNewTileEntity(World worldIn, int meta)
        {
            return new OBJVertexColoring2TileEntity();
        }
        
        @Override
        public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ)
        {
            if (world.getTileEntity(pos) != null && world.getTileEntity(pos) instanceof OBJVertexColoring2TileEntity)
            {
                ((OBJVertexColoring2TileEntity) world.getTileEntity(pos)).cycleColors();
            }
            return false;
        }
    }
    
    public static class OBJVertexColoring2TileEntity extends TileEntity
    {
        private int index = 0;
        private int maxIndex = 1;
        private List<Vector4f> colorList = new ArrayList<Vector4f>();
        private boolean hasFilledList = false;
        private boolean shouldIncrement = true;
        
        public OBJVertexColoring2TileEntity() {}
        
        public void cycleColors()
        {
            if (this.worldObj.isRemote)
            {
                FMLLog.info("%b", shouldIncrement);
                IBakedModel bakedModel = Minecraft.getMinecraft().getBlockRendererDispatcher().getModelFromBlockState(this.worldObj.getBlockState(this.pos), this.worldObj, this.pos);
                if (bakedModel != null && bakedModel instanceof OBJBakedModel)
                {
                    OBJBakedModel objBaked = (OBJBakedModel) bakedModel;
                    ImmutableList<String> materialNames = objBaked.getModel().getMatLib().getMaterialNames();
                    if (!hasFilledList)
                    {
                        for (String name : materialNames)
                        {
                            if (!name.equals(OBJModel.Material.WHITE_NAME))
                            {
                                colorList.add(objBaked.getModel().getMatLib().getMaterial(name).getColor());
                            }
                        }
                        hasFilledList = true;
                    }
                    maxIndex = materialNames.size();
                    if (this.shouldIncrement && index + 1 < maxIndex)
                    {
                        FMLLog.info("incrementing");
                        String name = materialNames.get(index);
                        objBaked.getModel().getMatLib().changeMaterialColor(name, 0xFF000000);
                        objBaked.scheduleRebake();
                        index++;
                    }
                    else if (index - 1 >= 0)
                    {
                        index--;
                        this.shouldIncrement = index == 0;
                        int color = 0;
                        color |= (int) (colorList.get(index).getW() * 255) << 24;
                        color |= (int) (colorList.get(index).getX() * 255) << 16;
                        color |= (int) (colorList.get(index).getY() * 255) << 8;
                        color |= (int) (colorList.get(index).getZ() * 255);
                        String name = materialNames.get(index);
                        if (!name.equals(OBJModel.Material.WHITE_NAME))
                        {
                            objBaked.getModel().getMatLib().changeMaterialColor(name, color);
                            objBaked.scheduleRebake();
                        }
                    }
                    this.worldObj.markBlockRangeForRenderUpdate(this.pos, this.pos);
                }
            }
        }
    }
    
    /**
     * This block is a debug block that faces the player when placed, like a piston.
     * @author shadekiller666
     *
     */
    public static class OBJDirectionBlock extends Block
    {
        public static final PropertyDirection FACING = PropertyDirection.create("facing");
        public static final OBJDirectionBlock instance = new OBJDirectionBlock();
        public static final String name = "OBJDirectionBlock";
        public ExtendedBlockState state = new ExtendedBlockState(this, new IProperty[]{FACING}, new IUnlistedProperty[]{OBJModel.OBJProperty.instance});
        
        private OBJDirectionBlock()
        {
            super(Material.iron);
            this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
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
        public IBlockState onBlockPlaced(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
        {
            return this.getDefaultState().withProperty(FACING, getFacingFromEntity(world, pos, placer));
        }
        
        @Override
        public IBlockState getStateFromMeta(int meta)
        {
            return this.getDefaultState().withProperty(FACING, EnumFacing.getFront(meta));
        }
        
        @Override
        public int getMetaFromState(IBlockState state)
        {
            return ((EnumFacing) state.getValue(FACING)).getIndex();
        }
        
        @Override
        public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos)
        {
            //Only return an IExtendedBlockState from this method and createState(), otherwise block placement will break!
            EnumFacing facing = (EnumFacing) state.getValue(FACING);
            TRSRTransformation transform = new TRSRTransformation(facing);
            OBJModel.OBJState newState = new OBJModel.OBJState(Lists.newArrayList(OBJModel.Group.ALL), true, transform);
            return ((IExtendedBlockState) state).withProperty(OBJModel.OBJProperty.instance, newState);
        }
        
        @Override
        public BlockState createBlockState()
        {
            return new ExtendedBlockState(this, new IProperty[]{FACING}, new IUnlistedProperty[]{OBJModel.OBJProperty.instance});
        }
        
        public static EnumFacing getFacingFromEntity(World worldIn, BlockPos clickedBlock, EntityLivingBase entityIn)
        {
            if (MathHelper.abs((float)entityIn.posX - (float)clickedBlock.getX()) < 2.0F && MathHelper.abs((float)entityIn.posZ - (float)clickedBlock.getZ()) < 2.0F)
            {
                double d0 = entityIn.posY + (double)entityIn.getEyeHeight();

                if (d0 - (double)clickedBlock.getY() > 2.0D)
                {
                    return EnumFacing.UP;
                }

                if ((double)clickedBlock.getY() - d0 > 0.0D)
                {
                    return EnumFacing.DOWN;
                }
            }

            return entityIn.getHorizontalFacing().getOpposite();
        }
    }
    
    /**
     * This block is a testing block that will be used to test the use
     * of "custom" data defined in a forge blockstate json. WIP, ignore for now.
     * @author shadekiller666
     *
     */
    public static class OBJCustomDataBlock extends Block
    {
        public static final PropertyBool NORTH = PropertyBool.create("north");
        public static final PropertyBool SOUTH = PropertyBool.create("south");
        public static final PropertyBool WEST = PropertyBool.create("west");
        public static final PropertyBool EAST = PropertyBool.create("east");
        public static final OBJCustomDataBlock instance = new OBJCustomDataBlock();
        public static final String name = "OBJCustomDataBlock";
        
        private OBJCustomDataBlock()
        {
            super(Material.iron);
            this.setDefaultState(this.blockState.getBaseState().withProperty(NORTH, false).withProperty(SOUTH, false).withProperty(WEST, false).withProperty(EAST, false));
            setCreativeTab(CreativeTabs.tabBlock);
            setUnlocalizedName(MODID + ":" + name);
        }
        
        @Override
        public boolean isOpaqueCube()
        {
            return false;
        }
        
        @Override
        public boolean isFullCube()
        {
            return false;
        }
        
        @Override
        public int getMetaFromState(IBlockState state)
        {
            return 0;
        }
        
        public boolean canConnectTo(IBlockAccess world, BlockPos pos)
        {
            Block block = world.getBlockState(pos).getBlock();
            return block instanceof OBJCustomDataBlock;
        }
        
        @Override
        public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos)
        {
            return state.withProperty(NORTH, this.canConnectTo(world, pos.north())).withProperty(SOUTH, this.canConnectTo(world, pos.south())).withProperty(WEST, this.canConnectTo(world, pos.west())).withProperty(EAST, this.canConnectTo(world, pos.east()));
        }
        
        @Override
        public BlockState createBlockState()
        {
            return new BlockState(this, new IProperty[]{NORTH, SOUTH, WEST, EAST});
        }
    }
    
    /**
     * This block uses the same model as CustomModelBlock4, but instead of facing the
     * player when placed, this one ALWAYS faces the player. I know, creepy right?
     * @author shadekiller666
     *
     */
    public static class OBJDynamicEye extends Block implements ITileEntityProvider
    {
        public static final OBJDynamicEye instance = new OBJDynamicEye();
        public static final String name = "OBJDynamicEye";
        public ExtendedBlockState state = new ExtendedBlockState(this, new IProperty[0], new IUnlistedProperty[] {OBJModel.OBJProperty.instance});
        private OBJDynamicEye()
        {
            super(Material.iron);
            setCreativeTab(CreativeTabs.tabBlock);
            setUnlocalizedName(MODID + ":" + name);
        }
        
        @Override
        public TileEntity createNewTileEntity(World worldIn, int meta)
        {
            return new OBJDynamicEyeTileEntity();
        }
        
        @Override
        public boolean isOpaqueCube()
        {
            return false;
        }
        
        @Override
        public boolean isFullCube()
        {
            return false;
        }
        
        @Override
        public boolean hasTileEntity(IBlockState state)
        {
            return true;
        }
        
        @Override
        public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos)
        {
            if (world.getTileEntity(pos) != null && world.getTileEntity(pos) instanceof OBJDynamicEyeTileEntity)
            {
                OBJDynamicEyeTileEntity te = (OBJDynamicEyeTileEntity) world.getTileEntity(pos);
                if (te.state != null)
                {
                    return ((IExtendedBlockState) this.state.getBaseState()).withProperty(OBJModel.OBJProperty.instance, te.state);
                }
            }
            return state;
        }
        
        @Override
        public BlockState createBlockState()
        {
            return new ExtendedBlockState(this, new IProperty[0], new IUnlistedProperty[] {OBJModel.OBJProperty.instance});
        }
    }
    
    public static class OBJDynamicEyeTileEntity extends TileEntity implements ITickable
    {
        public OBJModel.OBJState state;
        
        public OBJDynamicEyeTileEntity()
        {
            this.state = new OBJModel.OBJState(Lists.newArrayList(OBJModel.Group.ALL), true);
        }
        
        @Override
        public void update()
        {
            if (this.worldObj.isRemote)
            {
                Vector3d teLoc = new Vector3d(this.getPos().getX(), this.getPos().getY(), this.getPos().getZ());
                EntityPlayer player = Minecraft.getMinecraft().thePlayer;
                Vector3d playerLoc = new Vector3d();
                playerLoc.setX(player.posX);
                playerLoc.setY(player.posY + player.getEyeHeight());
                playerLoc.setZ(player.posZ);
                Vector3d lookVec = new Vector3d(playerLoc.getX() - teLoc.getX(), playerLoc.getY() - teLoc.getY(), playerLoc.getZ() - teLoc.getZ());
                double angleYaw = Math.atan2(lookVec.getZ(), lookVec.getX()) - Math.PI/2d;
                double anglePitch = Math.atan2(lookVec.getY(), Math.sqrt(lookVec.getX() * lookVec.getX() + lookVec.getZ() * lookVec.getZ()));
                AxisAngle4d yaw = new AxisAngle4d(0, 1, 0, -angleYaw);
                AxisAngle4d pitch = new AxisAngle4d(1, 0, 0, -anglePitch);
                Quat4f rot = new Quat4f(0, 0, 0, 1);
                Quat4f yawQuat = new Quat4f();
                Quat4f pitchQuat = new Quat4f();
                yawQuat.set(yaw);
                rot.mul(yawQuat);
                pitchQuat.set(pitch);
                rot.mul(pitchQuat);
                Matrix4f matrix = new Matrix4f();
                matrix.setIdentity();
                matrix.setRotation(rot);
                TRSRTransformation transform = new TRSRTransformation(matrix);
                transform = TRSRTransformation.blockCenterToCorner(transform);
                this.state = new OBJModel.OBJState(Lists.newArrayList(OBJModel.Group.ALL), true, transform);
                this.worldObj.markBlockRangeForRenderUpdate(this.pos, this.pos);
            }
        }
    }
}
