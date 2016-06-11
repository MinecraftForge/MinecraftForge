package net.minecraftforge.fmp.multipart;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.IWorldLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fmp.capabilities.CapabilityWrapperRegistry;
import net.minecraftforge.fmp.capabilities.ISlottedCapabilityProvider;
import net.minecraftforge.fmp.event.PartEvent;
import net.minecraftforge.fmp.multipart.ISolidPart.ISolidTopPart;
import net.minecraftforge.fmp.network.MessageMultipartChange;
import net.minecraftforge.fmp.network.MessageMultipartChange.Type;

/**
 * Helper class that contains all the logic required for an {@link IMultipartContainer} to work, as well as methods that
 * are forwarded to each of the parts.<br/>
 * You can implement {@link IMultipartContainer} in your {@link TileEntity} and forward the calls to an instance of this
 * class for default multipart container logic.
 *
 * @see IMultipart
 * @see IMultipartContainer
 */
public class MultipartContainer implements IMultipartContainer
{
    
    private IWorldLocation location;
    private IMultipartContainerListener listener;

    private boolean canTurnIntoBlock;
    private BiMap<UUID, IMultipart> partMap;
    private Map<PartSlot, ISlottedPart> slotMap;
    private Set<IMultipart> tickingParts;

    public MultipartContainer(IWorldLocation location, boolean canTurnIntoBlock)
    {
        this.location = location;
        this.canTurnIntoBlock = canTurnIntoBlock;
        this.partMap = ImmutableBiMap.of();
        this.slotMap = ImmutableMap.of();
        this.tickingParts = ImmutableSet.of();
    }

    public MultipartContainer(IWorldLocation location, boolean canTurnIntoBlock, MultipartContainer container)
    {
        this.location = location;
        this.canTurnIntoBlock = canTurnIntoBlock;
        this.partMap = container.partMap;
        this.slotMap = container.slotMap;
        this.tickingParts = container.tickingParts;
        for (IMultipart part : partMap.values())
        {
            part.setContainer(this);
        }
    }

    public void setListener(IMultipartContainerListener listener)
    {
        this.listener = listener;
    }

    @Override
    public World getWorldIn()
    {
        return location.getWorldIn();
    }

    @Override
    public BlockPos getPosIn()
    {
        return location.getPosIn();
    }

    public boolean canTurnIntoBlock()
    {
        return canTurnIntoBlock;
    }

    @Override
    public Collection<? extends IMultipart> getParts()
    {
        return partMap.values();
    }
    
    public Collection<? extends IMultipart> getTickingParts()
    {
        return tickingParts;
    }

    @Override
    public ISlottedPart getPartInSlot(PartSlot slot)
    {
        return slotMap.get(slot);
    }

    @Override
    public boolean canAddPart(IMultipart part)
    {
        if (part == null || getParts().contains(part))
        {
            return false;
        }
        if (part instanceof ISlottedPart)
        {
            for (PartSlot s : ((ISlottedPart) part).getSlotMask())
            {
                if (getPartInSlot(s) != null)
                {
                    return false;
                }
            }
        }
        if (occlusionTest(part))
        {
            List<AxisAlignedBB> list = new ArrayList<AxisAlignedBB>();
            part.addCollisionBoxes(new AxisAlignedBB(0, 0, 0, 1, 1, 1), list, null);
            if (getWorldIn() != null && getPosIn() != null)
            {
                for (AxisAlignedBB bb : list)
                {
                    if (!getWorldIn().checkNoEntityCollision(bb.offset(getPosIn().getX(), getPosIn().getY(), getPosIn().getZ())))
                    {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean canReplacePart(IMultipart oldPart, IMultipart newPart)
    {
        if (oldPart == null)
        {
            return canAddPart(newPart);
        }
        if (newPart == null || getParts().contains(newPart))
        {
            return false;
        }

        if (newPart instanceof ISlottedPart)
        {
            for (PartSlot s : ((ISlottedPart) newPart).getSlotMask())
            {
                IMultipart p = getPartInSlot(s);
                if (p != null && p != oldPart)
                {
                    return false;
                }
            }
        }

        if (!occlusionTest(newPart, oldPart))
        {
            return false;
        }

        List<AxisAlignedBB> list = new ArrayList<AxisAlignedBB>();
        newPart.addCollisionBoxes(new AxisAlignedBB(0, 0, 0, 1, 1, 1), list, null);
        if (getWorldIn() != null && getPosIn() != null)
        {
            for (AxisAlignedBB bb : list)
            {
                if (!getWorldIn().checkNoEntityCollision(bb.offset(getPosIn().getX(), getPosIn().getY(), getPosIn().getZ())))
                    return false;
            }
        }

        return true;
    }

    @Override
    public void addPart(IMultipart part)
    {
        if (getWorldIn().isRemote)
        {
            throw new IllegalStateException("Attempted to add a part on the client!");
        }
        addPart(part, true, true, true, true, UUID.randomUUID());
    }

    public void addPart(IMultipart part, boolean notifyPart, boolean notifyNeighbors, boolean tryConvert, boolean postEvent, UUID id)
    {
        if (part == null)
        {
            throw new NullPointerException("Attempted to add a null part at " + getPosIn());
        }
        if (getParts().contains(part))
        {
            throw new IllegalArgumentException("Attempted to add a duplicate part at " + getPosIn() + " (" + part + ")");
        }

        if (listener != null)
        {
            listener.onAddPartPre(part);
        }

        part.setContainer(this);

        Map<UUID, IMultipart> partMap = new LinkedHashMap<UUID, IMultipart>(this.partMap);
        Map<PartSlot, ISlottedPart> slotMap = new HashMap<PartSlot, ISlottedPart>(this.slotMap);
        Set<IMultipart> tickingParts = part instanceof ITickable ? new HashSet<IMultipart>(this.tickingParts) : null;

        partMap.put(id, part);
        if (tickingParts != null)
        {
            tickingParts.add(part);
        }
        if (part instanceof ISlottedPart)
        {
            for (PartSlot s : ((ISlottedPart) part).getSlotMask())
            {
                slotMap.put(s, (ISlottedPart) part);
            }
        }

        this.partMap = ImmutableBiMap.copyOf(partMap);
        this.slotMap = ImmutableMap.copyOf(slotMap);
        if (tickingParts != null)
        {
            this.tickingParts = ImmutableSet.copyOf(tickingParts);
        }

        if (postEvent)
        {
            MinecraftForge.EVENT_BUS.post(new PartEvent.Add(part));
        }

        if (notifyPart)
        {
            part.onAdded();
        }
        if (notifyNeighbors)
        {
            notifyPartChanged(part);
            getWorldIn().checkLight(getPosIn());
        }

        if (listener != null)
        {
            listener.onAddPartPost(part);
        }

        if (getWorldIn() != null && !getWorldIn().isRemote && (!canTurnIntoBlock || !tryConvert || !MultipartRegistry.convertToBlock(this)))
        {
            MessageMultipartChange.newPacket(getWorldIn(), getPosIn(), part, Type.ADD).send(getWorldIn());
        }
    }

    @Override
    public void removePart(IMultipart part)
    {
        removePart(part, true, true, true);
    }

    public void removePart(IMultipart part, boolean notifyPart, boolean notifyNeighbors, boolean postEvent)
    {
        if (part == null)
        {
            throw new NullPointerException("Attempted to remove a null part from " + getPosIn());
        }
        if (!getParts().contains(part))
        {
            throw new IllegalArgumentException("Attempted to remove a part that doesn't exist from " + getPosIn() + " (" + part + ")");
        }

        Map<IMultipart, UUID> partMap = new LinkedHashMap<IMultipart, UUID>(this.partMap.inverse());
        BiMap<UUID, IMultipart> oldPartMap = this.partMap;
        Map<PartSlot, ISlottedPart> slotMap = new HashMap<PartSlot, ISlottedPart>(this.slotMap), oldSlotMap = this.slotMap;
        Set<IMultipart> tickingParts = part instanceof ITickable ? new HashSet<IMultipart>(this.tickingParts) : null;

        if (listener != null)
        {
            listener.onRemovePartPre(part);
        }

        partMap.remove(part);
        if (tickingParts != null)
        {
            tickingParts.remove(part);
        }
        if (part instanceof ISlottedPart)
        {
            Iterator<Entry<PartSlot, ISlottedPart>> it = slotMap.entrySet().iterator();
            while (it.hasNext())
            {
                if (it.next().getValue() == part)
                {
                    it.remove();
                }
            }
        }

        BiMap<UUID, IMultipart> iPartMap = this.partMap = ImmutableBiMap.copyOf(partMap).inverse();
        this.slotMap = slotMap;
        if (tickingParts != null)
        {
            this.tickingParts = tickingParts;
        }

        if (postEvent)
        {
            MinecraftForge.EVENT_BUS.post(new PartEvent.Remove(part));
        }

        // Yes, it's a bit of a dirty solution, but it's the best I could come up with. The part must not be there in
        // the if statement :P
        if (getWorldIn() != null && !getWorldIn().isRemote && (!canTurnIntoBlock || !MultipartRegistry.convertToBlock(this)))
        {
            this.partMap = oldPartMap;
            this.slotMap = oldSlotMap;
            MessageMultipartChange.newPacket(getWorldIn(), getPosIn(), part, Type.REMOVE).send(getWorldIn());
            this.partMap = iPartMap;
            this.slotMap = slotMap;
        }

        if (notifyPart)
        {
            part.onRemoved();
        }
        if (notifyNeighbors)
        {
            notifyPartChanged(part);
            getWorldIn().checkLight(getPosIn());
        }

        part.setContainer(null);

        if (listener != null)
        {
            listener.onRemovePartPost(part);
        }
    }

    @Override
    public UUID getPartID(IMultipart part)
    {
        return partMap.inverse().get(part);
    }

    @Override
    public IMultipart getPartFromID(UUID id)
    {
        return partMap.get(id);
    }

    @Override
    public void addPart(UUID id, IMultipart part)
    {
        addPart(part, true, true, true, true, id);
    }

    @Override
    public boolean occlusionTest(IMultipart part, IMultipart... ignored)
    {
        List<IMultipart> ignoredList = Arrays.asList(ignored);

        for (IMultipart p : getParts())
        {
            if (!ignoredList.contains(p) && (!p.occlusionTest(part) || !part.occlusionTest(p)))
            {
                return false;
            }
        }

        return true;
    }

    public void notifyPartChanged(IMultipart part)
    {
        for (IMultipart p : getParts())
        {
            if (p != part)
            {
                p.onPartChanged(part);
            }
        }
        getWorldIn().notifyNeighborsOfStateChange(getPosIn(), getWorldIn().getBlockState(getPosIn()).getBlock());
    }

    public RayTraceResult collisionRayTrace(Vec3d start, Vec3d end)
    {
        double dist = Double.POSITIVE_INFINITY;
        RayTraceResult current = null;

        for (IMultipart p : getParts())
        {
            RayTraceResult result = p.collisionRayTrace(start, end);
            if (result != null)
            {
                double d = result.hitVec.squareDistanceTo(start);
                if (d <= dist)
                {
                    dist = d;
                    current = result;
                }
            }
        }

        return current;
    }

    public void addCollisionBoxes(AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, Entity collidingEntity)
    {
        for (IMultipart p : getParts())
        {
            p.addCollisionBoxes(entityBox, collidingBoxes, collidingEntity);
        }
    }

    public int getLightValue()
    {
        int max = 0;
        for (IMultipart part : getParts())
        {
            max = Math.max(max, part.getLightValue());
        }
        return max;
    }

    public ItemStack getPickBlock(EntityPlayer player, RayTraceResult hit)
    {
        return hit.partHit.getPickPart(player, hit);
    }

    public List<ItemStack> getDrops()
    {
        List<ItemStack> list = new ArrayList<ItemStack>();
        for (IMultipart part : getParts())
        {
            list.addAll(part.getDrops());
        }
        return list;
    }

    public boolean harvest(EntityPlayer player, RayTraceResult hit)
    {
        if (getWorldIn().isRemote)
        {
            return false;
        }
        if (hit == null)
        {
            for (IMultipart part : getParts())
            {
                part.harvest(null, hit);
            }
            return true;
        }
        if (partMap.values().contains(hit.partHit))
        {
            if (getWorldIn().isRemote)
            {
                return getParts().size() - 1 == 0;
            }
            hit.partHit.harvest(player, hit);
            return getParts().isEmpty();
        }
        return false;
    }

    public float getHardness(EntityPlayer player, RayTraceResult hit)
    {
        if (!partMap.values().contains(hit.partHit))
        {
            return -1;
        }
        return hit.partHit.getStrength(player, hit);
    }

    public void onNeighborBlockChange(Block block)
    {
        for (IMultipart part : getParts())
        {
            part.onNeighborBlockChange(block);
        }
    }

    public void onNeighborTileChange(EnumFacing facing)
    {
        for (IMultipart part : getParts())
        {
            part.onNeighborTileChange(facing);
        }
    }

    public boolean onActivated(EntityPlayer playerIn, EnumHand hand, ItemStack heldItem, RayTraceResult hit)
    {
        if (hit == null || !partMap.values().contains(hit.partHit))
        {
            return false;
        }
        return hit.partHit.onActivated(playerIn, hand, heldItem, hit);
    }

    public void onClicked(EntityPlayer playerIn, RayTraceResult hit)
    {
        if (hit == null || !partMap.values().contains(hit.partHit))
        {
            return;
        }
        hit.partHit.onClicked(playerIn, hit);
    }

    public boolean canConnectRedstone(EnumFacing side)
    {
        return MultipartRedstoneHelper.canConnectRedstone(this, side);
    }

    public int getWeakSignal(EnumFacing side)
    {
        return MultipartRedstoneHelper.getWeakSignal(this, side);
    }

    public int getStrongSignal(EnumFacing side)
    {
        return MultipartRedstoneHelper.getStrongSignal(this, side);
    }

    public boolean isSideSolid(EnumFacing side)
    {
        IMultipart slotPart = getPartInSlot(PartSlot.getFaceSlot(side));
        if (slotPart != null && slotPart instanceof ISolidPart)
        {
            return ((ISolidPart) slotPart).isSideSolid(side);
        }
        for (IMultipart p : getParts())
        {
            if ((!(p instanceof ISlottedPart) || ((ISlottedPart) p).getSlotMask().isEmpty()) && p instanceof ISolidPart &&
                    ((ISolidPart) p).isSideSolid(side))
            {
                return true;
            }
        }
        return false;
    }

    public boolean canPlaceTorchOnTop()
    {
        IMultipart slotPart = getPartInSlot(PartSlot.getFaceSlot(EnumFacing.UP));
        if (slotPart != null && slotPart instanceof ISolidTopPart)
        {
            return ((ISolidTopPart) slotPart).canPlaceTorchOnTop();
        }
        for (IMultipart p : getParts())
        {
            if (p instanceof ISolidTopPart && ((ISolidTopPart) p).canPlaceTorchOnTop())
            {
                return true;
            }
        }
        return false;
    }

    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(Random rand)
    {
        for (IMultipart p : getParts())
        {
            p.randomDisplayTick(rand);
        }
    }

    public NBTTagCompound writeToNBT(NBTTagCompound tag)
    {
        NBTTagList partList = new NBTTagList();
        for (Entry<UUID, IMultipart> entry : partMap.entrySet())
        {
            NBTTagCompound t = new NBTTagCompound();
            t.setString("__partID", entry.getKey().toString());
            t.setString("__partType", entry.getValue().getType().toString());
            t = entry.getValue().writeToNBT(t);
            partList.appendTag(t);
        }
        tag.setTag("partList", partList);
        return tag;
    }

    public void readFromNBT(NBTTagCompound tag)
    {
        partMap = ImmutableBiMap.of();
        slotMap = new HashMap<PartSlot, ISlottedPart>();

        NBTTagList partList = tag.getTagList("partList", new NBTTagCompound().getId());
        for (int i = 0; i < partList.tagCount(); i++)
        {
            NBTTagCompound t = partList.getCompoundTagAt(i);
            UUID id = UUID.fromString(t.getString("__partID"));
            IMultipart part = MultipartRegistry.createPart(new ResourceLocation(t.getString("__partType")), t);
            if (part != null)
            {
                addPart(part, false, false, false, false, id);
            }
        }
    }

    public NBTTagCompound writeDescription(NBTTagCompound tag)
    {
        NBTTagList partList = new NBTTagList();
        for (Entry<UUID, IMultipart> entry : partMap.entrySet())
        {
            NBTTagCompound t = new NBTTagCompound();
            t.setString("__partID", entry.getKey().toString());
            t.setString("__partType", entry.getValue().getType().toString());
            ByteBuf buf = Unpooled.buffer();
            entry.getValue().writeUpdatePacket(new PacketBuffer(buf));
            t.setByteArray("data", buf.array());
            partList.appendTag(t);
        }
        tag.setTag("partList", partList);
        return tag;
    }

    public void readDescription(NBTTagCompound tag)
    {
        NBTTagList partList = tag.getTagList("partList", new NBTTagCompound().getId());
        for (int i = 0; i < partList.tagCount(); i++)
        {
            NBTTagCompound t = partList.getCompoundTagAt(i);
            UUID id = UUID.fromString(t.getString("__partID"));
            IMultipart part = partMap.get(id);
            if (part == null)
            {
                part = MultipartRegistry.createPart(new ResourceLocation(t.getString("__partType")),
                        new PacketBuffer(Unpooled.copiedBuffer(t.getByteArray("data"))));
                addPart(part, false, false, false, false, id);
            }
            else
            {
                part.readUpdatePacket(new PacketBuffer(Unpooled.copiedBuffer(t.getByteArray("data"))));
            }
        }
    }

    public List<PartState> getExtendedStates(IBlockAccess world, BlockPos pos)
    {
        List<PartState> states = new ArrayList<PartState>();
        for (IMultipart part : getParts())
        {
            PartState state = PartState.fromPart(part);
            if (state != null)
            {
                states.add(state);
            }
        }
        return states;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, PartSlot slot, EnumFacing facing)
    {
        if (slot == null)
        {
            for (IMultipart p : getParts())
            {
                if ((!(p instanceof ISlottedPart) || ((ISlottedPart) p).getSlotMask().isEmpty()) && p instanceof ICapabilityProvider &&
                        ((ICapabilityProvider) p).hasCapability(capability, facing))
                {
                    return true;
                }
            }
            return false;
        }

        IMultipart part = getPartInSlot(slot);
        return part instanceof ISlottedCapabilityProvider ? ((ISlottedCapabilityProvider) part).hasCapability(capability, slot, facing)
                : part instanceof ICapabilityProvider ? ((ICapabilityProvider) part).hasCapability(capability, facing) : false;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, PartSlot slot, EnumFacing facing)
    {
        if (slot == null)
        {
            List<T> implementations = new ArrayList<T>();
            for (IMultipart p : getParts())
            {
                if (!(p instanceof ISlottedPart) || ((ISlottedPart) p).getSlotMask().isEmpty())
                {
                    if (p instanceof ICapabilityProvider)
                    {
                        T impl = ((ICapabilityProvider) p).getCapability(capability, facing);
                        if (impl != null)
                        {
                            implementations.add(impl);
                        }
                    }
                }
            }

            if (implementations.isEmpty())
            {
                return null;
            }
            else if (implementations.size() == 1)
            {
                return implementations.get(0);
            }
            else
            {
                return CapabilityWrapperRegistry.wrap(capability, implementations);
            }
        }

        IMultipart part = getPartInSlot(slot);
        return part instanceof ISlottedCapabilityProvider ? ((ISlottedCapabilityProvider) part).getCapability(capability, slot, facing)
                : part instanceof ICapabilityProvider ? ((ICapabilityProvider) part).getCapability(capability, facing) : null;
    }

    public Boolean isAABBInsideMaterial(AxisAlignedBB aabb, Material material)
    {
        Boolean def = null;
        for (IMultipart part : getParts())
        {
            Boolean is = part.isAABBInsideMaterial(aabb, material);
            if (is != null)
            {
                if (is == true)
                {
                    return true;
                }
                else
                {
                    def = false;
                }
            }
        }
        return def;
    }

    public Boolean isEntityInsideMaterial(Entity entity, double yToTest, Material material, boolean testingHead)
    {
        Boolean def = null;
        for (IMultipart part : getParts())
        {
            Boolean is = part.isEntityInsideMaterial(entity, yToTest, material, testingHead);
            if (is != null)
            {
                if (is == true)
                {
                    return true;
                }
                else
                {
                    def = false;
                }
            }
        }
        return def;
    }

    public void onEntityStanding(Entity entity)
    {
        for (IMultipart part : getParts())
        {
            part.onEntityStanding(entity);
        }
    }

    public void onEntityCollided(Entity entity)
    {
        for (IMultipart part : getParts())
        {
            part.onEntityCollided(entity);
        }
    }

    /**
     * Interface used to listen to {@link IMultipartContainer} part addition and removal events.
     */
    public static interface IMultipartContainerListener
    {
        
        /**
         * Called before a part is added to the container.
         */
        public void onAddPartPre(IMultipart part);

        /**
         * Called after a part is added to the container.
         */
        public void onAddPartPost(IMultipart part);

        /**
         * Called before a part is removed from the container.
         */
        public void onRemovePartPre(IMultipart part);

        /**
         * Called after a part is removed from the container.
         */
        public void onRemovePartPost(IMultipart part);

    }


}
