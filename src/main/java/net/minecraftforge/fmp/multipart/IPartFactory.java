package net.minecraftforge.fmp.multipart;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

/**
 * Interface used to instantiate multiparts.
 *
 * @see IMultipart
 * @see MultipartRegistry
 * @see IAdvancedPartFactory
 */
public interface IPartFactory
{
    
    /**
     * Creates a part from a part type.
     */
    public IMultipart createPart(ResourceLocation type, boolean client);

    /**
     * Interface used to instantiate parts depending on the part type and the data stored in NBT or received in an
     * update packet.
     *
     * @see IMultipart
     * @see MultipartRegistry
     * @see IPartFactory
     */
    public interface IAdvancedPartFactory
    {
        
        /**
         * Creates a part from a type and an update packet. Used when loading parts in the client.
         */
        public IMultipart createPart(ResourceLocation type, PacketBuffer buf);

        /**
         * Creates a part from a type and an NBT tag. Used when loading parts from NBT.
         */
        public IMultipart createPart(ResourceLocation type, NBTTagCompound tag);

    }

}
