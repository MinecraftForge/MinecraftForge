/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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
 */

package net.minecraftforge.fluids;

import java.util.Map;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.common.property.Properties;
import net.minecraftforge.common.property.PropertyFloat;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * This is a base implementation for Fluid blocks.
 *
 * It is highly recommended that you extend this class or one of the Forge-provided child classes.
 *
 */
public abstract class BlockFluidBase extends Block implements IFluidBlock
{
    protected final static Map<Block, Boolean> defaultDisplacements = Maps.newHashMap();

    static
    {
        defaultDisplacements.put(Blocks.OAK_DOOR,                       false);
        defaultDisplacements.put(Blocks.SPRUCE_DOOR,                    false);
        defaultDisplacements.put(Blocks.BIRCH_DOOR,                     false);
        defaultDisplacements.put(Blocks.JUNGLE_DOOR,                    false);
        defaultDisplacements.put(Blocks.ACACIA_DOOR,                    false);
        defaultDisplacements.put(Blocks.DARK_OAK_DOOR,                  false);
        defaultDisplacements.put(Blocks.TRAPDOOR,                       false);
        defaultDisplacements.put(Blocks.IRON_TRAPDOOR,                  false);
        defaultDisplacements.put(Blocks.OAK_FENCE,                      false);
        defaultDisplacements.put(Blocks.SPRUCE_FENCE,                   false);
        defaultDisplacements.put(Blocks.BIRCH_FENCE,                    false);
        defaultDisplacements.put(Blocks.JUNGLE_FENCE,                   false);
        defaultDisplacements.put(Blocks.DARK_OAK_FENCE,                 false);
        defaultDisplacements.put(Blocks.ACACIA_FENCE,                   false);
        defaultDisplacements.put(Blocks.NETHER_BRICK_FENCE,             false);
        defaultDisplacements.put(Blocks.OAK_FENCE_GATE,                 false);
        defaultDisplacements.put(Blocks.SPRUCE_FENCE_GATE,              false);
        defaultDisplacements.put(Blocks.BIRCH_FENCE_GATE,               false);
        defaultDisplacements.put(Blocks.JUNGLE_FENCE_GATE,              false);
        defaultDisplacements.put(Blocks.DARK_OAK_FENCE_GATE,            false);
        defaultDisplacements.put(Blocks.ACACIA_FENCE_GATE,              false);
        defaultDisplacements.put(Blocks.WOODEN_PRESSURE_PLATE,          false);
        defaultDisplacements.put(Blocks.STONE_PRESSURE_PLATE,           false);
        defaultDisplacements.put(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE,  false);
        defaultDisplacements.put(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE,  false);
        defaultDisplacements.put(Blocks.LADDER,                         false);
        defaultDisplacements.put(Blocks.IRON_BARS,                      false);
        defaultDisplacements.put(Blocks.GLASS_PANE,                     false);
        defaultDisplacements.put(Blocks.STAINED_GLASS_PANE,             false);
        defaultDisplacements.put(Blocks.PORTAL,                         false);
        defaultDisplacements.put(Blocks.END_PORTAL,                     false);
        defaultDisplacements.put(Blocks.COBBLESTONE_WALL,               false);
        defaultDisplacements.put(Blocks.BARRIER,                        false);
        defaultDisplacements.put(Blocks.STANDING_BANNER,                false);
        defaultDisplacements.put(Blocks.WALL_BANNER,                    false);
        defaultDisplacements.put(Blocks.CAKE,                           false);

        defaultDisplacements.put(Blocks.IRON_DOOR,     false);
        defaultDisplacements.put(Blocks.STANDING_SIGN, false);
        defaultDisplacements.put(Blocks.WALL_SIGN,     false);
        defaultDisplacements.put(Blocks.REEDS,         false);
    }
    protected Map<Block, Boolean> displacements = Maps.newHashMap();

    private static final class UnlistedPropertyBool extends Properties.PropertyAdapter<Boolean>
    {
        public UnlistedPropertyBool(String name)
        {
            super(PropertyBool.create(name));
        }
    }

    public static final PropertyInteger LEVEL = PropertyInteger.create("level", 0, 15);
    public static final PropertyFloat[] LEVEL_CORNERS = new PropertyFloat[4];
    public static final PropertyFloat FLOW_DIRECTION = new PropertyFloat("flow_direction", -1000f, 1000f);
    public static final UnlistedPropertyBool[] SIDE_OVERLAYS = new UnlistedPropertyBool[4];
    public static final ImmutableList<IUnlistedProperty<?>> FLUID_RENDER_PROPS;

    static
    {
        ImmutableList.Builder<IUnlistedProperty<?>> builder = ImmutableList.builder();
        builder.add(FLOW_DIRECTION);
        for(int i = 0; i < 4; i++)
        {
            LEVEL_CORNERS[i] = new PropertyFloat("level_corner_" + i, 0f, 1f);
            builder.add(LEVEL_CORNERS[i]);

            SIDE_OVERLAYS[i] = new UnlistedPropertyBool("side_overlay_" + i);
            builder.add(SIDE_OVERLAYS[i]);
        }
        FLUID_RENDER_PROPS = builder.build();
    }

    protected int quantaPerBlock = 8;
    protected float quantaPerBlockFloat = 8F;
    protected float quantaFraction = 8f / 9f;
    protected int density = 1;
    protected int densityDir = -1;
    protected int temperature = 295;

    protected int tickRate = 20;
    protected BlockRenderLayer renderLayer = BlockRenderLayer.TRANSLUCENT;
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
        this.setDefaultState(blockState.getBaseState().withProperty(LEVEL, getMaxRenderHeightMeta()));
    }

    @Override
    @Nonnull
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer.Builder(this)
                .add(LEVEL)
                .add(FLUID_RENDER_PROPS.toArray(new IUnlistedProperty<?>[0]))
                .build();
    }

    @Override
    public int getMetaFromState(@Nonnull IBlockState state)
    {
        return state.getValue(LEVEL);
    }
    @Override
    @Deprecated
    @Nonnull
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(LEVEL, meta);
    }

    public BlockFluidBase setQuantaPerBlock(int quantaPerBlock)
    {
        if (quantaPerBlock > 16 || quantaPerBlock < 1) quantaPerBlock = 8;
        this.quantaPerBlock = quantaPerBlock;
        this.quantaPerBlockFloat = quantaPerBlock;
        this.quantaFraction = quantaPerBlock / (quantaPerBlock + 1f);
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

    public BlockFluidBase setRenderLayer(BlockRenderLayer renderLayer)
    {
        this.renderLayer = renderLayer;
        return this;
    }

    public BlockFluidBase setMaxScaledLight(int maxScaledLight)
    {
        this.maxScaledLight = maxScaledLight;
        return this;
    }

    public final int getDensity()
    {
        return density;
    }

    public final int getTemperature()
    {
        return temperature;
    }

    /**
     * Returns true if the block at (pos) is displaceable. Does not displace the block.
     */
    public boolean canDisplace(IBlockAccess world, BlockPos pos)
    {
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        if (block.isAir(state, world, pos))
        {
            return true;
        }

        if (block == this)
        {
            return false;
        }

        if (displacements.containsKey(block))
        {
            return displacements.get(block);
        }

        Material material = state.getMaterial();
        if (material.blocksMovement() || material == Material.PORTAL || material == Material.STRUCTURE_VOID)
        {
            return false;
        }

        int density = getDensity(world, pos);
        if (density == Integer.MAX_VALUE)
        {
            return true;
        }

        return this.density > density;
    }

    /**
     * Attempt to displace the block at (pos), return true if it was displaced.
     */
    public boolean displaceIfPossible(World world, BlockPos pos)
    {
        boolean canDisplace = canDisplace(world, pos);
        if (canDisplace)
        {
            IBlockState state = world.getBlockState(pos);
            Block block = state.getBlock();

            if (!block.isAir(state, world, pos) && !isFluid(state))
            {
                // Forge: Vanilla has a 'bug' where snowballs don't drop like every other block. So special case because ewww...
                if (block != Blocks.SNOW_LAYER) block.dropBlockAsItem(world, pos, state, 0);
            }
        }
        return canDisplace;
    }

    public abstract int getQuantaValue(IBlockAccess world, BlockPos pos);

    @Override
    public abstract boolean canCollideCheck(@Nonnull IBlockState state, boolean fullHit);

    public abstract int getMaxRenderHeightMeta();

    /* BLOCK FUNCTIONS */
    @Override
    public void onBlockAdded(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state)
    {
        world.scheduleUpdate(pos, this, tickRate);
    }

    @Override
    public void neighborChanged(@Nonnull IBlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Block neighborBlock, @Nonnull BlockPos neighbourPos)
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
    public boolean isPassable(@Nonnull IBlockAccess world, @Nonnull BlockPos pos)
    {
        return true;
    }

    @Override
    @Nonnull
    public Item getItemDropped(@Nonnull IBlockState state, @Nonnull Random rand, int fortune)
    {
        return Items.AIR;
    }

    @Override
    public int quantityDropped(@Nonnull Random par1Random)
    {
        return 0;
    }

    @Override
    public int tickRate(@Nonnull World world)
    {
        return tickRate;
    }

    @Override
    @Nonnull
    public Vec3d modifyAcceleration(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull Entity entity, @Nonnull Vec3d vec)
    {
        return densityDir > 0 ? vec : vec.add(getFlowVector(world, pos));
    }

    @Override
    public int getLightValue(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos)
    {
        if (maxScaledLight == 0)
        {
            return super.getLightValue(state, world, pos);
        }
        return (int) (getQuantaPercentage(world, pos) * maxScaledLight);
    }

    @Override
    public boolean isOpaqueCube(@Nonnull IBlockState state)
    {
        return false;
    }

    @Override
    public boolean isFullCube(@Nonnull IBlockState state)
    {
        return false;
    }

    @Override
    public int getPackedLightmapCoords(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos)
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
    @Nonnull
    public BlockRenderLayer getBlockLayer()
    {
        return this.renderLayer;
    }

    @Override
    @Nonnull
    public BlockFaceShape getBlockFaceShape(@Nonnull IBlockAccess worldIn, @Nonnull IBlockState state, @Nonnull BlockPos pos, @Nonnull EnumFacing face)
    {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    public boolean shouldSideBeRendered(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull EnumFacing side)
    {
        IBlockState neighbor = world.getBlockState(pos.offset(side));
        if (neighbor.getMaterial() == state.getMaterial())
        {
            return false;
        }
        if (side == (densityDir < 0 ? EnumFacing.UP : EnumFacing.DOWN))
        {
            return true;
        }
        return super.shouldSideBeRendered(state, world, pos, side);
    }

    private static boolean isFluid(@Nonnull IBlockState blockstate)
    {
        return blockstate.getMaterial().isLiquid() || blockstate.getBlock() instanceof IFluidBlock;
    }

    @Override
    @Nonnull
    public IBlockState getExtendedState(@Nonnull IBlockState oldState, @Nonnull IBlockAccess world, @Nonnull BlockPos pos)
    {
        IExtendedBlockState state = (IExtendedBlockState)oldState;
        state = state.withProperty(FLOW_DIRECTION, (float)getFlowDirection(world, pos));
        IBlockState[][] upBlockState = new IBlockState[3][3];
        float[][] height = new float[3][3];
        float[][] corner = new float[2][2];
        upBlockState[1][1] = world.getBlockState(pos.down(densityDir));
        height[1][1] = getFluidHeightForRender(world, pos, upBlockState[1][1]);
        if (height[1][1] == 1)
        {
            for (int i = 0; i < 2; i++)
            {
                for (int j = 0; j < 2; j++)
                {
                    corner[i][j] = 1;
                }
            }
        }
        else
        {
            for (int i = 0; i < 3; i++)
            {
                for (int j = 0; j < 3; j++)
                {
                    if (i != 1 || j != 1)
                    {
                        upBlockState[i][j] = world.getBlockState(pos.add(i - 1, 0, j - 1).down(densityDir));
                        height[i][j] = getFluidHeightForRender(world, pos.add(i - 1, 0, j - 1), upBlockState[i][j]);
                    }
                }
            }
            for (int i = 0; i < 2; i++)
            {
                for (int j = 0; j < 2; j++)
                {
                    corner[i][j] = getFluidHeightAverage(height[i][j], height[i][j + 1], height[i + 1][j], height[i + 1][j + 1]);
                }
            }
            //check for downflow above corners
            boolean n =  isFluid(upBlockState[0][1]);
            boolean s =  isFluid(upBlockState[2][1]);
            boolean w =  isFluid(upBlockState[1][0]);
            boolean e =  isFluid(upBlockState[1][2]);
            boolean nw = isFluid(upBlockState[0][0]);
            boolean ne = isFluid(upBlockState[0][2]);
            boolean sw = isFluid(upBlockState[2][0]);
            boolean se = isFluid(upBlockState[2][2]);
            if (nw || n || w)
            {
                corner[0][0] = 1;
            }
            if (ne || n || e)
            {
                corner[0][1] = 1;
            }
            if (sw || s || w)
            {
                corner[1][0] = 1;
            }
            if (se || s || e)
            {
                corner[1][1] = 1;
            }
        }

        for (int i = 0; i < 4; i++)
        {
            EnumFacing side = EnumFacing.getHorizontal(i);
            BlockPos offset = pos.offset(side);
            boolean useOverlay = world.getBlockState(offset).getBlockFaceShape(world, offset, side.getOpposite()) == BlockFaceShape.SOLID;
            state = state.withProperty(SIDE_OVERLAYS[i], useOverlay);
        }

        state = state.withProperty(LEVEL_CORNERS[0], corner[0][0]);
        state = state.withProperty(LEVEL_CORNERS[1], corner[0][1]);
        state = state.withProperty(LEVEL_CORNERS[2], corner[1][1]);
        state = state.withProperty(LEVEL_CORNERS[3], corner[1][0]);
        return state;
    }

    /* FLUID FUNCTIONS */
    public static int getDensity(IBlockAccess world, BlockPos pos)
    {
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        if (block instanceof BlockFluidBase)
        {
            return ((BlockFluidBase)block).getDensity();
        }

        Fluid fluid = getFluid(state);
        if (fluid != null)
        {
            return fluid.getDensity();
        }
        return Integer.MAX_VALUE;
    }

    public static int getTemperature(IBlockAccess world, BlockPos pos)
    {
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        if (block instanceof BlockFluidBase)
        {
            return ((BlockFluidBase)block).getTemperature();
        }

        Fluid fluid = getFluid(state);
        if (fluid != null)
        {
            return fluid.getTemperature();
        }
        return Integer.MAX_VALUE;
    }

    @Nullable
    private static Fluid getFluid(IBlockState state)
    {
        Block block = state.getBlock();

        if (block instanceof IFluidBlock)
        {
            return ((IFluidBlock)block).getFluid();
        }
        if (block instanceof BlockLiquid)
        {
            if (state.getMaterial() == Material.WATER)
            {
                return FluidRegistry.WATER;
            }
            if (state.getMaterial() == Material.LAVA)
            {
                return FluidRegistry.LAVA;
            }
        }
        return null;
    }

    public static double getFlowDirection(IBlockAccess world, BlockPos pos)
    {
        IBlockState state = world.getBlockState(pos);
        if (!state.getMaterial().isLiquid())
        {
            return -1000.0;
        }
        Vec3d vec = ((BlockFluidBase)state.getBlock()).getFlowVector(world, pos);
        return vec.x == 0.0D && vec.z == 0.0D ? -1000.0D : MathHelper.atan2(vec.z, vec.x) - Math.PI / 2D;
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

        for (int i = 0; i < flow.length; i++)
        {
            if (flow[i] >= quantaFraction)
            {
                total += flow[i] * 10;
                count += 10;
            }

            if (flow[i] >= 0)
            {
                total += flow[i];
                count++;
            }
        }

        return total / count;
    }

    public float getFluidHeightForRender(IBlockAccess world, BlockPos pos, @Nonnull IBlockState up)
    {
        IBlockState here = world.getBlockState(pos);
        if (here.getBlock() == this)
        {
            if (isFluid(up))
            {
                return 1;
            }

            if (getMetaFromState(here) == getMaxRenderHeightMeta())
            {
                return quantaFraction;
            }
        }
        if (here.getBlock() instanceof BlockLiquid)
        {
            return Math.min(1 - BlockLiquid.getLiquidHeightPercent(here.getValue(BlockLiquid.LEVEL)), quantaFraction);
        }
        return !here.getMaterial().isSolid() && up.getBlock() == this ? 1 : this.getQuantaPercentage(world, pos) * quantaFraction;
    }

    public Vec3d getFlowVector(IBlockAccess world, BlockPos pos)
    {
        Vec3d vec = new Vec3d(0.0D, 0.0D, 0.0D);
        int decay = getFlowDecay(world, pos);

        for (EnumFacing side : EnumFacing.Plane.HORIZONTAL)
        {
            BlockPos offset = pos.offset(side);
            int otherDecay = getFlowDecay(world, offset);
            if (otherDecay >= quantaPerBlock)
            {
                if (!world.getBlockState(offset).getMaterial().blocksMovement())
                {
                    otherDecay = getFlowDecay(world, offset.up(densityDir));
                    if (otherDecay < quantaPerBlock)
                    {
                        int power = otherDecay - (decay - quantaPerBlock);
                        vec = vec.addVector(side.getFrontOffsetX() * power, 0, side.getFrontOffsetZ() * power);
                    }
                }
            }
            else
            {
                int power = otherDecay - decay;
                vec = vec.addVector(side.getFrontOffsetX() * power, 0, side.getFrontOffsetZ() * power);
            }
        }

        if (hasVerticalFlow(world, pos))
        {
            for (EnumFacing side : EnumFacing.Plane.HORIZONTAL)
            {
                BlockPos offset = pos.offset(side);
                if (causesDownwardCurrent(world, offset, side) || causesDownwardCurrent(world, offset.down(densityDir), side))
                {
                    vec = vec.normalize().addVector(0.0, 6.0 * densityDir, 0.0);
                    break;
                }
            }
        }

        return vec.normalize();
    }

    private int getFlowDecay(IBlockAccess world, BlockPos pos)
    {
        int quantaValue = getQuantaValue(world, pos);
        return quantaValue > 0 && hasVerticalFlow(world, pos) ? 0 : quantaPerBlock - quantaValue;
    }

    private boolean hasVerticalFlow(IBlockAccess world, BlockPos pos)
    {
        return world.getBlockState(pos.down(densityDir)).getBlock() == this;
    }

    protected boolean causesDownwardCurrent(IBlockAccess world, BlockPos pos, EnumFacing face)
    {
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        if (block == this) return false;
        if (face == (densityDir < 0 ? EnumFacing.UP : EnumFacing.DOWN)) return true;
        if (state.getMaterial() == Material.ICE) return false;

        boolean flag = isExceptBlockForAttachWithPiston(block) || block instanceof BlockStairs;
        return !flag && state.getBlockFaceShape(world, pos, face) == BlockFaceShape.SOLID;
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
        return getFilledPercentage((IBlockAccess) world, pos);
    }

    public float getFilledPercentage(IBlockAccess world, BlockPos pos)
    {
        int quantaRemaining = getQuantaValue(world, pos);
        float remaining = (quantaRemaining + 1f) / (quantaPerBlockFloat + 1f);
        return remaining * (density > 0 ? 1 : -1);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(@Nonnull IBlockState blockState, @Nonnull IBlockAccess worldIn, @Nonnull BlockPos pos)
    {
        return NULL_AABB;
    }
    
    @Override
    @SideOnly (Side.CLIENT)
    public Vec3d getFogColor(World world, BlockPos pos, IBlockState state, Entity entity, Vec3d originalColor, float partialTicks)
    {
        if (!isWithinFluid(world, pos, ActiveRenderInfo.projectViewFromEntity(entity, partialTicks)))
        {
            BlockPos otherPos = pos.down(densityDir);
            IBlockState otherState = world.getBlockState(otherPos);
            return otherState.getBlock().getFogColor(world, otherPos, otherState, entity, originalColor, partialTicks);
        }

        if (getFluid() != null)
        {
            int color = getFluid().getColor();
            float red = (color >> 16 & 0xFF) / 255.0F;
            float green = (color >> 8 & 0xFF) / 255.0F;
            float blue = (color & 0xFF) / 255.0F;
            return new Vec3d(red, green, blue);
        }

        return super.getFogColor(world, pos, state, entity, originalColor, partialTicks);
    }

    @Override
    public IBlockState getStateAtViewpoint(IBlockState state, IBlockAccess world, BlockPos pos, Vec3d viewpoint)
    {
        if (!isWithinFluid(world, pos, viewpoint))
        {
            return world.getBlockState(pos.down(densityDir));
        }

        return super.getStateAtViewpoint(state, world, pos, viewpoint);
    }

    private boolean isWithinFluid(IBlockAccess world, BlockPos pos, Vec3d vec)
    {
        float filled = getFilledPercentage(world, pos);
        return filled < 0 ? vec.y > pos.getY() + filled + 1
                          : vec.y < pos.getY() + filled;
    }
}
