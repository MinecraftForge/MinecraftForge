package net.minecraftforge.gui.capability;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Represents a gui provider- something that activates a gui for a player to use.
 */
public interface IGuiProvider<T>
{
    /**
     * Deserializes a buffer from the server into the provider instance.
     *
     * @param buffer
     */
    void deserialize(ByteBuf buffer);

    /**
     * Serializes data on the server to send along with the open gui packet.
     * Use this to add any information you need to open the gui properly on the client.
     *
     * @param buffer
     */
    void serialize(ByteBuf buffer);

    /**
     * Gets the gui provider object from the world, player, etc.
     * Used in the get*Element methods to pass a direct instance to them.
     *
     * @param player The player instance.
     * @param world The world.
     * @param hand The hand being used to access the gui. May be null if not required.
     * @return An object that "owns" the gui- a tile, an entity, an item.
     */
    T getOwner(EntityPlayer player, World world, @Nullable EnumHand hand);

    /**
     * Gets a client-side gui element to display to a player.
     *
     * @param player The player viewing the gui.
     * @param world The world the gui is being displayed in.
     * @param owner The owner object of the gui. Usually a tile, item, or entity.
     * @return A Gui/GuiScreen to display on the client.
     */
    @Nullable
    Object getClientGuiElement(EntityPlayer player, World world, T owner);

    /**
     * Gets a server-side container to handle slots and interaction on the server.
     *
     * @param player The player viewing the gui.
     * @param world The world the gui is being accessed on.
     * @param owner The owner object. Usually a tile, item, or entity.
     * @return A Container the server uses for processing.
     */
    @Nullable
    Container getServerGuiElement(EntityPlayer player, World world, T owner);
}
