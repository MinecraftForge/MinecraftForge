package net.minecraftforge.gui.capability;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Represents a gui provider- something that activates a gui for a player to use.
 */
public interface IGuiProvider<T>
{
    /**
     * Deserializes a buffer from the server into the provider instance.
     * In practice this is run after an open request to set the owner up on the client.
     *
     * @param buffer
     */
    IGuiProvider deserialize(ByteBuf buffer);

    /**
     * Serializes data on the server to send along with the open gui packet.
     * Use this to add any information you need to open the gui properly on the client.
     *
     * @param buffer
     */
    void serialize(ByteBuf buffer, Object... extras);

    /**
     * Gets the gui provider object from the instance, after being set with deserialize.
     *
     * @param world The world.
     * @param player The player instance.
     * @return An object that "owns" the gui- a tile, an entity, an item.
     */
    T getOwner(World world, EntityPlayer player);

    /**
     * Gets a client-side gui element to display to a player.
     *
     * @param world The world the gui is being displayed in.
     * @param player The player viewing the gui.
     * @return A Gui/GuiScreen to display on the client.
     */
    GuiScreen clientElement(World world, EntityPlayer player);

    /**
     * Gets a server-side container to handle slots and interaction on the server.
     *
     * @param world The world the gui is being accessed on.
     * @param player The player viewing the gui.
     * @return A Container the server uses for processing.
     */
    @Nullable
    Container serverElement(World world, EntityPlayer player);

    ResourceLocation getGuiIdentifier();
}
