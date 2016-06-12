package net.minecraftforge.fmp.microblock;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import com.google.common.base.Optional;

import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.common.property.Properties;
import net.minecraftforge.common.property.Properties.PropertyAdapter;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fmp.ForgeMultipartModContainer;
import net.minecraftforge.fmp.microblock.IMicroMaterial.IDelegatedMicroMaterial;
import net.minecraftforge.fmp.multipart.IMultipart;
import net.minecraftforge.fmp.multipart.IRedstonePart;
import net.minecraftforge.fmp.multipart.IRedstonePart.ISlottedRedstonePart;
import net.minecraftforge.fmp.multipart.Multipart;
import net.minecraftforge.fmp.multipart.PartSlot;

/**
 * A default abstract implementation of {@link IMicroblock}.<br/>
 * Includes everything required for basic microblock functionality, including {@link MicroblockDelegate} support.<br/>
 *
 * @see IMicroblock
 */
public abstract class Microblock extends Multipart implements IMicroblock, IRedstonePart, ISlottedRedstonePart
{

    public static final IUnlistedProperty<IMicroMaterial> PROPERTY_MATERIAL = new PropertyMicroMaterial("material");
    public static final IUnlistedProperty<Integer> PROPERTY_SIZE = Properties.toUnlisted(PropertyInteger.create("size", 0, 7));
    public static final IUnlistedProperty<IBlockState> PROPERTY_MATERIAL_STATE = new PropertyBlockState("material_state");
    public static final IUnlistedProperty<PartSlot> PROPERTY_SLOT = new PropertyPartSlot("slot");

    protected IMicroMaterial material;
    protected PartSlot slot;
    protected int size;
    protected MicroblockDelegate delegate;

    public Microblock(IMicroMaterial material, PartSlot slot, int size, boolean isRemote)
    {
        this.material = material;
        this.slot = slot;
        this.size = size;
        this.delegate = material instanceof IDelegatedMicroMaterial ? ((IDelegatedMicroMaterial) material).provideDelegate(this, isRemote)
                : null;
    }

    @Override
    public abstract MicroblockClass getMicroClass();

    @Override
    public IMicroMaterial getMicroMaterial()
    {
        return material;
    }

    @Override
    public EnumSet<PartSlot> getSlotMask()
    {
        return slot == null ? EnumSet.noneOf(PartSlot.class) : EnumSet.of(slot);
    }

    @Override
    public PartSlot getSlot()
    {
        return slot;
    }

    @Override
    public void setSlot(PartSlot slot)
    {
        this.slot = slot;
    }

    @Override
    public int getSize()
    {
        return size;
    }

    @Override
    public void setSize(int size)
    {
        this.size = size;
    }

    @Override
    public ResourceLocation getType()
    {
        return getMicroClass().getType();
    }

    @Override
    public ModelResourceLocation getModelPath(IBlockState state)
    {
        return new ModelResourceLocation(getModelPath(), "multipart");
    }

    @Override
    public int getLightValue()
    {
        return getMicroMaterial().getLightValue();
    }

    @Override
    public float getHardness(RayTraceResult hit)
    {
        return getMicroMaterial().getHardness();
    }

    @Override
    public ItemStack getPickPart(EntityPlayer player, RayTraceResult hit)
    {
        int size = getSize();
        int picked = 1;

        for (int i = 2; i >= 0; i--)
        {
            if (size - (1 << i) >= 0)
            {
                size -= (picked = (1 << i));
            }
        }

        return getMicroClass().createStack(getMicroMaterial(), picked, 1);
    }

    @Override
    public List<ItemStack> getDrops()
    {
        MicroblockClass microclass = getMicroClass();
        IMicroMaterial material = getMicroMaterial();
        int size = getSize();
        List<ItemStack> drops = new ArrayList<ItemStack>();

        for (int i = 2; i >= 0; i--)
        {
            if (size - (1 << i) >= 0)
            {
                size -= 1 << i;
                drops.add(microclass.createStack(material, 1 << i, 1));
            }
        }

        return drops;
    }

    @Override
    public boolean occlusionTest(IMultipart part)
    {
        return part instanceof IMicroblock || super.occlusionTest(part);
    }

    @Override
    public IExtendedBlockState getExtendedState(IBlockState state)
    {
        return ((IExtendedBlockState) state).withProperty(PROPERTY_MATERIAL, getMicroMaterial())
                .withProperty(PROPERTY_MATERIAL_STATE, getMicroMaterial().getMaterialState(getWorld(), getPos(), this))
                .withProperty(PROPERTY_SIZE, getSize()).withProperty(PROPERTY_SLOT, slot);
    }

    @Override
    public BlockStateContainer createBlockState()
    {
        return new BlockStateContainer.Builder(ForgeMultipartModContainer.multipart).add(PROPERTY_MATERIAL, PROPERTY_MATERIAL_STATE,
                PROPERTY_SIZE, PROPERTY_SLOT).build();
    }

    @Override
    public void writeUpdatePacket(PacketBuffer buf)
    {
        super.writeUpdatePacket(buf);

        ByteBufUtils.writeUTF8String(buf, getMicroMaterial().getType().toString());
        buf.writeInt(slot != null ? slot.ordinal() : -1);
        buf.writeInt(getSize());
        if (delegate != null)
        {
            delegate.writeUpdatePacket(buf);
        }
    }

    @Override
    public void readUpdatePacket(PacketBuffer buf)
    {
        super.readUpdatePacket(buf);

        IMicroMaterial oldMat = material;
        material = MicroblockRegistry.getMaterial(new ResourceLocation(ByteBufUtils.readUTF8String(buf)));
        int iSlot = buf.readInt();
        slot = iSlot == -1 ? null : PartSlot.VALUES[iSlot];
        size = buf.readInt();
        if (oldMat != material)
        {
            delegate = material instanceof IDelegatedMicroMaterial ? ((IDelegatedMicroMaterial) material).provideDelegate(this, true)
                    : null;
        }
        if (delegate != null)
        {
            delegate.readUpdatePacket(buf);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag)
    {
        tag = super.writeToNBT(tag);

        tag.setString("material", getMicroMaterial().getType().toString());
        tag.setInteger("slot", slot != null ? slot.ordinal() : -1);
        tag.setInteger("size", getSize());
        if (delegate != null)
        {
            tag = delegate.writeToNBT(tag);
        }
        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        super.readFromNBT(tag);

        material = MicroblockRegistry.getMaterial(new ResourceLocation(tag.getString("material")));
        int iSlot = tag.getInteger("slot");
        slot = iSlot == -1 ? null : PartSlot.VALUES[iSlot];
        size = tag.getInteger("size");
        delegate = material instanceof IDelegatedMicroMaterial ? ((IDelegatedMicroMaterial) material).provideDelegate(this, false) : null;
        if (delegate != null)
        {
            delegate.readFromNBT(tag);
        }
    }

    // Delegation

    @Override
    public void harvest(EntityPlayer player, RayTraceResult hit)
    {
        if (delegate == null || !delegate.harvest(player, hit))
        {
            super.harvest(player, hit);
        }
    }

    @Override
    public float getStrength(EntityPlayer player, RayTraceResult hit)
    {
        if (delegate != null)
        {
            Optional<Float> strength = delegate.getStrength(player, hit);
            if (strength.isPresent())
                return strength.get();
        }
        return super.getStrength(player, hit);
    }

    @Override
    public void onPartChanged(IMultipart part)
    {
        super.onPartChanged(part);
        if (delegate != null)
        {
            delegate.onPartChanged(part);
        }
    }

    @Override
    public void onNeighborBlockChange(Block block)
    {
        super.onNeighborBlockChange(block);
        if (delegate != null)
        {
            delegate.onNeighborBlockChange(block);
        }
    }

    @Override
    public void onNeighborTileChange(EnumFacing facing)
    {
        super.onNeighborTileChange(facing);
        if (delegate != null)
        {
            delegate.onNeighborTileChange(facing);
        }
    }

    @Override
    public void onAdded()
    {
        super.onAdded();
        if (delegate != null)
        {
            delegate.onAdded();
        }
    }

    @Override
    public void onRemoved()
    {
        super.onRemoved();
        if (delegate != null)
        {
            delegate.onRemoved();
        }
    }

    @Override
    public void onLoaded()
    {
        super.onLoaded();
        if (delegate != null)
        {
            delegate.onLoaded();
        }
    }

    @Override
    public void onUnloaded()
    {
        super.onUnloaded();
        if (delegate != null)
        {
            delegate.onUnloaded();
        }
    }

    @Override
    public boolean onActivated(EntityPlayer player, EnumHand hand, ItemStack heldItem, RayTraceResult hit)
    {
        if (delegate != null)
        {
            EnumActionResult activated = delegate.onActivated(player, hand, heldItem, hit);
            if (activated != EnumActionResult.PASS)
            {
                return activated == EnumActionResult.SUCCESS;
            }
        }
        return super.onActivated(player, hand, heldItem, hit);
    }

    @Override
    public void onClicked(EntityPlayer player, RayTraceResult hit)
    {
        if (delegate != null && delegate.onClicked(player, hit))
        {
            return;
        }
        super.onClicked(player, hit);
    }

    @Override
    public boolean canConnectRedstone(EnumFacing side)
    {
        if (delegate != null)
        {
            Optional<Boolean> can = delegate.canConnectRedstone(side);
            if (can.isPresent())
            {
                return can.get();
            }
        }
        return false;
    }

    @Override
    public int getWeakSignal(EnumFacing side)
    {
        if (delegate != null)
        {
            Optional<Integer> signal = delegate.getWeakSignal(side);
            if (signal.isPresent())
            {
                return signal.get();
            }
        }
        return 0;
    }

    @Override
    public int getStrongSignal(EnumFacing side)
    {
        if (delegate != null)
        {
            Optional<Integer> signal = delegate.getStrongSignal(side);
            if (signal.isPresent())
            {
                return signal.get();
            }
        }
        return 0;
    }

    @Override
    public boolean canRenderInLayer(BlockRenderLayer layer)
    {
        return material.canRenderInLayer(layer);
    }

    public static class PropertyAABB implements IUnlistedProperty<AxisAlignedBB>
    {
        
        private final String name;

        public PropertyAABB(String name)
        {
            this.name = name;
        }

        @Override
        public String getName()
        {
            return name;
        }

        @Override
        public boolean isValid(AxisAlignedBB value)
        {
            return value != null;
        }

        @Override
        public Class<AxisAlignedBB> getType()
        {
            return AxisAlignedBB.class;
        }

        @Override
        public String valueToString(AxisAlignedBB value)
        {
            return value.toString();
        }

    }

    public static class PropertyEnumSet<T extends Enum<T>> implements IUnlistedProperty<EnumSet<T>>
    {
        
        private final String name;

        public PropertyEnumSet(String name)
        {
            this.name = name;
        }

        @Override
        public String getName()
        {
            return name;
        }

        @Override
        public boolean isValid(EnumSet<T> value)
        {
            return value != null;
        }

        @SuppressWarnings("unchecked")
        @Override
        public Class<EnumSet<T>> getType()
        {
            return (Class<EnumSet<T>>) (Class<?>) EnumSet.class;
        }

        @Override
        public String valueToString(EnumSet<T> value)
        {
            return value.toString();
        }

    }

    public static class PropertyMicroMaterial implements IUnlistedProperty<IMicroMaterial>
    {
        
        private final String name;

        public PropertyMicroMaterial(String name)
        {
            this.name = name;
        }

        @Override
        public String getName()
        {
            return name;
        }

        @Override
        public boolean isValid(IMicroMaterial value)
        {
            return value != null;
        }

        @Override
        public Class<IMicroMaterial> getType()
        {
            return IMicroMaterial.class;
        }

        @Override
        public String valueToString(IMicroMaterial value)
        {
            return value.getType().toString();
        }

    }

    public static class PropertyBlockState implements IUnlistedProperty<IBlockState>
    {
        
        private final String name;

        public PropertyBlockState(String name)
        {
            this.name = name;
        }

        @Override
        public String getName()
        {
            return name;
        }

        @Override
        public boolean isValid(IBlockState value)
        {
            return value != null;
        }

        @Override
        public Class<IBlockState> getType()
        {
            return IBlockState.class;
        }

        @Override
        public String valueToString(IBlockState value)
        {
            return value.toString();
        }

    }

    public static class PropertyPartSlot implements IUnlistedProperty<PartSlot> {

        private final String name;

        public PropertyPartSlot(String name)
        {
            this.name = name;
        }

        @Override
        public String getName()
        {
            return name;
        }

        @Override
        public boolean isValid(PartSlot value)
        {
            return true;
        }

        @Override
        public Class<PartSlot> getType()
        {
            return PartSlot.class;
        }

        @Override
        public String valueToString(PartSlot value)
        {
            return value.getLocalizedName();
        }

    }

}
