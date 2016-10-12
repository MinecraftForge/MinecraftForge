package net.minecraftforge.fmp.multipart;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityDispatcher;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fmp.ForgeMultipartModContainer;
import net.minecraftforge.fmp.client.multipart.AdvancedParticleManager;
import net.minecraftforge.fmp.client.multipart.MultipartStateMapper;
import net.minecraftforge.fmp.multipart.IPartFactory.IAdvancedPartFactory;
import net.minecraftforge.fmp.network.MessageMultipartChange;

/**
 * A default abstract implementation of {@link IMultipart}.<br/>
 * Includes {@link AxisAlignedBB} raytracing, part hardness, materials and tool effectivity, as well as helper methods
 * to notify various kinds of updates to the world.<br/>
 * {@link IMultipart#getType()} is implemented by default and it returns the type with which this class was registered
 * in {@link MultipartRegistry}, though custom types can be used if your part is created by a custom
 * {@link IPartFactory} or {@link IAdvancedPartFactory}.
 *
 * @see IMultipart
 */
public abstract class Multipart implements IMultipart
{

    protected static final AxisAlignedBB DEFAULT_RENDER_BOUNDS = new AxisAlignedBB(0, 0, 0, 1, 1, 1);

    private final ResourceLocation partType = MultipartRegistry.getPartType(this);
    private IMultipartContainer container;

    private final CapabilityDispatcher capabilities;

    public Multipart()
    {
        AttachCapabilitiesEvent.Multipart event = new AttachCapabilitiesEvent.Multipart(this);
        MinecraftForge.EVENT_BUS.post(event);
        capabilities = event.getCapabilities().size() > 0 ? new CapabilityDispatcher(event.getCapabilities(), null) : null;
    }

    @Override
    public World getWorld()
    {
        return getContainer() != null ? getContainer().getWorldIn() : null;
    }

    @Override
    public BlockPos getPos()
    {
        return getContainer() != null ? getContainer().getPosIn() : null;
    }

    @Override
    public IMultipartContainer getContainer()
    {
        return container;
    }

    @Override
    public void setContainer(IMultipartContainer container)
    {
        this.container = container;
    }

    @Override
    public ResourceLocation getType()
    {
        return partType;
    }

    @Override
    public ResourceLocation getModelPath()
    {
        return getType();
    }

    @Override
    public ModelResourceLocation getModelPath(IBlockState state)
    {
        return new ModelResourceLocation(getModelPath(), MultipartStateMapper.instance.getPropertyString(state.getProperties()));
    }

    @Override
    public RayTraceResult collisionRayTrace(Vec3d start, Vec3d end)
    {
        Vec3d posVec = new Vec3d(getPos());
        start = start.subtract(posVec);
        end = end.subtract(posVec);
        List<AxisAlignedBB> list = new ArrayList<AxisAlignedBB>();
        addSelectionBoxes(list);
        double dist = Double.POSITIVE_INFINITY;
        RayTraceResult current = null;
        for (AxisAlignedBB aabb : list)
        {
            RayTraceResult result = aabb.calculateIntercept(start, end);
            if (result != null)
            {
                double d = result.hitVec.squareDistanceTo(start);
                if (d <= dist)
                {
                    dist = d;
                    current = result;
                    current.boxHit = aabb;
                }
            }
        }
        if (current != null)
        {
            RayTraceResult result = new RayTraceResult(RayTraceResult.Type.BLOCK, current.hitVec.add(posVec), current.sideHit, getPos());
            result.partHit = this;
            result.boxHit = current.boxHit;
            return result;
        }
        return null;
    }

    /**
     * Adds the selection boxes used to ray trace this part.
     */
    public void addSelectionBoxes(List<AxisAlignedBB> list)
    {
    }

    @Override
    public void addCollisionBoxes(AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity)
    {
    }

    @Override
    public boolean occlusionTest(IMultipart part)
    {
        return OcclusionHelper.defaultOcclusionTest(this, part);
    }

    @Override
    public int getLightValue()
    {
        return 0;
    }

    @Override
    public ItemStack getPickPart(EntityPlayer player, RayTraceResult hit)
    {
        return null;
    }

    @Override
    public List<ItemStack> getDrops()
    {
        return Arrays.asList();
    }

    @Override
    public void harvest(EntityPlayer player, RayTraceResult hit)
    {
        World world = getWorld();
        BlockPos pos = getPos();
        double x = pos.getX() + 0.5, y = pos.getY() + 0.5, z = pos.getZ() + 0.5;

        if ((player == null || !player.capabilities.isCreativeMode) && !world.isRemote && world.getGameRules().getBoolean("doTileDrops")
                && !world.restoringBlockSnapshots)
        {
            for (ItemStack stack : getDrops())
            {
                EntityItem item = new EntityItem(world, x, y, z, stack);
                item.setDefaultPickupDelay();
                world.spawnEntityInWorld(item);
            }
        }
        getContainer().removePart(this);
    }

    /**
     * Gets the hardness of this part. Similar to {@link Block#getBlockHardness(World, BlockPos)}, not to be confused
     * with {@link IMultipart#getStrength(EntityPlayer, RayTraceResult)}.
     */
    public float getHardness(RayTraceResult hit)
    {
        return 0;
    }

    /**
     * Gets the material this part is made of. Used for harvest speed checks.
     */
    public Material getMaterial()
    {
        return null;
    }

    /**
     * Checks if the specified tool is strong enough to harvest this part at full speed.
     */
    public boolean isToolEffective(String type, int level)
    {
        return true;
    }

    @Override
    public float getStrength(EntityPlayer player, RayTraceResult hit)
    {
        float hardness = getHardness(hit);
        if (hardness < 0.0F)
        {
            return 0.0F;
        }
        else if (hardness == 0.0F)
        {
            return 1.0F;
        }

        Material mat = getMaterial();
        ItemStack stack = player.getHeldItemMainhand();
        boolean effective = mat == null || mat.isToolNotRequired();
        if (!effective && stack != null)
        {
            for (String tool : stack.getItem().getToolClasses(stack))
            {
                if (effective = isToolEffective(tool, stack.getItem().getHarvestLevel(stack, tool)))
                {
                    break;
                }
            }
        }

        float breakSpeed = player.getDigSpeed(getExtendedState(MultipartRegistry.getDefaultState(this).getBaseState()), getPos());

        if (!effective)
        {
            return breakSpeed / hardness / 100F;
        }
        else
        {
            return breakSpeed / hardness / 30F;
        }
    }

    @Override
    public void onPartChanged(IMultipart part)
    {
    }

    @Override
    public void onNeighborBlockChange(Block block)
    {
    }

    @Override
    public void onNeighborTileChange(EnumFacing facing)
    {
    }

    @Override
    public void onAdded()
    {
    }

    @Override
    public void onRemoved()
    {
    }

    @Override
    public void onLoaded()
    {
    }

    @Override
    public void onUnloaded()
    {
    }

    @Override
    public void onConverted(TileEntity tile)
    {
    }

    public boolean canRotatePartAround(EnumFacing axis)
    {
        return false;
    }

    public void rotatePartAround(EnumFacing axis)
    {
    }

    public boolean canPlayerRotate(EnumFacing axis, EntityPlayer player, RayTraceResult hit)
    {
        return canRotatePartAround(axis);
    }

    public void rotatePart(EnumFacing axis, EntityPlayer player, RayTraceResult hit)
    {
        rotatePartAround(axis);
    }

    public boolean recolorPart(EnumFacing side, EnumDyeColor color, EntityPlayer player, RayTraceResult hit)
    {
        return false;
    }

    @Override
    public boolean onActivated(EntityPlayer player, EnumHand hand, ItemStack heldItem, RayTraceResult hit)
    {
        return false;
    }

    @Override
    public void onClicked(EntityPlayer player, RayTraceResult hit)
    {
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag)
    {
        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
    }

    @Override
    public void writeUpdatePacket(PacketBuffer buf)
    {
    }

    @Override
    public void readUpdatePacket(PacketBuffer buf)
    {
    }

    @Override
    public void sendUpdatePacket()
    {
        sendUpdatePacket(getModelPath() != null);
    }

    /**
     * Similar to {@link IMultipart#sendUpdatePacket()}, but also allows the user to specify if the chunk should be
     * re-rendered on arrival.
     */
    public void sendUpdatePacket(boolean reRender)
    {
        if (getWorld() instanceof WorldServer)
        {
            MessageMultipartChange.newPacket(getWorld(), getPos(), this,
                    reRender ? MessageMultipartChange.Type.UPDATE_RERENDER : MessageMultipartChange.Type.UPDATE).send(getWorld());
        }
    }

    @Override
    public boolean canRenderInLayer(BlockRenderLayer layer)
    {
        return layer == BlockRenderLayer.SOLID;
    }

    @Override
    public BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(ForgeMultipartModContainer.multipart);
    }

    @Override
    public IBlockState getActualState(IBlockState state)
    {
        return state;
    }

    @Override
    public IBlockState getExtendedState(IBlockState state)
    {
        return state;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public AxisAlignedBB getRenderBoundingBox()
    {
        return DEFAULT_RENDER_BOUNDS;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(Random rand)
    {
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean addDestroyEffects(AdvancedParticleManager particleManager)
    {
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean addHitEffects(RayTraceResult hit, AdvancedParticleManager particleManager)
    {
        return false;
    }

    @Override
    public Boolean isAABBInsideMaterial(AxisAlignedBB aabb, Material material)
    {
        return null;
    }

    @Override
    public Boolean isEntityInsideMaterial(Entity entity, double yToTest, Material material, boolean testingHead)
    {
        return null;
    }

    @Override
    public void onEntityCollided(Entity entity)
    {
    }

    @Override
    public void onEntityStanding(Entity entity)
    {
    }

    @Override
    public boolean drawHighlight(RayTraceResult hit, EntityPlayer player, float partialTicks)
    {
        return false;
    }

    @Override
    public boolean hasFastRenderer()
    {
        return false;
    }
    
    @Override
    public IBlockColor getTint()
    {
        return null;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing)
    {
        return capabilities != null ? capabilities.hasCapability(capability, facing) : false;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing)
    {
        return capabilities != null ? capabilities.getCapability(capability, facing) : null;
    }

    protected void markRenderUpdate()
    {
        World world = getWorld();
        if (world != null)
        {
            BlockPos pos = getPos();
            world.markBlockRangeForRenderUpdate(pos, pos);
        }
    }

    protected void markDirty()
    {
        World world = getWorld();
        if (world != null)
        {
            BlockPos pos = getPos();
            world.markChunkDirty(pos, null);
            world.updateComparatorOutputLevel(pos, world.getBlockState(pos).getBlock());
        }
    }

    protected void markLightingUpdate()
    {
        World world = getWorld();
        if (world != null)
        {
            world.checkLight(getPos());
        }
    }

    protected void notifyBlockUpdate()
    {
        World world = getWorld();
        BlockPos pos = getPos();
        if (world != null)
        {
            world.notifyNeighborsOfStateChange(pos, world.getBlockState(pos).getBlock());
        }
    }

    protected void notifyPartUpdate()
    {
        IMultipartContainer container = getContainer();
        if (container != null)
        {
            for (IMultipart part : container.getParts())
            {
                part.onPartChanged(this);
            }
        }
    }

}
