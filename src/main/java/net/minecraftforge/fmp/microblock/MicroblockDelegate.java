package net.minecraftforge.fmp.microblock;

import com.google.common.base.Optional;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fmp.multipart.IMultipart;
import net.minecraftforge.fmp.multipart.Multipart;

/**
 * Represents an object to which microblock events are delegated. This allows for custom interaction and data storage
 * inside the microblock.
 */
public class MicroblockDelegate
{
    
    protected final IMicroblock delegated;

    public MicroblockDelegate(IMicroblock delegated)
    {
        this.delegated = delegated;
    }

    /**
     * Harvests this microblock, removing it from the container it's in.
     */
    public boolean harvest(EntityPlayer player, RayTraceResult hit)
    {
        return false;
    }

    /**
     * Gets the strength of a player against this microblock. Not to be confused with
     * {@link Multipart#getHardness(RayTraceResult)}. This also takes potion effects and tools into account.
     */
    public Optional<Float> getStrength(EntityPlayer player, RayTraceResult hit)
    {
        return Optional.absent();
    }

    /**
     * Called when a part in the same block block changes.
     */
    public void onPartChanged(IMultipart part)
    {
    }

    /**
     * Called when a neighbor block changes.
     */
    public void onNeighborBlockChange(Block block)
    {
    }

    /**
     * Called when a neighbor tile changes.
     */
    public void onNeighborTileChange(EnumFacing facing)
    {
    }

    /**
     * Called when this microblock is added to the container.
     */
    public void onAdded()
    {
    }

    /**
     * Called right before this microblock is removed from the container.
     */
    public void onRemoved()
    {
    }

    /**
     * Called when the chunk this microblock is in is loaded.
     */
    public void onLoaded()
    {
    }

    /**
     * Called when the chunk this microblock is in is unloaded.
     */
    public void onUnloaded()
    {
    }

    /**
     * Called when a player right-clicks this microblock. Return true to play the right-click animation.
     */
    public EnumActionResult onActivated(EntityPlayer player, EnumHand hand, ItemStack heldItem, RayTraceResult hit)
    {
        return EnumActionResult.PASS;
    }

    /**
     * Called when a player left-clicks this microblock.
     */
    public boolean onClicked(EntityPlayer player, RayTraceResult hit)
    {
        return false;
    }

    /**
     * Writes this microblock's NBT data to a tag so it can be saved.
     */
    public NBTTagCompound writeToNBT(NBTTagCompound tag)
    {
        return tag;
    }

    /**
     * Loads this microblock's data from the saved NBT tag.
     */
    public void readFromNBT(NBTTagCompound tag)
    {
    }

    /**
     * Writes this microblock's data to a packet buffer for it to be sent to the client. This is called when the client
     * gets close enough to the container that it gets synced and also and when
     * {@link MicroblockDelegate#sendUpdatePacket()} is called.
     */
    public void writeUpdatePacket(PacketBuffer buf)
    {
    }

    /**
     * Reads this microblock's data from a packet sent form the server.
     */
    public void readUpdatePacket(PacketBuffer buf)
    {
    }

    /**
     * Writes this microblock's data to a packet and sends it to the client.
     */
    public final void sendUpdatePacket()
    {
        delegated.sendUpdatePacket();
    }

    /**
     * Whether or not redstone can connect to the specified side of this microblock.
     */
    public Optional<Boolean> canConnectRedstone(EnumFacing side)
    {
        return Optional.absent();
    }

    /**
     * Gets the weak redstone signal output by this microblock on the specified side.
     */
    public Optional<Integer> getWeakSignal(EnumFacing side)
    {
        return Optional.absent();
    }

    /**
     * Gets the strong redstone signal output by this microblock on the specified side.
     */
    public Optional<Integer> getStrongSignal(EnumFacing side)
    {
        return Optional.absent();
    }
}
