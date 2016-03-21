package net.minecraftforge.debug;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3d;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.Attributes;
import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.TRSRTransformation;
import net.minecraftforge.client.model.b3d.B3DLoader;
import net.minecraftforge.client.model.obj.OBJCustomData;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.client.model.obj.OBJModel;
import net.minecraftforge.client.model.pipeline.LightUtil;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Mod(modid = ModelLoaderRegistryDebug.MODID, version = ModelLoaderRegistryDebug.VERSION)
public class ModelLoaderRegistryDebug
{
    public static final String MODID = "ForgeDebugModelLoaderRegistry";
    public static final String VERSION = "1.0";

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        GameRegistry.registerBlock(CustomModelBlock.instance, CustomModelBlock.name);
        GameRegistry.registerBlock(OBJGroupTest.instance, OBJGroupTest.name);
        GameRegistry.registerBlock(OBJVertexColoring.instance, OBJVertexColoring.name);
        GameRegistry.registerBlock(OBJDirectionEye.instance, OBJDirectionEye.name);
        GameRegistry.registerBlock(OBJDynamicEye.instance, OBJDynamicEye.name);
        GameRegistry.registerBlock(OBJDirectionBlock.instance, OBJDirectionBlock.name);
        GameRegistry.registerBlock(OBJClock.instance, OBJClock.name);
        GameRegistry.registerTileEntity(OBJDynamicEyeTileEntity.class, OBJDynamicEye.name);
        GameRegistry.registerTileEntity(OBJClockTileEntity.class, OBJClock.name);
        if (event.getSide() == Side.CLIENT)
            clientPreInit();
    }

    private void clientPreInit()
    {
        B3DLoader.instance.addDomain(MODID.toLowerCase());
        Item item = Item.getItemFromBlock(CustomModelBlock.instance);
        ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(MODID.toLowerCase() + ":" + CustomModelBlock.name, "inventory"));
        
        OBJLoader.instance.addDomain(MODID.toLowerCase());
        ClientRegistry.bindTileEntitySpecialRenderer(OBJClockTileEntity.class, new OBJClockRender());
        
        Item item2 = Item.getItemFromBlock(OBJGroupTest.instance);
        ModelLoader.setCustomModelResourceLocation(item2, 0, new ModelResourceLocation(MODID.toLowerCase() + ":" + OBJGroupTest.name, "inventory"));
        
        Item item3 = Item.getItemFromBlock(OBJVertexColoring.instance);
        ModelLoader.setCustomModelResourceLocation(item3, 0, new ModelResourceLocation(MODID.toLowerCase() + ":" + OBJVertexColoring.name, "inventory"));
        
        Item item4 = Item.getItemFromBlock(OBJDirectionEye.instance);
        ModelLoader.setCustomModelResourceLocation(item4, 0, new ModelResourceLocation(MODID.toLowerCase() + ":" + OBJDirectionEye.name, "inventory"));
        
        Item item5 = Item.getItemFromBlock(OBJDynamicEye.instance);
        ModelLoader.setCustomModelResourceLocation(item5, 0, new ModelResourceLocation(MODID.toLowerCase() + ":" + OBJDynamicEye.name, "inventory"));

        Item item6 = Item.getItemFromBlock(OBJDirectionBlock.instance);
        ModelLoader.setCustomModelResourceLocation(item6, 0, new ModelResourceLocation(MODID.toLowerCase() + ":" + OBJDirectionBlock.name, "inventory"));
        
        Item item7 = Item.getItemFromBlock(OBJClock.instance);
        ModelLoader.setCustomModelResourceLocation(item7, 0, new ModelResourceLocation(MODID.toLowerCase() + ":" + OBJClock.name, "inventory"));
        
        OBJClockRender.init();
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
     * When placed next to any non-air block, the model will "attach"
     * to the block its adjacent to, like a pipe.
     * This block also demonstrates how to change the color of the material(s) applied
     * to the block. This can be performed by right-clicking on the block.
     * @author shadekiller666
     *
     */
    public static class OBJGroupTest extends Block
    {
    	public static final PropertyBool IS_WHITE = PropertyBool.create("is_white");
    	public static final OBJGroupTest instance = new OBJGroupTest();
    	public static final String name = "OBJGroupTest";
    	private ExtendedBlockState state = new ExtendedBlockState(this, new IProperty[] {IS_WHITE}, new IUnlistedProperty[]{OBJModel.OBJProperty.instance});
    	
    	private OBJGroupTest()
    	{
    		super(Material.iron);
    		this.setDefaultState(this.blockState.getBaseState().withProperty(IS_WHITE, false));
    		setCreativeTab(CreativeTabs.tabBlock);
    		setUnlocalizedName(MODID + ":" + name);
    	}
    	
    	@Override
    	public IBlockState getStateFromMeta(int meta)
    	{
    		return this.getDefaultState().withProperty(IS_WHITE, meta > 0);
    	}
    	
    	@Override
    	public int getMetaFromState(IBlockState state)
    	{
    		return state.getValue(IS_WHITE) ? 1 : 0;
    	}
    	
    	@Override
    	public BlockState createBlockState()
    	{
    		return new ExtendedBlockState(this, new IProperty[] {IS_WHITE}, new IUnlistedProperty[] {OBJModel.OBJProperty.instance});
    	}
    	
    	@Override
        public boolean isOpaqueCube() { return false; }

        @Override
        public boolean isFullCube() { return false; }

        @Override
        public boolean isVisuallyOpaque() { return false; }
        
        private List<String> checkConnections(IBlockAccess world, BlockPos pos)
        {
        	List<String> connections = Lists.newArrayList("center");
        	if (world.getBlockState(pos.north()) != null && !world.isAirBlock(pos.north())) connections.add("north");
        	if (world.getBlockState(pos.south()) != null && !world.isAirBlock(pos.south())) connections.add("south");
        	if (world.getBlockState(pos.west()) != null && !world.isAirBlock(pos.west())) connections.add("west");
        	if (world.getBlockState(pos.east()) != null && !world.isAirBlock(pos.east())) connections.add("east");
        	if (world.getBlockState(pos.up()) != null && !world.isAirBlock(pos.up())) connections.add("up");
        	if (world.getBlockState(pos.down()) != null && !world.isAirBlock(pos.down())) connections.add("down");
        	return connections;
        }
        
        @Override
        public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos)
        {
        	OBJModel.OBJState objState = new OBJModel.OBJState(this.checkConnections(world, pos)).setIgnoreHidden(true);
        	if (state.getValue(IS_WHITE)) objState.setMaterialColor("black", 0xFFFFFFFF);
        	else objState.setMaterialColor("black", 0xFF000000);
        	return ((IExtendedBlockState) this.state.getBaseState()).withProperty(OBJModel.OBJProperty.instance, objState);
        }
        
        @Override
        public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ)
        {
        	world.setBlockState(pos, state.cycleProperty(IS_WHITE), 3);
        	return true;
        }
    }
    
    /**
     * This block is intended to demonstrate how to change the visibilities of multiple
     * groups from within a TESR, and how to use the standard model system and a TESR for
     * the same block at the same time. The "backplate" of the digital clock model is
     * rendered by the game itself in the standard fashion. The numbers for the clock
     * are rendered by OBJClockRender from a file containing only one digit's model.
     * @author shadekiller666
     *
     */
    public static class OBJClock extends Block
    {
    	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    	public static final PropertyBool TWENTY_FOUR = PropertyBool.create("twenty_four");
    	public static final PropertyBool SECONDARY = PropertyBool.create("secondary");
    	public static final OBJClock instance = new OBJClock();
    	public static final String name = "OBJClock";
    	private ExtendedBlockState state = new ExtendedBlockState(this, new IProperty[] {FACING, TWENTY_FOUR, SECONDARY}, new IUnlistedProperty[] {OBJModel.OBJProperty.instance});
    	
    	public OBJClock()
    	{
    		super(Material.iron);
    		this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(TWENTY_FOUR, false).withProperty(SECONDARY, false));
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
    	public boolean hasTileEntity(IBlockState state)
    	{
    		return !state.getValue(SECONDARY);
    	}
    	
    	@Override
    	public TileEntity createTileEntity(World world, IBlockState state)
    	{
    		return new OBJClockTileEntity(world, state.getValue(FACING));
    	}
    	
    	private AxisAlignedBB getBounds(EnumFacing facing)
    	{
    		switch (facing)
    		{
    		case NORTH: return new AxisAlignedBB(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0625f);
    		case SOUTH: return new AxisAlignedBB(0.0f, 0.0f, 0.9375f, 1.0f, 1.0f, 1.0f);
    		case WEST: return new AxisAlignedBB(0.0f, 0.0f, 0.0f, 0.0625f, 1.0f, 1.0f);
    		case EAST: return new AxisAlignedBB(0.9375f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
    		default: return new AxisAlignedBB(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
    		}
    	}
    	
    	@Override
    	public void setBlockBoundsBasedOnState(IBlockAccess world, BlockPos pos)
    	{
    		AxisAlignedBB box = this.getBounds(world.getBlockState(pos).getValue(FACING));
    		setBlockBounds((float) box.minX, (float) box.minY, (float) box.minZ, (float) box.maxX, (float) box.maxY, (float) box.maxZ);
    	}
    	
    	@Override
    	public AxisAlignedBB getCollisionBoundingBox(World world, BlockPos pos, IBlockState state)
    	{
    		return null;
    	}
    	
    	@Override
    	public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighbor)
    	{
    		if (neighbor != this && !world.isSideSolid(pos.offset(state.getValue(FACING)), state.getValue(FACING).getOpposite()))
    		{
    			this.breakBlock(world, pos, state);
    		}
    	}
    	
    	@Override
    	public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side)
    	{
    		if (side.getAxis() == EnumFacing.Axis.Y) return false;
    		boolean canPlaceSelf = world.isSideSolid(pos.offset(side.getOpposite()), side);
    		boolean canPlaceNeighbor = 
    				(this.canPlaceBlockAt(world, pos.offset(side.rotateY())) && world.isSideSolid(pos.offset(side.rotateY()).offset(side.getOpposite()), side)) ||
    				(this.canPlaceBlockAt(world, pos.offset(side.rotateYCCW())) && world.isSideSolid(pos.offset(side.rotateYCCW()).offset(side.getOpposite()), side));
    		return canPlaceSelf && canPlaceNeighbor;
    	}
    	
    	@Override
        public IBlockState onBlockPlaced(World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
        {
    		IBlockState retState = this.getDefaultState().withProperty(TWENTY_FOUR, placer.isSneaking());
    		if (this.canPlaceBlockOnSide(world, pos, side))
    		{
    			retState = retState.withProperty(FACING, side.getOpposite());
    			BlockPos right = pos.offset(side.rotateY());
    			BlockPos left = pos.offset(side.rotateYCCW());
    			if (this.canPlaceBlockAt(world, pos) && this.canPlaceBlockAt(world, left) && this.canPlaceBlockOnSide(world, left, side))
    			{
    				retState = retState.withProperty(SECONDARY, false);
    				IBlockState secState = this.getDefaultState().withProperty(FACING, side.getOpposite()).withProperty(TWENTY_FOUR, placer.isSneaking()).withProperty(SECONDARY, true);
    				world.setBlockState(left, secState, 3);
    				
    			}
    			else if (this.canPlaceBlockAt(world, pos) && this.canPlaceBlockAt(world, right) && this.canPlaceBlockOnSide(world, right, side))
    			{
    				retState = retState.withProperty(SECONDARY, true);
    				IBlockState primeState = this.getDefaultState().withProperty(FACING, side.getOpposite()).withProperty(TWENTY_FOUR, placer.isSneaking()).withProperty(SECONDARY, false);
    				world.setBlockState(right, primeState, 3);
    			}
    		}
            return retState;
        }
    	
    	@Override
    	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    	{
    		BlockPos tilePos = !state.getValue(SECONDARY) ? pos.offset(state.getValue(FACING).rotateYCCW()) : pos;
    		if (world.getTileEntity(tilePos) != null && world.getTileEntity(tilePos) instanceof OBJClockTileEntity)
    		{
    			((OBJClockTileEntity) world.getTileEntity(tilePos)).facing = state.getValue(FACING);
    			world.setBlockState(tilePos, state.withProperty(TWENTY_FOUR, ((OBJClockTileEntity) world.getTileEntity(tilePos)).isTwentyFour), 3);
    		}
    	}
    	
    	@Override
    	public void breakBlock(World world, BlockPos pos, IBlockState state)
    	{
    		if (state.getValue(SECONDARY))
    		{
    			world.setBlockToAir(pos.offset(state.getValue(FACING).rotateYCCW()));
    			world.removeTileEntity(pos.offset(state.getValue(FACING).rotateYCCW()));
    			super.breakBlock(world, pos, state);
    		}
    		else
    		{
    			world.setBlockToAir(pos.offset(state.getValue(FACING).rotateY()));
    			super.breakBlock(world, pos, state);
    		}
    	}
    	
    	@Override
    	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitz)
    	{
    		state = state.cycleProperty(TWENTY_FOUR);
    		world.setBlockState(pos, state, 3);
    		if (state.getValue(SECONDARY))
    		{
    			BlockPos primary = pos.offset(state.getValue(FACING).rotateYCCW());
    			world.setBlockState(primary, state.cycleProperty(SECONDARY), 3);
    			if (world.getTileEntity(primary) != null && world.getTileEntity(primary) instanceof OBJClockTileEntity)
    			{
    				((OBJClockTileEntity) world.getTileEntity(primary)).isTwentyFour = state.getValue(TWENTY_FOUR);
    				((OBJClockTileEntity) world.getTileEntity(primary)).facing = state.getValue(FACING);
    			}
    		}
    		else
    		{
    			world.setBlockState(pos.offset(state.getValue(FACING).rotateY()), state.cycleProperty(SECONDARY), 3);
    			if (world.getTileEntity(pos) != null && world.getTileEntity(pos) instanceof OBJClockTileEntity)
    			{
    				((OBJClockTileEntity) world.getTileEntity(pos)).isTwentyFour = state.getValue(TWENTY_FOUR);
    				((OBJClockTileEntity) world.getTileEntity(pos)).facing = state.getValue(FACING);
    			}
    		}
    		return true;
    	}

    	@Override
    	public IBlockState getStateFromMeta(int meta)
    	{
    		return this.getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta & 3)).withProperty(TWENTY_FOUR, (meta & 4) > 0).withProperty(SECONDARY, (meta & 8) > 0);
    	}
    	
    	@Override
    	public int getMetaFromState(IBlockState state)
    	{
    		int meta = 0;
    		if (state.getValue(SECONDARY)) meta |= 8;
    		if (state.getValue(TWENTY_FOUR)) meta |= 4;
    		meta |= state.getValue(FACING).getHorizontalIndex();
    		return meta;
    	}
    	
    	@Override
    	public BlockState createBlockState()
    	{
    		return new ExtendedBlockState(this, new IProperty[] {FACING, TWENTY_FOUR, SECONDARY}, new IUnlistedProperty[] {OBJModel.OBJProperty.instance});
    	}
    	
    	@Override
    	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos)
    	{
//    		OBJModel.OBJState retState = new OBJModel.OBJState(new TRSRTransformation(state.getValue(FACING)));
    		OBJModel.OBJState retState = new OBJModel.OBJState();
    		if (state.getValue(SECONDARY)) retState.setHideAllConfigs();
    		return ((IExtendedBlockState) this.state.getBaseState()).withProperty(OBJModel.OBJProperty.instance, retState);
    	}
    }
    
    public static class OBJClockTileEntity extends TileEntity
    {
    	private boolean isTwentyFour = false;
    	private EnumFacing facing = EnumFacing.NORTH;
    	
    	public OBJClockTileEntity() {}
    	
    	public OBJClockTileEntity(World world, EnumFacing facing)
    	{
    		this.worldObj = world;
    		this.facing = facing;
    	}
    	
    	public Pair<String, String[]> getConfigOperationFor(int index)
    	{
    		switch(index)
    		{
    		case 0: return Pair.of("hide", new String[] {"G"});
    		case 1: return Pair.of("show", new String[] {"E", "F"});
    		case 2: return Pair.of("hide", new String[] {"B", "E"});
    		case 3: return Pair.of("hide", new String[] {"B", "C"});
    		case 4: return Pair.of("hide", new String[] {"A", "C", "D"});
    		case 5: return Pair.of("hide", new String[] {"C", "F"});
    		case 6: return Pair.of("hide", new String[] {"F"});
    		case 7: return Pair.of("show", new String[] {"A", "E", "F"});
    		case 8: return Pair.of("hide", new String[] {});
    		case 9: return Pair.of("hide", new String[] {"C"});
    		default: return null;
    		}
    	}
    	
    	@Override
    	public void writeToNBT(NBTTagCompound compound)
    	{
    		super.writeToNBT(compound);
    		compound.setBoolean("twenty_four", this.isTwentyFour);
    		compound.setInteger("facing", this.facing.getHorizontalIndex());
    	}
    	
    	@Override
    	public void readFromNBT(NBTTagCompound compound)
    	{
    		super.readFromNBT(compound);
    		if (compound.hasKey("twenty_four")) this.isTwentyFour = compound.getBoolean("twenty_four");
    		if (compound.hasKey("facing")) this.facing = EnumFacing.getHorizontal(compound.getInteger("facing"));
    	}
    	
    	@Override
    	public boolean shouldRenderInPass(int pass)
    	{
    		return pass == 1;
    	}
    	
    	@Override
    	public NBTTagCompound getTileData()
    	{
    		NBTTagCompound compound = new NBTTagCompound();
    		compound.setBoolean("twenty_four", this.isTwentyFour);
    		compound.setInteger("facing", this.facing.getHorizontalIndex());
    		return compound;
    	}
    	
    	@Override
    	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
    	{
    		NBTTagCompound compound = pkt.getNbtCompound();
    		if (compound.hasKey("twenty_four")) this.isTwentyFour = compound.getBoolean("twenty_four");
    		if (compound.hasKey("facing")) this.facing = EnumFacing.getHorizontal(compound.getInteger("facing"));
    	}
    	
    	@Override
    	public Packet getDescriptionPacket()
    	{
    		S35PacketUpdateTileEntity packet = new S35PacketUpdateTileEntity(this.getPos(), this.getBlockMetadata(), this.getTileData());
    		return packet;
    	}
    }
    
    public static class OBJClockRender extends TileEntitySpecialRenderer<OBJClockTileEntity>
    {
    	private static Map<String, IFlexibleBakedModel> loadedModels = Maps.newHashMap();
    	private static final ResourceLocation DIGIT = new ResourceLocation(MODID.toLowerCase() + ":block/clock_digit.obj");
    	
    	public static void init()
    	{
    		IResourceManager manager = Minecraft.getMinecraft().getResourceManager();
    		if (manager instanceof IReloadableResourceManager)
    		{
    			((IReloadableResourceManager) manager).registerReloadListener(new IResourceManagerReloadListener()
    			{
    				@Override
    				public void onResourceManagerReload(IResourceManager ignored)
    				{
    					loadedModels.clear();
    				}
    			});
    		}
    	}
    	
    	@Override
    	public void renderTileEntityAt(OBJClockTileEntity te, double x, double y, double z, float partialTicks, int destroyStage)
    	{
    		if (loadedModels.isEmpty())
    		{
    			FMLLog.info("loadedModels was empty, attempting to load model '%s'", DIGIT.toString());

    			IModel model = null;
    			try
    			{
    				model = ModelLoaderRegistry.getModel(DIGIT);
    			}
    			catch (IOException e)
    			{
    				FMLLog.severe("OBJClockRender: Failed to get model '%s' from ModelLoaderRegistry.", DIGIT);
    			}
				
				if (model != null && model instanceof OBJModel)
				{
					FMLLog.info("Successfully obtained model from registry");
					OBJModel objModel = new OBJModel(((OBJModel) model).getMatLib(), ((OBJModel) model).getModelLocation(), ((OBJModel) model).getCustomData());
					OBJCustomData.GroupConfigHandler configHandler = objModel.getCustomData().getConfigHandler();
					OBJCustomData.GroupConfigBuilder builder = configHandler.getConfigBuilder();
					
					for (int i = 0; i <= 9; i++)
					{
						Pair<String, String[]> configOperation = te.getConfigOperationFor(i);
						builder.startNew("" + i);
						if (configOperation.getLeft().equals("hide")) builder.showAll(configOperation.getRight());
						if (configOperation.getLeft().equals("show")) builder.hideAll(configOperation.getRight());
						configHandler.addConfig(builder.build());
					}
							
					for (int i = 0; i <= 9; i++)
					{
						OBJModel.OBJState state = new OBJModel.OBJState(Lists.newArrayList("" + i));
						IFlexibleBakedModel baked = objModel.bake(state, Attributes.DEFAULT_BAKED_FORMAT, ModelLoader.defaultTextureGetter());
						loadedModels.put("" + i, baked);
					}
				}
    		}
    		
    		IFlexibleBakedModel hourTens = null;
    		IFlexibleBakedModel hourOnes = null;
    		IFlexibleBakedModel minsTens = null;
    		IFlexibleBakedModel minsOnes = null;
    		
    		int hour12 = Calendar.getInstance().get(Calendar.HOUR);
    		int hour24 = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
    		int minute = Calendar.getInstance().get(Calendar.MINUTE);
    		
    		if (te.isTwentyFour)
    		{
    			String h24 = Integer.toString(hour24);
    			hourTens = h24.length() > 1 ? loadedModels.get("" + h24.charAt(0)) : null;
    			hourOnes = h24.length() > 1 ? loadedModels.get("" + h24.charAt(1)) : loadedModels.get("" + h24.charAt(0));
    		}
    		else
    		{
    			String h12 = Integer.toString(hour12);
    			if (hour12 == 0) h12 = "12";
    			hourTens = h12.length() > 1 ? loadedModels.get("" + h12.charAt(0)) : null;
    			hourOnes = h12.length() > 1 ? loadedModels.get("" + h12.charAt(1)) : loadedModels.get("" + h12.charAt(0));
    		}
    		
    		String m = Integer.toString(minute);
    		minsTens = m.length() > 1 ? loadedModels.get("" + m.charAt(0)) : loadedModels.get("0");
    		minsOnes = m.length() > 1 ? loadedModels.get("" + m.charAt(1)) : loadedModels.get("" + m.charAt(0));
    		
    		bindTexture(TextureMap.locationBlocksTexture);
    		Tessellator tessellator = Tessellator.getInstance();
    		WorldRenderer worldRenderer = tessellator.getWorldRenderer();
    		
    		GlStateManager.disableLighting();
    		GlStateManager.disableAlpha();
    		GlStateManager.disableRescaleNormal();
    		GlStateManager.enableDepth();
    		GlStateManager.depthMask(true);
    		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    		
    		GlStateManager.pushMatrix();
    		GlStateManager.translate(x, y, z);
    		
    		GlStateManager.pushMatrix();
    		GlStateManager.translate(0.5f, 0.5f, 0.5f);
    		switch (te.facing)
    		{
    		default:
    		case NORTH: break;
    		case SOUTH: GlStateManager.rotate(180, 0, 1, 0); break;
    		case WEST: GlStateManager.rotate(90, 0, 1, 0); break;
    		case EAST: GlStateManager.rotate(270, 0, 1, 0); break;
    		}
    		GlStateManager.translate(-0.5f, -0.5f, -0.5f);
    		
    		if (hourTens != null)
    		{
    			worldRenderer.begin(GL11.GL_QUADS, Attributes.DEFAULT_BAKED_FORMAT);
    			for (BakedQuad quad : hourTens.getGeneralQuads())
    			{
    				LightUtil.renderQuadColor(worldRenderer, quad, 0xFFFFFFFF); //last argument is abgr!
    			}
    			tessellator.draw();
    		}
    		
    		worldRenderer.begin(GL11.GL_QUADS, Attributes.DEFAULT_BAKED_FORMAT);
    		GlStateManager.translate(0.375f, 0.0f, 0.0f);
    		for (BakedQuad quad : hourOnes.getGeneralQuads())
    		{
    			LightUtil.renderQuadColor(worldRenderer, quad, 0xFFFFFFFF);
    		}
    		tessellator.draw();
    		
    		if (minsTens != null)
    		{    			
    			worldRenderer.begin(GL11.GL_QUADS, Attributes.DEFAULT_BAKED_FORMAT);
    			GlStateManager.translate(0.497442f, 0.0f, 0.0f);
    			for (BakedQuad quad : minsTens.getGeneralQuads())
    			{
    				LightUtil.renderQuadColor(worldRenderer, quad, 0xFFFFFFFF);
    			}
    			tessellator.draw();
    		}
    		
    		worldRenderer.begin(GL11.GL_QUADS, Attributes.DEFAULT_BAKED_FORMAT);
    		GlStateManager.translate(0.375f, 0.0f, 0.0f);
    		for (BakedQuad quad : minsOnes.getGeneralQuads())
    		{
    			LightUtil.renderQuadColor(worldRenderer, quad, 0xFFFFFFFF);
    		}
    		tessellator.draw();
    		
    		GlStateManager.popMatrix();
    		GlStateManager.popMatrix();
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
    public static class OBJVertexColoring extends Block
    {
        public static final OBJVertexColoring instance = new OBJVertexColoring();
        public static final String name = "OBJVertexColoring";
        
        private OBJVertexColoring()
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
        
        private OBJDirectionEye()
        {
            super(Material.iron);
            this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
            setCreativeTab(CreativeTabs.tabBlock);
            setUnlocalizedName(MODID + ":" + name);
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
            OBJModel.OBJState retState = new OBJModel.OBJState(transform);
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
     * This block uses the same model as OBJDirectionEye, but instead of facing the
     * player when placed, this one ALWAYS faces the player. I know, creepy right?
     * @author shadekiller666
     *
     */
    public static class OBJDynamicEye extends Block
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
        public boolean isOpaqueCube() { return false; }
        
        @Override
        public boolean isFullCube() { return false; }
        
        @Override
        public boolean hasTileEntity(IBlockState state) { return true; }
        
        @Override
        public TileEntity createTileEntity(World worldIn, IBlockState state)
        {
        	return new OBJDynamicEyeTileEntity();
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
            this.state = new OBJModel.OBJState(null, TRSRTransformation.identity());
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
                this.state = new OBJModel.OBJState(null, transform);
                this.worldObj.markBlockRangeForRenderUpdate(this.pos, this.pos);
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
            OBJModel.OBJState newState = new OBJModel.OBJState(null, transform);
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
}
