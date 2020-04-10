/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

package net.minecraftforge.fml.common.network;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryManager;

import org.apache.commons.lang3.Validate;

import io.netty.buffer.ByteBuf;

import javax.annotation.Nullable;
import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableList;

/**
 * Utilities for interacting with {@link ByteBuf}.
 * @author cpw
 *
 */
public class ByteBufUtils {
    /**
     * The number of bytes to write the supplied int using the 7 bit varint encoding.
     *
     * @param toCount The number to analyse
     * @return The number of bytes it will take to write it (maximum of 5)
     */
    public static int varIntByteCount(int toCount)
    {
        return (toCount & 0xFFFFFF80) == 0 ? 1 : ((toCount & 0xFFFFC000) == 0 ? 2 : ((toCount & 0xFFE00000) == 0 ? 3 : ((toCount & 0xF0000000) == 0 ? 4 : 5)));
    }
    /**
     * Read a varint from the supplied buffer.
     *
     * @param buf The buffer to read from
     * @param maxSize The maximum length of bytes to read
     * @return The integer
     */
    public static int readVarInt(ByteBuf buf, int maxSize)
    {
        Validate.isTrue(maxSize < 6 && maxSize > 0, "Varint length is between 1 and 5, not %d", maxSize);
        int i = 0;
        int j = 0;
        byte b0;

        do
        {
            b0 = buf.readByte();
            i |= (b0 & 127) << j++ * 7;

            if (j > maxSize)
            {
                throw new RuntimeException("VarInt too big");
            }
        }
        while ((b0 & 128) == 128);

        return i;
    }
    /**
     * An extended length short. Used by custom payload packets to extend size.
     *
     * @param buf
     * @return
     */
    public static int readVarShort(ByteBuf buf)
    {
        int low = buf.readUnsignedShort();
        int high = 0;
        if ((low & 0x8000) != 0)
        {
            low = low & 0x7FFF;
            high = buf.readUnsignedByte();
        }
        return ((high & 0xFF) << 15) | low;
    }

    public static void writeVarShort(ByteBuf buf, int toWrite)
    {
        int low = toWrite & 0x7FFF;
        int high = ( toWrite & 0x7F8000 ) >> 15;
        if (high != 0)
        {
            low = low | 0x8000;
        }
        buf.writeShort(low);
        if (high != 0)
        {
            buf.writeByte(high);
        }
    }
    /**
     * Write an integer to the buffer using variable length encoding. The maxSize constrains
     * how many bytes (and therefore the maximum number) that will be written.
     *
     * @param to The buffer to write to
     * @param toWrite The integer to write
     * @param maxSize The maximum number of bytes to use
     */
    public static void writeVarInt(ByteBuf to, int toWrite, int maxSize)
    {
        Validate.isTrue(varIntByteCount(toWrite) <= maxSize, "Integer is too big for %d bytes", maxSize);
        while ((toWrite & -128) != 0)
        {
            to.writeByte(toWrite & 127 | 128);
            toWrite >>>= 7;
        }

        to.writeByte(toWrite);
    }
    /**
     * Read a UTF8 string from the byte buffer.
     * It is encoded as <varint length>[<UTF8 char bytes>]
     *
     * @param from The buffer to read from
     * @return The string
     */
    public static String readUTF8String(ByteBuf from)
    {
        int len = readVarInt(from,2);
        String str = from.toString(from.readerIndex(), len, StandardCharsets.UTF_8);
        from.readerIndex(from.readerIndex() + len);
        return str;
    }

    /**
     * Write a String with UTF8 byte encoding to the buffer.
     * It is encoded as <varint length>[<UTF8 char bytes>]
     * @param to the buffer to write to
     * @param string The string to write
     */
    public static void writeUTF8String(ByteBuf to, String string)
    {
        byte[] utf8Bytes = string.getBytes(StandardCharsets.UTF_8);
        Validate.isTrue(varIntByteCount(utf8Bytes.length) < 3, "The string is too long for this encoding.");
        writeVarInt(to, utf8Bytes.length, 2);
        to.writeBytes(utf8Bytes);
    }

    /**
     * Write an {@link ItemStack} using minecraft compatible encoding.
     *
     * @param to The buffer to write to
     * @param stack The itemstack to write
     */
    public static void writeItemStack(ByteBuf to, ItemStack stack)
    {
        PacketBuffer pb = new PacketBuffer(to);
        pb.writeItemStack(stack);
    }

    /**
     * Read an {@link ItemStack} from the byte buffer provided. It uses the minecraft encoding.
     *
     * @param from The buffer to read from
     * @return The itemstack read
     */
    public static ItemStack readItemStack(ByteBuf from)
    {
        PacketBuffer pb = new PacketBuffer(from);
        try
        {
            return pb.readItemStack();
        }
        catch (IOException e)
        {
            // Unpossible?
            throw new RuntimeException(e);
        }
    }

    /**
     * Write an {@link NBTTagCompound} to the byte buffer. It uses the minecraft encoding.
     *
     * @param to The buffer to write to
     * @param tag The tag to write
     */
    public static void writeTag(ByteBuf to, NBTTagCompound tag)
    {
        PacketBuffer pb = new PacketBuffer(to);
        pb.writeCompoundTag(tag);
    }

    /**
     * Read an {@link NBTTagCompound} from the byte buffer. It uses the minecraft encoding.
     *
     * @param from The buffer to read from
     * @return The read tag
     */
    @Nullable
    public static NBTTagCompound readTag(ByteBuf from)
    {
        PacketBuffer pb = new PacketBuffer(from);
        try
        {
            return pb.readCompoundTag();
        } catch (IOException e)
        {
            // Unpossible?
            throw new RuntimeException(e);
        }
    }

    /**
     * Write a registry entry to the stream. The serialized format is not specified and must not be relied upon.
     * Do not use this to write to a file, it is used for client-server communication only.
     * @param out the buffer to write to
     * @param entry the registry entry
     */
    public static <T extends IForgeRegistryEntry<T>> void writeRegistryEntry(@Nonnull ByteBuf out, @Nonnull T entry)
    {
        ForgeRegistry<T> registry = (ForgeRegistry<T>)GameRegistry.findRegistry(entry.getRegistryType());
        writeUTF8String(out, RegistryManager.ACTIVE.getName(registry).toString());
        writeVarInt(out, registry.getID(entry), 5);
    }

    /**
     * Read a registry entry from the stream. The same format as in {@link #writeRegistryEntry(ByteBuf, IForgeRegistryEntry)} is used.
     * @param in the buffer to read from
     * @param registry the registry the entry belongs to
     * @return the read registry entry
     */
    @Nonnull
    public static <T extends IForgeRegistryEntry<T>> T readRegistryEntry(@Nonnull ByteBuf in, @Nonnull IForgeRegistry<T> registry)
    {
        String registryName = readUTF8String(in);
        int id = readVarInt(in, 5);
        ResourceLocation expectedRegistryName = RegistryManager.ACTIVE.getName(registry);

        if (!expectedRegistryName.toString().equals(registryName))
            throw new IllegalArgumentException("Registry mismatch: " + registryName + " != " + expectedRegistryName);

        T thing = ((ForgeRegistry<T>)registry).getRaw(id);
        if (thing == null)
            throw new IllegalArgumentException("Unknown ID " + id + " for registry " + expectedRegistryName + " received.");

        return thing;
    }

    /**
     * Write multiple registry entries from the same registry to the stream. The serialized format may be more compact than using
     * {@link #writeRegistryEntry(ByteBuf, IForgeRegistryEntry)} multiple times.
     * @param out the buffer to write to
     * @param entries the entries to write
     */
    public static <T extends IForgeRegistryEntry<T>> void writeRegistryEntries(@Nonnull ByteBuf out, @Nonnull Collection<T> entries)
    {
        writeVarInt(out, entries.size(), 5);

        Iterator<T> it = entries.iterator();
        if (it.hasNext())
        {
            T first = it.next();
            ForgeRegistry<T> registry = (ForgeRegistry<T>)GameRegistry.findRegistry(first.getRegistryType());
            writeUTF8String(out, RegistryManager.ACTIVE.getName(registry).toString());
            writeVarInt(out, registry.getID(first), 5);

            while (it.hasNext())
            {
                int id = registry.getID(it.next());
                if (id == -1)
                    throw new IllegalArgumentException("Unregistered IForgeRegistryEntry in collection " + entries + ".");
                writeVarInt(out, id, 5);
            }
        }
    }

    /**
     * Read multiple registry entries from the same registries from the stream. The list of entries must have been written by
     * {@link #writeRegistryEntries(ByteBuf, Collection)}.
     * @param in the buffer to read from
     * @param registry the registry the entries belong to
     * @return the immutable list of entries
     */
    @Nonnull
    public static <T extends IForgeRegistryEntry<T>> List<T> readRegistryEntries(@Nonnull ByteBuf in, @Nonnull IForgeRegistry<T> registry)
    {
        int size = readVarInt(in, 5);
        if (size == 0)
        {
            return ImmutableList.of();
        }
        else
        {
            String registryName = readUTF8String(in);
            ResourceLocation expectedRegistryName = RegistryManager.ACTIVE.getName(registry);
            if (!expectedRegistryName.toString().equals(registryName))
            {
                throw new IllegalArgumentException("Registry mismatch: " + registryName + " != " + expectedRegistryName);
            }

            ImmutableList.Builder<T> b = ImmutableList.builder();
            for (int i = 0; i < size; i++)
            {
                int id = readVarInt(in, 5);
                T thing = ((ForgeRegistry<T>)registry).getRaw(id);
                if (thing == null)
                    throw new IllegalArgumentException("Unknown ID " + id + " for registry " + expectedRegistryName + " received.");

                b.add(thing);
            }
            return b.build();
        }
    }

    public static String getContentDump(ByteBuf buffer)
    {
        int currentLength = buffer.readableBytes();
        StringBuffer returnString = new StringBuffer((currentLength * 3) + // The
                                                                           // hex
                (currentLength) + // The ascii
                (currentLength / 4) + // The tabs/\n's
                30); // The text

        // returnString.append("Buffer contents:\n");
        int i, j; // Loop variables
        for (i = 0; i < currentLength; i++)
        {
            if ((i != 0) && (i % 16 == 0))
            {
                // If it's a multiple of 16 and i isn't null, show the ascii
                returnString.append('\t');
                for (j = i - 16; j < i; j++)
                {
                    if (buffer.getByte(j) < 0x20 || buffer.getByte(j) > 0x7F)
                        returnString.append('.');
                    else
                        returnString.append((char) buffer.getByte(j));
                }
                // Add a linefeed after the string
                returnString.append("\n");
            }

            returnString.append(Integer.toString((buffer.getByte(i) & 0xF0) >> 4, 16) + Integer.toString((buffer.getByte(i) & 0x0F) >> 0, 16));
            returnString.append(' ');
        }

        // Add padding spaces if it's not a multiple of 16
        if (i != 0 && i % 16 != 0)
        {
            for (j = 0; j < ((16 - (i % 16)) * 3); j++)
            {
                returnString.append(' ');
            }
        }
        // Add the tab for alignment
        returnString.append('\t');

        // Add final characters at right, after padding

        // If it was at the end of a line, print out the full line
        if (i > 0 && (i % 16) == 0)
        {
            j = i - 16;
        } else
        {
            j = (i - (i % 16));
        }

        for (; i >= 0 && j < i; j++)
        {
            if (buffer.getByte(j) < 0x20 || buffer.getByte(j) > 0x7F)
                returnString.append('.');
            else
                returnString.append((char) buffer.getByte(j));
        }

        // Finally, tidy it all up with a newline
        returnString.append('\n');
        returnString.append("Length: " + currentLength);

        return returnString.toString();

    }
}
