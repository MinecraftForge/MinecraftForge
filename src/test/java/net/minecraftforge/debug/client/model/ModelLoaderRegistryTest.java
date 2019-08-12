/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *//*


package net.minecraftforge.debug.client.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.vecmath.AxisAngle4d;
import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector4f;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.UnmodifiableIterator;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemGroup;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.b3d.B3DLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.model.IModelPart;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.Models;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ObjectHolder;

//@Mod(ModelLoaderRegistryTest.MODID)
public class ModelLoaderRegistryTest
{
    public static final boolean ENABLED = true;
    public static final String MODID = "forgedebugmodelloaderregistry";
    public static final String VERSION = "1.0";
    private static Logger logger;

    //@ObjectHolder(CustomModelBlock.name)
    public static final Block CUSTOM_MODEL_BLOCK = null;
    //@ObjectHolder(OBJTesseractBlock.name)
    public static final Block TESSERACT_BLOCK = null;
    //@ObjectHolder(OBJVertexColoring1.name)
    public static final Block VERTEX_COLOR_1 = null;
    //@ObjectHolder(OBJVertexColoring2.name)
    public static final Block VERTEX_COLOR_2 = null;
    //@ObjectHolder(OBJDirectionBlock.name)
    public static final Block DIRECTION = null;
    //@ObjectHolder(OBJDirectionEye.name)
    public static final Block DIRECTION_EYE = null;
    //@ObjectHolder(OBJDynamicEye.name)
    public static final Block DYNAMIC_EYE = null;
    //@ObjectHolder(OBJCustomDataBlock.name)
    public static final Block CUSTOM_DATA = null;
    
    //@ObjectHolder(OBJTesseractBlock.name)
    public static final TileEntityType<OBJTesseractTileEntity> TESSERACT_TILE = null;
    //@ObjectHolder(OBJVertexColoring2.name)
    public static final TileEntityType<OBJVertexColoring2TileEntity> VERTEX_COLOR_2_TILE = null;
    
    public ModelLoaderRegistryTest()
    {
        if (!ENABLED)
            return;

        logger = LogManager.getLogger();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupClient);
    }
    
    private void setupClient(FMLClientSetupEvent event)
    {
        B3DLoader.INSTANCE.addDomain(MODID);
        OBJLoader.INSTANCE.addDomain(MODID);
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Bus.MOD)
    public static class Registration
    {
        @net.minecraftforge.eventbus.api.SubscribeEvent
        public static void registerBlocks(RegistryEvent.Register<Block> event)
        {
            if (!ENABLED)
                return;
            event.getRegistry().registerAll(
                new CustomModelBlock(),
                new OBJTesseractBlock(),
                new OBJVertexColoring1(),
                new OBJVertexColoring2(),
                new OBJDirectionBlock(),
                //new OBJDirectionEye(),
                //new OBJDynamicEye(),
                new OBJCustomDataBlock()
            );
        }
        
        @SubscribeEvent
        public static void registerTileEntities(RegistryEvent.Register<TileEntityType<?>> event)
        {
            event.getRegistry().registerAll(
                    new TileEntityType<>(OBJTesseractTileEntity::new, null).setRegistryName(OBJTesseractBlock.name),
                    new TileEntityType<>(OBJVertexColoring2TileEntity::new, null).setRegistryName(OBJVertexColoring2.name));
            //GameRegistry.registerTileEntity(OBJDynamicEyeTileEntity.class, OBJDynamicEye.name);
        }

        @net.minecraftforge.eventbus.api.SubscribeEvent
        public static void registerItems(RegistryEvent.Register<Item> event)
        {
            if (!ENABLED)
                return;
            Block[] blocks = {
                CUSTOM_MODEL_BLOCK,
                TESSERACT_BLOCK,
                VERTEX_COLOR_1,
                VERTEX_COLOR_2,
                DIRECTION,
                //DIRECTION_EYE,
                //DYNAMIC_EYE,
                CUSTOM_DATA
            };
            for (Block block : blocks)
                event.getRegistry().register(new ItemBlock(block, new Item.Properties().group(ItemGroup.BUILDING_BLOCKS)).setRegistryName(block.getRegistryName()));
        }

        @net.minecraftforge.eventbus.api.SubscribeEvent
        public static void registerModels(ModelRegistryEvent event)
        {
            if (!ENABLED)
                return;
            B3DLoader.INSTANCE.addDomain(MODID.toLowerCase());
            OBJLoader.INSTANCE.addDomain(MODID.toLowerCase());
            Block[] blocks = {
                CUSTOM_MODEL_BLOCK,
                TESSERACT_BLOCK,
                VERTEX_COLOR_1,
                VERTEX_COLOR_2,
                DIRECTION,
                //DIRECTION_EYE,
                //DYNAMIC_EYE,
                CUSTOM_DATA
            };
        }
    }
    
    static final VoxelShape SHAPE = VoxelShapes.create(1/16D, 1/16D, 1/16D, 15/16D, 15/16D, 15/16D); // We don't really care, just not solid

    public static class CustomModelBlock extends Block
    {
        public static final DirectionProperty FACING = BlockStateProperties.FACING;
        public static final String name = "custom_model_block";
        private int counter = 1;

        private CustomModelBlock()
        {
            super(Properties.create(Material.IRON));
            this.setDefaultState(this.getStateContainer().getBaseState().with(FACING, EnumFacing.NORTH));
            setRegistryName(new ResourceLocation(MODID, name));
        }
        
        @Override
        public VoxelShape getRenderShape(IBlockState state, IBlockReader worldIn, BlockPos pos)
        {
            return SHAPE;
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
        public IBlockState getStateForPlacement(BlockItemUseContext context)
        {
            return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite());
        }

*/
/*        @Override
        public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos)
        {
            //Only return an IExtendedBlockState from this method and createState(), otherwise block placement might break!
            B3DLoader.B3DState newState = new B3DLoader.B3DState(null, counter);
            return ((IExtendedBlockState) state).withProperty(Properties.AnimationProperty, newState);
        }
*//*

        @Override
        public boolean onBlockActivated(IBlockState state, World world, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
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
        public void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder)
        {
            builder.add(FACING);
        }
    }

    */
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
     *//*

    public static class OBJTesseractBlock extends Block
    {
        public static final String name = "obj_tesseract_block";

        private OBJTesseractBlock()
        {
            super(Properties.create(Material.IRON));
            setRegistryName(new ResourceLocation(MODID, name));
        }
        
        @Override
        public boolean hasTileEntity()
        {
            return true;
        }
        
        @Override
        public TileEntity createTileEntity(IBlockState state, IBlockReader world)
        {
            return new OBJTesseractTileEntity();
        }
        
        @Override
        public VoxelShape getRenderShape(IBlockState state, IBlockReader worldIn, BlockPos pos)
        {
            return SHAPE;
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
        public boolean onBlockActivated(IBlockState state, World world, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
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

*/
/*        @Override
        public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos)
        {
            if (world.getTileEntity(pos) != null && world.getTileEntity(pos) instanceof OBJTesseractTileEntity)
            {
                OBJTesseractTileEntity te = (OBJTesseractTileEntity) world.getTileEntity(pos);
                return ((IExtendedBlockState) state).withProperty(Properties.AnimationProperty, te.state);
            }
            return state;
        }*//*

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
                return Optional.empty();
            }
        };
        
        public OBJTesseractTileEntity()
        {
            super(TESSERACT_TILE);
        }

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
                Minecraft.getInstance().ingameGUI.getChatGUI().printChatMessage(text);
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
                Minecraft.getInstance().ingameGUI.getChatGUI().printChatMessage(text);
            }
        }

        public void setMax(int max)
        {
            this.max = max;
        }
    }

    */
/**
     * This block demonstrates how to utilize the vertex coloring feature
     * of the OBJ loader. See 'vertex_coloring.obj' and 'vertex_coloring.mtl' in
     * 'test/resources/assets/forgedebugmodelloaderregistry/models/block/', to properly
     * utilize this feature an obj file must have 1 'usemtl' key before every vertex as shown,
     * having less 'usemtl' lines than 'v' lines will result in the faces having that material's
     * color instead of each vertex.
     * @author shadekiller666
     *
     *//*

    public static class OBJVertexColoring1 extends Block
    {
        public static final String name = "obj_vertex_coloring1";

        private OBJVertexColoring1()
        {
            super(Properties.create(Material.IRON));
            setRegistryName(new ResourceLocation(MODID, name));
        }
        
        @Override
        public VoxelShape getRenderShape(IBlockState state, IBlockReader worldIn, BlockPos pos)
        {
            return SHAPE;
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

    */
/**
     * This block demonstrates how to use IProperties and IUnlistedProperties together
     * in the same ExtendedBlockState. Similar to pistons, this block will face the player
     * when placed. Unlike pistons, however; this block's model is an eyeball, because
     * the OBJ loader can load spheres.
     * @author shadekiller666
     *
     *//*

    public static class OBJDirectionEye extends Block
    {
        public static final DirectionProperty FACING = BlockStateProperties.FACING;
        public static final String name = "obj_direction_eye";

        private OBJDirectionEye()
        {
            super(Properties.create(Material.IRON));
            setDefaultState(this.getStateContainer().getBaseState().with(FACING, EnumFacing.NORTH));
            setRegistryName(new ResourceLocation(MODID, name));
        }

        @Override
        public IBlockState getStateForPlacement(BlockItemUseContext context)
        {
            return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite());
        }

        @Override
        public void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder)
        {
            builder.add(FACING);
        }
        
        @Override
        public VoxelShape getRenderShape(IBlockState state, IBlockReader worldIn, BlockPos pos)
        {
            return SHAPE;
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

    */
/**
     * This block uses the same model as CustomModelBlock3 does, but
     * this class allows the player to cycle the colors of each vertex to black
     * and then back to the original color when right clicking on the block.
     * @author shadekiller666
     *
     *//*

    public static class OBJVertexColoring2 extends Block
    {
        public static final String name = "obj_vertex_coloring2";

        private OBJVertexColoring2()
        {
            super(Properties.create(Material.IRON));
            setRegistryName(new ResourceLocation(MODID, name));
        }

        @Override
        public boolean hasTileEntity(IBlockState state)
        {
            return true;
        }
        
        @Override
        public TileEntity createTileEntity(IBlockState state, IBlockReader world)
        {
            return new OBJVertexColoring2TileEntity();
        }

        @Override
        public boolean onBlockActivated(IBlockState state, World world, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
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
            super(VERTEX_COLOR_2_TILE);
        }

        public void cycleColors()
        {
            if (this.world.isRemote)
            {
                logger.info(shouldIncrement);
                */
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
                }*//*

            }
        }
    }

    */
/**
     * This block is a debug block that faces the player when placed, like a piston.
     * @author shadekiller666
     *
     *//*

    public static class OBJDirectionBlock extends Block
    {
        public static final DirectionProperty FACING = BlockStateProperties.FACING;
        public static final String name = "obj_direction_block";

        private OBJDirectionBlock()
        {
            super(Properties.create(Material.IRON));
            this.setDefaultState(this.getStateContainer().getBaseState().with(FACING, EnumFacing.NORTH));
            setRegistryName(new ResourceLocation(MODID, name));
        }
        
        @Override
        public VoxelShape getRenderShape(IBlockState state, IBlockReader worldIn, BlockPos pos)
        {
            return SHAPE;
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
        public IBlockState getStateForPlacement(BlockItemUseContext context)
        {
            return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite());
        }

        @Override
        public void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder)
        {
            builder.add(FACING);
        }
    }

    */
/**
     * This block is a testing block that will be used to test the use
     * of "custom" data defined in a forge blockstate json. WIP, ignore for now.
     * @author shadekiller666
     *
     *//*

    public static class OBJCustomDataBlock extends Block
    {
        public static final BooleanProperty NORTH = BooleanProperty.create("north");
        public static final BooleanProperty SOUTH = BooleanProperty.create("south");
        public static final BooleanProperty WEST = BooleanProperty.create("west");
        public static final BooleanProperty EAST = BooleanProperty.create("east");
        public static final String name = "obj_custom_data_block";

        private OBJCustomDataBlock()
        {
            super(Properties.create(Material.IRON));
            this.setDefaultState(this.getStateContainer().getBaseState().with(NORTH, false).with(SOUTH, false).with(WEST, false).with(EAST, false));
            setRegistryName(new ResourceLocation(MODID, name));
        }
        
        @Override
        public VoxelShape getRenderShape(IBlockState state, IBlockReader worldIn, BlockPos pos)
        {
            return SHAPE;
        }

        @Override
        public boolean isFullCube(IBlockState state)
        {
            return false;
        }

        public boolean canConnectTo(IBlockReader world, BlockPos pos)
        {
            Block block = world.getBlockState(pos).getBlock();
            return block instanceof OBJCustomDataBlock;
        }
        
        @Override
        public IBlockState updatePostPlacement(IBlockState state, EnumFacing facing, IBlockState facingState, IWorld world, BlockPos pos, BlockPos facingPos)
        {
            return state.with(NORTH, this.canConnectTo(world, pos.north())).with(SOUTH, this.canConnectTo(world, pos.south())).with(WEST, this.canConnectTo(world, pos.west())).with(EAST, this.canConnectTo(world, pos.east()));
        }

        @Override
        protected void fillStateContainer(Builder<Block, IBlockState> builder)
        {
            builder.add(NORTH, SOUTH, WEST, EAST);
        }
    }

    */
/**
     * This block uses the same model as CustomModelBlock4, but instead of facing the
     * player when placed, this one ALWAYS faces the player. I know, creepy right?
     * @author shadekiller666
     *
     *//*

    public static class OBJDynamicEye extends Block
    {
        public static final String name = "obj_dynamic_eye";

        private OBJDynamicEye()
        {
            super(Properties.create(Material.IRON));
            setRegistryName(new ResourceLocation(MODID, name));
        }
        
        @Override
        public TileEntity createTileEntity(IBlockState state, IBlockReader world)
        {
            return new OBJDynamicEyeTileEntity();
        }
        
        @Override
        public VoxelShape getRenderShape(IBlockState state, IBlockReader worldIn, BlockPos pos)
        {
            return SHAPE;
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

*/
/*        @Override
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
        }*//*

    }

    public static class OBJDynamicEyeTileEntity extends TileEntity implements ITickable
    {
        public OBJDynamicEyeTileEntity()
        {
            super(null); // TODO
        }

        private TRSRTransformation transform = TRSRTransformation.identity();

        @Override
        public void tick()
        {
            if (this.world.isRemote)
            {
                Vector3d teLoc = new Vector3d(this.getPos().getX(), this.getPos().getY(), this.getPos().getZ());
                EntityPlayer player = Minecraft.getInstance().player;
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
*/
