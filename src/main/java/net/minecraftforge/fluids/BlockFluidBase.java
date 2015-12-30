package net.minecraftforge.fluids;

import java.util.Map;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.common.property.PropertyFloat;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

/**
 * This is a base implementation for Fluid blocks.
 *
 * It is highly recommended that you extend this class or one of the Forge-provided child classes.
 *
 * @author King Lemming, OvermindDL1
 *
 */
@SuppressWarnings("unchecked")
public abstract class BlockFluidBase extends Block implements IFluidBlock
{
    protected final static Map<Block, Boolean> defaultDisplacements = Maps.newHashMap();

    static
    {
        defaultDisplacements.put(Blocks.oak_door,                       false);
        defaultDisplacements.put(Blocks.spruce_door,                    false);
        defaultDisplacements.put(Blocks.birch_door,                     false);
        defaultDisplacements.put(Blocks.jungle_door,                    false);
        defaultDisplacements.put(Blocks.acacia_door,                    false);
        defaultDisplacements.put(Blocks.dark_oak_door,                  false);
        defaultDisplacements.put(Blocks.trapdoor,                       false);
        defaultDisplacements.put(Blocks.iron_trapdoor,                  false);
        defaultDisplacements.put(Blocks.oak_fence,                      false);
        defaultDisplacements.put(Blocks.spruce_fence,                   false);
        defaultDisplacements.put(Blocks.birch_fence,                    false);
        defaultDisplacements.put(Blocks.jungle_fence,                   false);
        defaultDisplacements.put(Blocks.dark_oak_fence,                 false);
        defaultDisplacements.put(Blocks.acacia_fence,                   false);
        defaultDisplacements.put(Blocks.nether_brick_fence,             false);
        defaultDisplacements.put(Blocks.oak_fence_gate,                 false);
        defaultDisplacements.put(Blocks.spruce_fence_gate,              false);
        defaultDisplacements.put(Blocks.birch_fence_gate,               false);
        defaultDisplacements.put(Blocks.jungle_fence_gate,              false);
        defaultDisplacements.put(Blocks.dark_oak_fence_gate,            false);
        defaultDisplacements.put(Blocks.acacia_fence_gate,              false);
        defaultDisplacements.put(Blocks.wooden_pressure_plate,          false);
        defaultDisplacements.put(Blocks.stone_pressure_plate,           false);
        defaultDisplacements.put(Blocks.light_weighted_pressure_plate,  false);
        defaultDisplacements.put(Blocks.heavy_weighted_pressure_plate,  false);
        defaultDisplacements.put(Blocks.ladder,                         false);
        defaultDisplacements.put(Blocks.iron_bars,                      false);
        defaultDisplacements.put(Blocks.glass_pane,                     false);
        defaultDisplacements.put(Blocks.stained_glass_pane,             false);
        defaultDisplacements.put(Blocks.portal,                         false);
        defaultDisplacements.put(Blocks.end_portal,                     false);
        defaultDisplacements.put(Blocks.cobblestone_wall,               false);
        defaultDisplacements.put(Blocks.barrier,                        false);
        defaultDisplacements.put(Blocks.standing_banner,                false);
        defaultDisplacements.put(Blocks.wall_banner,                    false);
        defaultDisplacements.put(Blocks.cake,                           false);

        defaultDisplacements.put(Blocks.iron_door,     false);
        defaultDisplacements.put(Blocks.standing_sign, false);
        defaultDisplacements.put(Blocks.wall_sign,     false);
        defaultDisplacements.put(Blocks.reeds,         false);
    }
    protected Map<Block, Boolean> displacements = Maps.newHashMap();

    public static final PropertyInteger LEVEL = PropertyInteger.create("level", 0, 15);
    public static final PropertyFloat[] LEVEL_CORNERS = new PropertyFloat[4];
    public static final PropertyFloat FLOW_DIRECTION = new PropertyFloat("flow_direction");
    public static final IUnlistedProperty<Float>[] FLUID_RENDER_PROPS;

    static
    {
        @SuppressWarnings("rawtypes")
        ImmutableList.Builder<IUnlistedProperty> builder = ImmutableList.builder();
        builder.add(FLOW_DIRECTION);
        for(int i = 0; i < 4; i++)
        {
            LEVEL_CORNERS[i] = new PropertyFloat("level_corner_" + i);
            builder.add(LEVEL_CORNERS[i]);
        }
        FLUID_RENDER_PROPS = builder.build().toArray(new IUnlistedProperty[0]);
    }

    protected int quantaPerBlock = 8;
    protected float quantaPerBlockFloat = 8F;
    protected int density = 1;
    protected int densityDir = -1;
	protected int temperature = 295;

    protected int tickRate = 20;
    protected EnumWorldBlockLayer renderLayer = EnumWorldBlockLayer.TRANSLUCENT;
    protected int maxScaledLight = 0;

    protected final String fluidName;

    /**
     * This is the fluid used in the constructor. Use this reference to configure things
     * like icons for your block. It might not be active in the registry, so do
     * NOT expose it.
     */
    protected final Fluid definedFluid;

    public BlockFluidBase(Fluid fluid, Material material)
    {
        super(material);
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        this.setTickRandomly(true);
        this.disableStats();

        this.fluidName = fluid.getName();
        this.density = fluid.density;
        this.temperature = fluid.temperature;
        this.maxScaledLight = fluid.luminosity;
        this.tickRate = fluid.viscosity / 200;
        this.densityDir = fluid.density > 0 ? -1 : 1;
        fluid.setBlock(this);

        this.definedFluid = fluid;
        displacements.putAll(defaultDisplacements);
        this.setDefaultState(blockState.getBaseState().withProperty(LEVEL, 0));
    }

    @Override
    protected BlockState createBlockState()
    {
        return new ExtendedBlockState(this, new IProperty[] { LEVEL }, FLUID_RENDER_PROPS);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return ((Integer)state.getValue(LEVEL)).intValue();
    }
    public BlockFluidBase setQuantaPerBlock(int quantaPerBlock)
    {
        if (quantaPerBlock > 16 || quantaPerBlock < 1) quantaPerBlock = 8;
        this.quantaPerBlock = quantaPerBlock;
        this.quantaPerBlockFloat = quantaPerBlock;
        return this;
    }

    public BlockFluidBase setDensity(int density)
    {
        if (density == 0) density = 1;
        this.density = density;
        this.densityDir = density > 0 ? -1 : 1;
        return this;
    }

    public BlockFluidBase setTemperature(int temperature)
    {
        this.temperature = temperature;
        return this;
    }

    public BlockFluidBase setTickRate(int tickRate)
    {
        if (tickRate <= 0) tickRate = 20;
        this.tickRate = tickRate;
        return this;
    }

    public BlockFluidBase setRenderLayer(EnumWorldBlockLayer renderLayer)
    {
        this.renderLayer = renderLayer;
        return this;
    }

    public BlockFluidBase setMaxScaledLight(int maxScaledLight)
    {
        this.maxScaledLight = maxScaledLight;
        return this;
    }

    /**
     * Returns true if the block at (pos) is displaceable. Does not displace the block.
     */
    public boolean canDisplace(IBlockAccess world, BlockPos pos)
    {
        if (world.isAirBlock(pos)) return true;

        Block block = world.getBlockState(pos).getBlock();

        if (block == this)
        {
            return false;
        }

        if (displacements.containsKey(block))
        {
            return displacements.get(block);
        }

        Material material = block.getMaterial();
        if (material.blocksMovement() || material == Material.portal)
        {
            return false;
        }

        int density = getDensity(world, pos);
        if (density == Integer.MAX_VALUE)
        {
        	 return true;
        }

        if (this.density > density)
        {
        	return true;
        }
        else
        {
        	return false;
        }
    }

    /**
     * Attempt to displace the block at (pos), return true if it was displaced.
     */
    public boolean displaceIfPossible(World world, BlockPos pos)
    {
        if (world.isAirBlock(pos))
        {
            return true;
        }

        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        if (block == this)
        {
            return false;
        }

        if (displacements.containsKey(block))
        {
            if (displacements.get(block))
            {
                block.dropBlockAsItem(world, pos, state, 0);
                return true;
            }
            return false;
        }

        Material material = block.getMaterial();
        if (material.blocksMovement() || material == Material.portal)
        {
            return false;
        }

        int density = getDensity(world, pos);
        if (density == Integer.MAX_VALUE)
        {
        	 block.dropBlockAsItem(world, pos, state, 0);
        	 return true;
        }

        if (this.density > density)
        {
        	return true;
        }
        else
        {
        	return false;
        }
    }

    public abstract int getQuantaValue(IBlockAccess world, BlockPos pos);

    @Override
    public abstract boolean canCollideCheck(IBlockState state, boolean fullHit);

    public abstract int getMaxRenderHeightMeta();

    /* BLOCK FUNCTIONS */
    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state)
    {
        world.scheduleUpdate(pos, this, tickRate);
    }

    @Override
    public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighborBlock)
    {
        world.scheduleUpdate(pos, this, tickRate);
    }

    // Used to prevent updates on chunk generation
    @Override
    public boolean requiresUpdates()
    {
        return false;
    }

    @Override
    public boolean isPassable(IBlockAccess world, BlockPos pos)
    {
        return true;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(World world, BlockPos pos, IBlockState state)
    {
        return null;
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return null;
    }

    @Override
    public int quantityDropped(Random par1Random)
    {
        return 0;
    }

    @Override
    public int tickRate(World world)
    {
        return tickRate;
    }

    @Override
    public Vec3 modifyAcceleration(World world, BlockPos pos, Entity entity, Vec3 vec)
    {
        if (densityDir > 0) return vec;
        Vec3 vec_flow = this.getFlowVector(world, pos);
        return vec.addVector(
                vec_flow.xCoord * (quantaPerBlock * 4),
                vec_flow.yCoord * (quantaPerBlock * 4),
                vec_flow.zCoord * (quantaPerBlock * 4));
    }

    @Override
    public int getLightValue(IBlockAccess world, BlockPos pos)
    {
        if (maxScaledLight == 0)
        {
            return super.getLightValue(world, pos);
        }
        int data = ((Integer)world.getBlockState(pos).getValue(LEVEL)).intValue();
        return (int) (data / quantaPerBlockFloat * maxScaledLight);
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

    /* Never used...?
    @Override
    public float getBlockBrightness(World world, BlockPos pos)
    {
        float lightThis = world.getLightBrightness(pos);
        float lightUp = world.getLightBrightness(x, y + 1, z);
        return lightThis > lightUp ? lightThis : lightUp;
    }
    */

    @Override
    public int getMixedBrightnessForBlock(IBlockAccess world, BlockPos pos)
    {
        int lightThis     = world.getCombinedLight(pos, 0);
        int lightUp       = world.getCombinedLight(pos.up(), 0);
        int lightThisBase = lightThis & 255;
        int lightUpBase   = lightUp & 255;
        int lightThisExt  = lightThis >> 16 & 255;
        int lightUpExt    = lightUp >> 16 & 255;
        return (lightThisBase > lightUpBase ? lightThisBase : lightUpBase) |
               ((lightThisExt > lightUpExt ? lightThisExt : lightUpExt) << 16);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public EnumWorldBlockLayer getBlockLayer()
    {
        return this.renderLayer;
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess world, BlockPos pos, EnumFacing side)
    {
        Block block = world.getBlockState(pos).getBlock();
        if (block.getMaterial() == this.blockMaterial)
        {
            return false;
        }
        if(densityDir == -1 && side == EnumFacing.UP)
        {
            return true;
        }
        if(densityDir == 1 && side == EnumFacing.DOWN)
        {
            return true;
        }
        return super.shouldSideBeRendered(world, pos, side);
    }

    @Override
    public IBlockState getExtendedState(IBlockState oldState, IBlockAccess worldIn, BlockPos pos)
    {
        IExtendedBlockState state = (IExtendedBlockState)oldState;
        state = state.withProperty(FLOW_DIRECTION, (float)getFlowDirection(worldIn, pos));
        float[][] height = new float[3][3];
        float[][] corner = new float[2][2];
        height[1][1] = getFluidHeightForRender(worldIn, pos);
        if(height[1][1] == 1)
        {
            for(int i = 0; i < 2; i++)
            {
                for(int j = 0; j < 2; j++)
                {
                    corner[i][j] = 1;
                }
            }
        }
        else
        {
            for(int i = 0; i < 3; i++)
            {
                for(int j = 0; j < 3; j++)
                {
                    if(i != 1 || j != 1)
                    {
                        height[i][j] = getFluidHeightForRender(worldIn, pos.add(i - 1, 0, j - 1));
                    }
                }
            }
            for(int i = 0; i < 2; i++)
            {
                for(int j = 0; j < 2; j++)
                {
                    corner[i][j] = getFluidHeightAverage(height[i][j], height[i][j + 1], height[i + 1][j], height[i + 1][j + 1]);
                }
            }
        }
        state = state.withProperty(LEVEL_CORNERS[0], corner[0][0]);
        state = state.withProperty(LEVEL_CORNERS[1], corner[0][1]);
        state = state.withProperty(LEVEL_CORNERS[2], corner[1][1]);
        state = state.withProperty(LEVEL_CORNERS[3], corner[1][0]);
        return state;
    }

    /* FLUID FUNCTIONS */
    public static final int getDensity(IBlockAccess world, BlockPos pos)
    {
        Block block = world.getBlockState(pos).getBlock();
        if (!(block instanceof BlockFluidBase))
        {
            return Integer.MAX_VALUE;
        }
        return ((BlockFluidBase)block).density;
    }

    public static final int getTemperature(IBlockAccess world, BlockPos pos)
    {
        Block block = world.getBlockState(pos).getBlock();
        if (!(block instanceof BlockFluidBase))
        {
            return Integer.MAX_VALUE;
        }
        return ((BlockFluidBase)block).temperature;
    }

    public static double getFlowDirection(IBlockAccess world, BlockPos pos)
    {
        Block block = world.getBlockState(pos).getBlock();
        if (!block.getMaterial().isLiquid())
        {
            return -1000.0;
        }
        Vec3 vec = ((BlockFluidBase) block).getFlowVector(world, pos);
        return vec.xCoord == 0.0D && vec.zCoord == 0.0D ? -1000.0D : Math.atan2(vec.zCoord, vec.xCoord) - Math.PI / 2D;
    }

    public final int getQuantaValueBelow(IBlockAccess world, BlockPos pos, int belowThis)
    {
        int quantaRemaining = getQuantaValue(world, pos);
        if (quantaRemaining >= belowThis)
        {
            return -1;
        }
        return quantaRemaining;
    }

    public final int getQuantaValueAbove(IBlockAccess world, BlockPos pos, int aboveThis)
    {
        int quantaRemaining = getQuantaValue(world, pos);
        if (quantaRemaining <= aboveThis)
        {
            return -1;
        }
        return quantaRemaining;
    }

    public final float getQuantaPercentage(IBlockAccess world, BlockPos pos)
    {
        int quantaRemaining = getQuantaValue(world, pos);
        return quantaRemaining / quantaPerBlockFloat;
    }

    public float getFluidHeightAverage(float... flow)
    {
        float total = 0;
        int count = 0;

        float end = 0;

        for (int i = 0; i < flow.length; i++)
        {
            if (flow[i] >= 0.875F && end != 1F)
            {
                end = flow[i];
            }

            if (flow[i] >= 0)
            {
                total += flow[i];
                count++;
            }
        }

        if (end == 0)
            end = total / count;

        return end;
    }

    public float getFluidHeightForRender(IBlockAccess world, BlockPos pos)
    {
        IBlockState here = world.getBlockState(pos);
        IBlockState up = world.getBlockState(pos.down(densityDir));
        if (here.getBlock() == this)
        {
            if (up.getBlock().getMaterial().isLiquid() || up.getBlock() instanceof IFluidBlock)
            {
                return 1;
            }

            if (getMetaFromState(here) == getMaxRenderHeightMeta())
            {
                return 0.875F;
            }
        }
        return !here.getBlock().getMaterial().isSolid() && up.getBlock() == this ? 1 : this.getQuantaPercentage(world, pos) * 0.875F;
    }

    public Vec3 getFlowVector(IBlockAccess world, BlockPos pos)
    {
        Vec3 vec = new Vec3(0.0D, 0.0D, 0.0D);
        int decay = quantaPerBlock - getQuantaValue(world, pos);

        for (int side = 0; side < 4; ++side)
        {
            int x2 = pos.getX();
            int z2 = pos.getZ();

            switch (side)
            {
                case 0: --x2; break;
                case 1: --z2; break;
                case 2: ++x2; break;
                case 3: ++z2; break;
            }

            BlockPos pos2 = new BlockPos(x2, pos.getY(), z2);
            int otherDecay = quantaPerBlock - getQuantaValue(world, pos2);
            if (otherDecay >= quantaPerBlock)
            {
                if (!world.getBlockState(pos2).getBlock().getMaterial().blocksMovement())
                {
                    otherDecay = quantaPerBlock - getQuantaValue(world, pos2.down());
                    if (otherDecay >= 0)
                    {
                        int power = otherDecay - (decay - quantaPerBlock);
                        vec = vec.addVector((pos2.getX() - pos.getX()) * power, 0, (pos2.getZ() - pos.getZ()) * power);
                    }
                }
            }
            else if (otherDecay >= 0)
            {
                int power = otherDecay - decay;
                vec = vec.addVector((pos2.getX() - pos.getX()) * power, 0, (pos2.getZ() - pos.getZ()) * power);
            }
        }

        if (world.getBlockState(pos.up()).getBlock() == this)
        {
            boolean flag =
                isBlockSolid(world, pos.add( 0,  0, -1), EnumFacing.NORTH) ||
                isBlockSolid(world, pos.add( 0,  0,  1), EnumFacing.SOUTH) ||
                isBlockSolid(world, pos.add(-1,  0,  0), EnumFacing.WEST) ||
                isBlockSolid(world, pos.add( 1,  0,  0), EnumFacing.EAST) ||
                isBlockSolid(world, pos.add( 0,  1, -1), EnumFacing.NORTH) ||
                isBlockSolid(world, pos.add( 0,  1,  1), EnumFacing.SOUTH) ||
                isBlockSolid(world, pos.add(-1,  1,  0), EnumFacing.WEST) ||
                isBlockSolid(world, pos.add( 1,  1,  0), EnumFacing.EAST);

            if (flag)
            {
                vec = vec.normalize().addVector(0.0D, -6.0D, 0.0D);
            }
        }
        vec = vec.normalize();
        return vec;
    }

    /* IFluidBlock */
    @Override
    public Fluid getFluid()
    {
        return FluidRegistry.getFluid(fluidName);
    }

    @Override
    public float getFilledPercentage(World world, BlockPos pos)
    {
        int quantaRemaining = getQuantaValue(world, pos) + 1;
        float remaining = quantaRemaining / quantaPerBlockFloat;
        if (remaining > 1) remaining = 1.0f;
        return remaining * (density > 0 ? 1 : -1);
    }
}