/*
 * The FML Forge Mod Loader suite. Copyright (C) 2012 cpw
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */
package cpw.mods.fml.server;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.logging.Logger;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.BaseMod;
import net.minecraft.src.BiomeGenBase;
import net.minecraft.src.Block;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityItem;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.IChunkProvider;
import net.minecraft.src.ICommandManager;
import net.minecraft.src.IInventory;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.MLProp;
import net.minecraft.src.NetworkManager;
import net.minecraft.src.Packet;
import net.minecraft.src.Packet1Login;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.Packet3Chat;
import net.minecraft.src.Profiler;
import net.minecraft.src.StringTranslate;
import net.minecraft.src.World;
import net.minecraft.src.WorldType;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.IFMLSidedHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.common.ProxyInjector;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.modloader.ModLoaderModContainer;
import cpw.mods.fml.common.modloader.ModProperty;
import cpw.mods.fml.common.network.EntitySpawnAdjustmentPacket;
import cpw.mods.fml.common.network.EntitySpawnPacket;
import cpw.mods.fml.common.registry.LanguageRegistry;

/**
 * Handles primary communication from hooked code into the system
 *
 * The FML entry point is {@link #onPreLoad(MinecraftServer)} called from
 * {@link MinecraftServer}
 *
 * Obfuscated code should focus on this class and other members of the "server"
 * (or "client") code
 *
 * The actual mod loading is handled at arms length by {@link Loader}
 *
 * It is expected that a similar class will exist for each target environment:
 * Bukkit and Client side.
 *
 * It should not be directly modified.
 *
 * @author cpw
 *
 */
public class FMLServerHandler implements IFMLSidedHandler
{
    /**
     * The singleton
     */
    private static final FMLServerHandler INSTANCE = new FMLServerHandler();

    /**
     * A reference to the server itself
     */
    private MinecraftServer server;

    private FMLServerHandler()
    {
        FMLCommonHandler.instance().beginLoading(this);
    }
    /**
     * Called to start the whole game off from
     * {@link MinecraftServer#startServer}
     *
     * @param minecraftServer
     */
    public void beginServerLoading(MinecraftServer minecraftServer)
    {
        server = minecraftServer;
        ObfuscationReflectionHelper.detectObfuscation(World.class);
        Loader.instance().loadMods();
    }

    /**
     * Called a bit later on during server initialization to finish loading mods
     */
    public void finishServerLoading()
    {
        Loader.instance().initializeMods();
        LanguageRegistry.reloadLanguageTable();
    }

    @Override
    public void haltGame(String message, Throwable exception)
    {
        throw new RuntimeException(message, exception);
    }

    /**
     * Get the server instance
     *
     * @return
     */
    public MinecraftServer getServer()
    {
        return server;
    }

    /**
     * @return the instance
     */
    public static FMLServerHandler instance()
    {
        return INSTANCE;
    }

    /* (non-Javadoc)
     * @see cpw.mods.fml.common.IFMLSidedHandler#getAdditionalBrandingInformation()
     */
    @Override
    public List<String> getAdditionalBrandingInformation()
    {
        return null;
    }

    /* (non-Javadoc)
     * @see cpw.mods.fml.common.IFMLSidedHandler#getSide()
     */
    @Override
    public Side getSide()
    {
        return Side.SERVER;
    }

    @Override
    public void showGuiScreen(Object clientGuiElement)
    {

    }

    @Override
    public Entity spawnEntityIntoClientWorld(Class<? extends Entity> entityClass, EntitySpawnPacket packet)
    {
        // NOOP
        return null;
    }

    @Override
    public void adjustEntityLocationOnClient(EntitySpawnAdjustmentPacket entitySpawnAdjustmentPacket)
    {
        // NOOP
    }
    @Override
    public void sendPacket(Packet packet)
    {
        throw new RuntimeException("You cannot send a bare packet without a target on the server!");
    }
}
