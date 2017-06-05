package net.minecraftforge.debug;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.UnmodifiableIterator;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.b3d.B3DLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.model.IModelPart;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.Models;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.common.property.Properties;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.Logger;

import javax.vecmath.AxisAngle4d;
import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector4f;
import java.util.ArrayList;
import java.util.List;

@Mod(modid = ModelLoaderRegistryDebug.MODID, name = "ForgeDebugModelLoaderRegistry", version = ModelLoaderRegistryDebug.VERSION, acceptableRemoteVersions = "*")
public class ModelLoaderRegistryDebug
{
    public static final boolean ENABLED = false;
    public static final String MODID = "forgedebugmodelloaderregistry";
    public static final String VERSION = "1.0";
    private static Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        if (!ENABLED)
        {
            return;
        }

        logger = event.getModLog();
        List<Block> blocks = Lists.newArrayList();
        blocks.add(CustomModelBlock.instance);
        blocks.add(OBJTesseractBlock.instance);
        blocks.add(OBJVertexColoring1.instance);
        //blocks.add(OBJDirectionEye.instance);
        blocks.add(OBJVertexColoring2.instance);
        blocks.add(OBJDirectionBlock.instance);
        blocks.add(OBJCustomDataBlock.instance);
        //blocks.add(OBJDynamicEye.instance);
        for (Block block : blocks)
        {
            GameRegistry.register(block);
            GameRegistry.register(new ItemBlock(block).setRegistryName(block.getRegistryName()));
        }
        GameRegistry.registerTileEntity(OBJTesseractTileEntity.class, OBJTesseractBlock.name);
        GameRegistry.registerTileEntity(OBJVertexColoring2TileEntity.class, OBJVertexColoring2.name);
        //GameRegistry.registerTileEntity(OBJDynamicEyeTileEntity.class, OBJDynamicEye.name);
        if (event.getSide() == Side.CLIENT)
        {
            clientPreInit();
        }
    }

    private void clientPreInit()
    {
        B3DLoader.INSTANCE.addDomain(MODID.toLowerCase());
        Item item = Item.getItemFromBlock(CustomModelBlock.instance);
        ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(MODID.toLowerCase() + ":" + CustomModelBlock.name, "inventory"));

        OBJLoader.INSTANCE.addDomain(MODID.toLowerCase());
        Item item2 = Item.getItemFromBlock(OBJTesseractBlock.instance);
        ModelLoader.setCustomModelResourceLocation(item2, 0, new ModelResourceLocation(MODID.toLowerCase() + ":" + OBJTesseractBlock.name, "inventory"));

        Item item3 = Item.getItemFromBlock(OBJVertexColoring1.instance);
        ModelLoader.setCustomModelResourceLocation(item3, 0, new ModelResourceLocation(MODID.toLowerCase() + ":" + OBJVertexColoring1.name, "inventory"));

        //Item item4 = Item.getItemFromBlock(OBJDirectionEye.instance);
        //ModelLoader.setCustomModelResourceLocation(item4, 0, new ModelResourceLocation(MODID.toLowerCase() + ":" + OBJDirectionEye.name, "inventory"));

        Item item5 = Item.getItemFromBlock(OBJVertexColoring2.instance);
        ModelLoader.setCustomModelResourceLocation(item5, 0, new ModelResourceLocation(MODID.toLowerCase() + ":" + OBJVertexColoring2.name, "inventory"));

        Item item6 = Item.getItemFromBlock(OBJDirectionBlock.instance);
        ModelLoader.setCustomModelResourceLocation(item6, 0, new ModelResourceLocation(MODID.toLowerCase() + ":" + OBJDirectionBlock.name, "inventory"));

        Item item7 = Item.getItemFromBlock(OBJCustomDataBlock.instance);
        ModelLoader.setCustomModelResourceLocation(item7, 0, new ModelResourceLocation(MODID.toLowerCase() + ":" + OBJCustomDataBlock.name, "inventory"));

        //Item item8 = Item.getItemFromBlock(OBJDynamicEye.instance);
        //ModelLoader.setCustomModelResourceLocation(item8, 0, new ModelResourceLocation(MODID.toLowerCase() + ":" + OBJDynamicEye.name, "inventory"));
    }

    public static class CustomModelBlock extends Block
    {
        public static final PropertyDirection FACING = PropertyDirection.create("facing");
        public static final CustomModelBlock instance = new CustomModelBlock();
        public static final String name = "custom_model_block";
        private int counter = 1;

        private CustomModelBlock()
        {
            super(Material.IRON);
            this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
            setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
            setUnlocalizedName(MODID + ":" + name);
            setRegistryName(new ResourceLocation(MODID, name));
        }

        @Override
        public boolean isOpaqueCube(IBlockState state)
        {
            return false;
        }

        @Override
        public boolean isFullCube(IBlockState state)
        {
            return false;
        }

        @Override
        public boolean causesSuffocation(IBlockState state)
        {
            return false;
        }

        @Override
        public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
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
            return state.getValue(FACING).getIndex();
        }

        @Override
        public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos)
        {
            //Only return an IExtendedBlockState from this method and createState(), otherwise block placement might break!
            B3DLoader.B3DState newState = new B3DLoader.B3DState(null, counter);
            return ((IExtendedBlockState) state).withProperty(Properties.AnimationProperty, newState);
        }

        @Override
        public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
        {
            if (world.isRemote)
            {
                logger.info("click {}", counter);
                if (player.isSneaking())
                {
                    counter--;
                }
                else
                {
                    counter++;
                }
                //if(counter >= model.getNode().getKeys().size()) counter = 0;
                world.markBlockRangeForRenderUpdate(pos, pos);
            }
            return false;
        }

        @Override
        public BlockStateContainer createBlockState()
        {
            return new ExtendedBlockState(this, new IProperty[]{FACING}, new IUnlistedProperty[]{Properties.AnimationProperty});
        }

        public static EnumFacing getFacingFromEntity(World worldIn, BlockPos clickedBlock, EntityLivingBase entityIn)
        {
            if (MathHelper.abs((float) entityIn.posX - (float) clickedBlock.getX()) < 2.0F && MathHelper.abs((float) entityIn.posZ - (float) clickedBlock.getZ()) < 2.0F)
            {
                double d0 = entityIn.posY + (double) entityIn.getEyeHeight();

                if (d0 - (double) clickedBlock.getY() > 2.0D)
                {
                    return EnumFacing.UP;
                }

                if ((double) clickedBlock.getY() - d0 > 0.0D)
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
        public static final String name = "obj_tesseract_block";

        private OBJTesseractBlock()
        {
            super(Material.IRON);
            setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
            setUnlocalizedName(MODID + ":" + name);
            setRegistryName(new ResourceLocation(MODID, name));
        }

        @Override
        public TileEntity createNewTileEntity(World worldIn, int meta)
        {
            return new OBJTesseractTileEntity();
        }

        @Override
        public boolean isOpaqueCube(IBlockState state)
        {
            return false;
        }

        @Override
        public boolean isFullCube(IBlockState state)
        {
            return false;
        }

        @Override
        public boolean causesSuffocation(IBlockState state)
        {
            return false;
        }

        @Override
        public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
        {
            if (world.getTileEntity(pos) == null)
            {
                world.setTileEntity(pos, new OBJTesseractTileEntity());
            }
            OBJTesseractTileEntity tileEntity = (OBJTesseractTileEntity) world.getTileEntity(pos);

            if (player.isSneaking())
            {
                tileEntity.decrement();
            }
            else
            {
                tileEntity.increment();
            }

            world.markBlockRangeForRenderUpdate(pos, pos);
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
            if (world.getTileEntity(pos) != null && world.getTileEntity(pos) instanceof OBJTesseractTileEntity)
            {
                OBJTesseractTileEntity te = (OBJTesseractTileEntity) world.getTileEntity(pos);
                return ((IExtendedBlockState) state).withProperty(Properties.AnimationProperty, te.state);
            }
            return state;
        }

        @Override
        public BlockStateContainer createBlockState()
        {
            return new ExtendedBlockState(this, new IProperty[0], new IUnlistedProperty[]{Properties.AnimationProperty});
        }
    }

    public static class OBJTesseractTileEntity extends TileEntity
    {
        private int counter = 1;
        private int max = 32;
        private final List<String> hidden = new ArrayList<String>();
        private final IModelState state = new IModelState()
        {
            private final Optional<TRSRTransformation> value = Optional.of(TRSRTransformation.identity());

            @Override
            public Optional<TRSRTransformation> apply(Optional<? extends IModelPart> part)
            {
                if (part.isPresent())
                {
                    // This whole thing is subject to change, but should do for now.
                    UnmodifiableIterator<String> parts = Models.getParts(part.get());
                    if (parts.hasNext())
                    {
                        String name = parts.next();
                        // only interested in the root level
                        if (!parts.hasNext() && hidden.contains(name))
                        {
                            return value;
                        }
                    }
                }
                return Optional.absent();
            }
        };

        public void increment()
        {
            if (this.counter == max)
            {
                this.counter = 0;
                this.hidden.clear();
            }
            this.counter++;
            this.hidden.add(Integer.toString(this.counter));
            TextComponentString text = new TextComponentString("" + this.counter);
            if (this.world.isRemote)
            {
                Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(text);
            }
        }

        public void decrement()
        {
            if (this.counter == 1)
            {
                this.counter = max + 1;
                for (int i = 1; i < max; i++) this.hidden.add(Integer.toString(i));
            }
            this.hidden.remove(Integer.toString(this.counter));
            this.counter--;
            TextComponentString text = new TextComponentString("" + this.counter);
            if (this.world.isRemote)
            {
                Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(text);
            }
        }

        public void setMax(int max)
        {
            this.max = max;
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
        public static final String name = "obj_vertex_coloring1";

        private OBJVertexColoring1()
        {
            super(Material.IRON);
            setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
            setUnlocalizedName(name);
            setRegistryName(new ResourceLocation(MODID, name));
        }

        @Override
        public boolean isOpaqueCube(IBlockState state)
        {
            return false;
        }

        @Override
        public boolean isFullCube(IBlockState state)
        {
            return false;
        }

        @Override
        public boolean causesSuffocation(IBlockState state)
        {
            return false;
        }
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
        public static final String name = "obj_direction_eye";

        private OBJDirectionEye()
        {
            super(Material.IRON);
            setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
            setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
            setUnlocalizedName(name);
            setRegistryName(new ResourceLocation(MODID, name));
        }

        @Override
        public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
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
            return state.getValue(FACING).getIndex();
        }

        @Override
        public BlockStateContainer createBlockState()
        {
            return new BlockStateContainer(this, FACING);
        }

        @Override
        public boolean isOpaqueCube(IBlockState state)
        {
            return false;
        }

        @Override
        public boolean isFullCube(IBlockState state)
        {
            return false;
        }

        @Override
        public boolean causesSuffocation(IBlockState state)
        {
            return false;
        }

        public static EnumFacing getFacingFromEntity(World worldIn, BlockPos clickedBlock, EntityLivingBase entityIn)
        {
            if (MathHelper.abs((float) entityIn.posX - (float) clickedBlock.getX()) < 2.0F && MathHelper.abs((float) entityIn.posZ - (float) clickedBlock.getZ()) < 2.0F)
            {
                double d0 = entityIn.posY + (double) entityIn.getEyeHeight();

                if (d0 - (double) clickedBlock.getY() > 2.0D)
                {
                    return EnumFacing.DOWN;
                }

                if ((double) clickedBlock.getY() - d0 > 0.0D)
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
        public static final String name = "obj_vertex_coloring2";

        private OBJVertexColoring2()
        {
            super(Material.IRON);
            setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
            setUnlocalizedName(name);
            setRegistryName(new ResourceLocation(MODID, name));
        }

        @Override
        public TileEntity createNewTileEntity(World worldIn, int meta)
        {
            return new OBJVertexColoring2TileEntity();
        }

        @Override
        public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
        {
            if (world.getTileEntity(pos) != null && world.getTileEntity(pos) instanceof OBJVertexColoring2TileEntity)
            {
                ((OBJVertexColoring2TileEntity) world.getTileEntity(pos)).cycleColors();
            }
            return false;
        }
    }

    @SuppressWarnings("unused")
    public static class OBJVertexColoring2TileEntity extends TileEntity
    {
        private int index = 0;
        private int maxIndex = 1;
        private List<Vector4f> colorList = new ArrayList<Vector4f>();
        private boolean hasFilledList = false;
        private boolean shouldIncrement = true;

        public OBJVertexColoring2TileEntity()
        {
        }

        public void cycleColors()
        {
            if (this.world.isRemote)
            {
                logger.info(shouldIncrement);
                /*
                IBakedModel bakedModel = Minecraft.getMinecraft().getBlockRendererDispatcher().getModelFromBlockState(this.world.getBlockState(this.pos), this.world, this.pos);
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
                        // no
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
                            // FIXME
                            objBaked.getModel().getMatLib().changeMaterialColor(name, color);
                            objBaked.scheduleRebake();
                        }
                    }
                    this.world.markBlockRangeForRenderUpdate(this.pos, this.pos);
                }*/
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
        public static final String name = "obj_direction_block";

        private OBJDirectionBlock()
        {
            super(Material.IRON);
            this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
            setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
            setUnlocalizedName(MODID + ":" + name);
            setRegistryName(new ResourceLocation(MODID, name));
        }

        @Override
        public boolean isOpaqueCube(IBlockState state)
        {
            return false;
        }

        @Override
        public boolean isFullCube(IBlockState state)
        {
            return false;
        }

        @Override
        public boolean causesSuffocation(IBlockState state)
        {
            return false;
        }

        @Override
        public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
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
            return state.getValue(FACING).getIndex();
        }

        @Override
        public BlockStateContainer createBlockState()
        {
            return new BlockStateContainer(this, FACING);
        }

        public static EnumFacing getFacingFromEntity(World worldIn, BlockPos clickedBlock, EntityLivingBase entityIn)
        {
            if (MathHelper.abs((float) entityIn.posX - (float) clickedBlock.getX()) < 2.0F && MathHelper.abs((float) entityIn.posZ - (float) clickedBlock.getZ()) < 2.0F)
            {
                double d0 = entityIn.posY + (double) entityIn.getEyeHeight();

                if (d0 - (double) clickedBlock.getY() > 2.0D)
                {
                    return EnumFacing.UP;
                }

                if ((double) clickedBlock.getY() - d0 > 0.0D)
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
        public static final String name = "obj_custom_data_block";

        private OBJCustomDataBlock()
        {
            super(Material.IRON);
            this.setDefaultState(this.blockState.getBaseState().withProperty(NORTH, false).withProperty(SOUTH, false).withProperty(WEST, false).withProperty(EAST, false));
            setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
            setUnlocalizedName(MODID + ":" + name);
            setRegistryName(new ResourceLocation(MODID, name));
        }

        @Override
        public boolean isOpaqueCube(IBlockState state)
        {
            return false;
        }

        @Override
        public boolean isFullCube(IBlockState state)
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
        public BlockStateContainer createBlockState()
        {
            return new BlockStateContainer(this, NORTH, SOUTH, WEST, EAST);
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
        public static final String name = "obj_dynamic_eye";

        private OBJDynamicEye()
        {
            super(Material.IRON);
            setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
            setUnlocalizedName(MODID + ":" + name);
            setRegistryName(new ResourceLocation(MODID, name));
        }

        @Override
        public TileEntity createNewTileEntity(World worldIn, int meta)
        {
            return new OBJDynamicEyeTileEntity();
        }

        @Override
        public boolean isOpaqueCube(IBlockState state)
        {
            return false;
        }

        @Override
        public boolean isFullCube(IBlockState state)
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
                if (te.transform != TRSRTransformation.identity())
                {
                    return ((IExtendedBlockState) state).withProperty(Properties.AnimationProperty, te.transform);
                }
            }
            return state;
        }

        @Override
        public BlockStateContainer createBlockState()
        {
            return new ExtendedBlockState(this, new IProperty[0], new IUnlistedProperty[]{Properties.AnimationProperty});
        }
    }

    public static class OBJDynamicEyeTileEntity extends TileEntity implements ITickable
    {
        private TRSRTransformation transform = TRSRTransformation.identity();

        @Override
        public void update()
        {
            if (this.world.isRemote)
            {
                Vector3d teLoc = new Vector3d(this.getPos().getX(), this.getPos().getY(), this.getPos().getZ());
                EntityPlayer player = Minecraft.getMinecraft().player;
                Vector3d playerLoc = new Vector3d();
                playerLoc.setX(player.posX);
                playerLoc.setY(player.posY + player.getEyeHeight());
                playerLoc.setZ(player.posZ);
                Vector3d lookVec = new Vector3d(playerLoc.getX() - teLoc.getX(), playerLoc.getY() - teLoc.getY(), playerLoc.getZ() - teLoc.getZ());
                double angleYaw = Math.atan2(lookVec.getZ(), lookVec.getX()) - Math.PI / 2d;
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
                transform = TRSRTransformation.blockCenterToCorner(new TRSRTransformation(matrix));
                this.world.markBlockRangeForRenderUpdate(this.pos, this.pos);
            }
        }
    }
}
