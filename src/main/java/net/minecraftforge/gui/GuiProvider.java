package net.minecraftforge.gui;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public abstract class GuiProvider extends IForgeRegistryEntry.Impl<GuiProvider> {

    /**
     * The owner of this gui provider.
     */
    public Object owner;

    public GuiProvider()
    {
        this(null);
    }

    public GuiProvider(Object in)
    {
        this.owner = in;
    }

    /**
     * Serializes data on the server to send along with the open gui packet.
     * Use this to add any information you need to open the gui properly on the client.
     *
     * @param buffer
     */
    public abstract void serialize(ByteBuf buffer, Object... extras);

    /**
     * Deserializes a buffer from the server into the provider instance.
     * In practice this is run after an open request to set up fields on the client.
     *
     * @param buffer
     * @param world The world the gui is being accessed on.
     * @param player The player accessing the gui.
     * @return An instance of GuiProvider- typically this, allows for chaining.
     */
    public abstract GuiProvider deserialize(ByteBuf buffer, World world, EntityPlayer player);

    /**
     * Gets a client-side gui element to display to a player.
     *
     * @param world The world the gui is being displayed in.
     * @param player The player viewing the gui.
     * @return A Gui/GuiScreen to display on the client.
     */
    public abstract Object clientElement(World world, EntityPlayer player);

    /**
     * Gets a server-side container to handle slots and interaction on the server.
     *
     * @param world The world the gui is being accessed on.
     * @param player The player viewing the gui.
     * @return A Container the server uses for processing.
     */
    @Nullable
    public Container serverElement(World world, EntityPlayer player) { return null; }
}
