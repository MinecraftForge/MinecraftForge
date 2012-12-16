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
package cpw.mods.fml.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.NetClientHandler;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.crash.CrashReport;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet131MapData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.MapDifference;
import com.google.common.collect.MapDifference.ValueDifference;

import cpw.mods.fml.client.modloader.ModLoaderClientHelper;
import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.DuplicateModsFoundException;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.IFMLSidedHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.LoaderException;
import cpw.mods.fml.common.MetadataCollection;
import cpw.mods.fml.common.MissingModsException;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.common.WrongMinecraftVersionException;
import cpw.mods.fml.common.network.EntitySpawnAdjustmentPacket;
import cpw.mods.fml.common.network.EntitySpawnPacket;
import cpw.mods.fml.common.network.ModMissingPacket;
import cpw.mods.fml.common.registry.EntityRegistry.EntityRegistration;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.common.registry.IThrowableEntity;
import cpw.mods.fml.common.registry.ItemData;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;


/**
 * Handles primary communication from hooked code into the system
 *
 * The FML entry point is {@link #beginMinecraftLoading(Minecraft)} called from
 * {@link Minecraft}
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
public class FMLClientHandler implements IFMLSidedHandler
{
    /**
     * The singleton
     */
    private static final FMLClientHandler INSTANCE = new FMLClientHandler();

    /**
     * A reference to the server itself
     */
    private Minecraft client;

    private DummyModContainer optifineContainer;

    private boolean guiLoaded;

    private boolean serverIsRunning;

    private MissingModsException modsMissing;

    private boolean loading;

    private WrongMinecraftVersionException wrongMC;

    private CustomModLoadingErrorDisplayException customError;

	private DuplicateModsFoundException dupesFound;

    private boolean serverShouldBeKilledQuietly;

    /**
     * Called to start the whole game off
     *
     * @param minecraft The minecraft instance being launched
     */
    public void beginMinecraftLoading(Minecraft minecraft)
    {
        if (minecraft.func_71355_q())
        {
            FMLLog.severe("DEMO MODE DETECTED, FML will not work. Finishing now.");
            haltGame("FML will not run in demo mode", new RuntimeException());
            return;
        }

        loading = true;
        client = minecraft;
        ObfuscationReflectionHelper.detectObfuscation(World.class);
        TextureFXManager.instance().setClient(client);
        FMLCommonHandler.instance().beginLoading(this);
        new ModLoaderClientHelper(client);
        try
        {
            Class<?> optifineConfig = Class.forName("Config", false, Loader.instance().getModClassLoader());
            String optifineVersion = (String) optifineConfig.getField("VERSION").get(null);
            Map<String,Object> dummyOptifineMeta = ImmutableMap.<String,Object>builder().put("name", "Optifine").put("version", optifineVersion).build();
            ModMetadata optifineMetadata = MetadataCollection.from(getClass().getResourceAsStream("optifinemod.info"),"optifine").getMetadataForId("optifine", dummyOptifineMeta);
            optifineContainer = new DummyModContainer(optifineMetadata);
            FMLLog.info("Forge Mod Loader has detected optifine %s, enabling compatibility features",optifineContainer.getVersion());
        }
        catch (Exception e)
        {
            optifineContainer = null;
        }
        try
        {
            Loader.instance().loadMods();
        }
        catch (WrongMinecraftVersionException wrong)
        {
            wrongMC = wrong;
        }
        catch (DuplicateModsFoundException dupes)
        {
        	dupesFound = dupes;
        }
        catch (MissingModsException missing)
        {
            modsMissing = missing;
        }
        catch (CustomModLoadingErrorDisplayException custom)
        {
            FMLLog.log(Level.SEVERE, custom, "A custom exception was thrown by a mod, the game will now halt");
            customError = custom;
        }
        catch (LoaderException le)
        {
            haltGame("There was a severe problem during mod loading that has caused the game to fail", le);
            return;
        }
    }

    @Override
    public void haltGame(String message, Throwable t)
    {
        client.func_71377_b(new CrashReport(message, t));
        throw Throwables.propagate(t);
    }
    /**
     * Called a bit later on during initialization to finish loading mods
     * Also initializes key bindings
     *
     */
    @SuppressWarnings("deprecation")
    public void finishMinecraftLoading()
    {
        if (modsMissing != null || wrongMC != null || customError!=null || dupesFound!=null)
        {
            return;
        }
        try
        {
            Loader.instance().initializeMods();
        }
        catch (CustomModLoadingErrorDisplayException custom)
        {
            FMLLog.log(Level.SEVERE, custom, "A custom exception was thrown by a mod, the game will now halt");
            customError = custom;
            return;
        }
        catch (LoaderException le)
        {
            haltGame("There was a severe problem during mod loading that has caused the game to fail", le);
            return;
        }
        LanguageRegistry.reloadLanguageTable();
        RenderingRegistry.instance().loadEntityRenderers((Map<Class<? extends Entity>, Render>)RenderManager.field_78727_a.field_78729_o);

        loading = false;
        KeyBindingRegistry.instance().uploadKeyBindingsToGame(client.field_71474_y);
    }

    public void onInitializationComplete()
    {
        if (wrongMC != null)
        {
            client.func_71373_a(new GuiWrongMinecraft(wrongMC));
        }
        else if (modsMissing != null)
        {
            client.func_71373_a(new GuiModsMissing(modsMissing));
        }
        else if (dupesFound != null)
        {
        	client.func_71373_a(new GuiDupesFound(dupesFound));
        }
        else if (customError != null)
        {
            client.func_71373_a(new GuiCustomModLoadingErrorScreen(customError));
        }
        else
        {
            TextureFXManager.instance().loadTextures(client.field_71418_C.func_77292_e());
        }
    }
    /**
     * Get the server instance
     */
    public Minecraft getClient()
    {
        return client;
    }

    /**
     * Get a handle to the client's logger instance
     * The client actually doesn't have one- so we return null
     */
    public Logger getMinecraftLogger()
    {
        return null;
    }

    /**
     * @return the instance
     */
    public static FMLClientHandler instance()
    {
        return INSTANCE;
    }

    /**
     * @param player
     * @param gui
     */
    public void displayGuiScreen(EntityPlayer player, GuiScreen gui)
    {
        if (client.field_71439_g==player && gui != null) {
            client.func_71373_a(gui);
        }
    }

    /**
     * @param mods
     */
    public void addSpecialModEntries(ArrayList<ModContainer> mods)
    {
        if (optifineContainer!=null) {
            mods.add(optifineContainer);
        }
    }

    @Override
    public List<String> getAdditionalBrandingInformation()
    {
        if (optifineContainer!=null)
        {
            return Arrays.asList(String.format("Optifine %s",optifineContainer.getVersion()));
        } else {
            return ImmutableList.<String>of();
        }
    }

    @Override
    public Side getSide()
    {
        return Side.CLIENT;
    }

    public boolean hasOptifine()
    {
        return optifineContainer!=null;
    }

    @Override
    public void showGuiScreen(Object clientGuiElement)
    {
        GuiScreen gui = (GuiScreen) clientGuiElement;
        client.func_71373_a(gui);
    }

    @Override
    public Entity spawnEntityIntoClientWorld(EntityRegistration er, EntitySpawnPacket packet)
    {
        WorldClient wc = client.field_71441_e;

        Class<? extends Entity> cls = er.getEntityClass();

        try
        {
            Entity entity;
            if (er.hasCustomSpawning())
            {
                entity = er.doCustomSpawning(packet);
            }
            else
            {
                entity = (Entity)(cls.getConstructor(World.class).newInstance(wc));
                entity.field_70157_k = packet.entityId;
                entity.func_70012_b(packet.scaledX, packet.scaledY, packet.scaledZ, packet.scaledYaw, packet.scaledPitch);
                if (entity instanceof EntityLiving)
                {
                    ((EntityLiving)entity).field_70759_as = packet.scaledHeadYaw;
                }

            }

            entity.field_70118_ct = packet.rawX;
            entity.field_70117_cu = packet.rawY;
            entity.field_70116_cv = packet.rawZ;

            if (entity instanceof IThrowableEntity)
            {
                Entity thrower = client.field_71439_g.field_70157_k == packet.throwerId ? client.field_71439_g : wc.func_73045_a(packet.throwerId);
                ((IThrowableEntity)entity).setThrower(thrower);
            }


            Entity parts[] = entity.func_70021_al();
            if (parts != null)
            {
                int i = packet.entityId - entity.field_70157_k;
                for (int j = 0; j < parts.length; j++)
                {
                    parts[j].field_70157_k += i;
                }
            }


            if (packet.metadata != null)
            {
                entity.func_70096_w().func_75687_a((List)packet.metadata);
            }

            if (packet.throwerId > 0)
            {
                entity.func_70016_h(packet.speedScaledX, packet.speedScaledY, packet.speedScaledZ);
            }

            if (entity instanceof IEntityAdditionalSpawnData)
            {
                ((IEntityAdditionalSpawnData)entity).readSpawnData(packet.dataStream);
            }

            wc.func_73027_a(packet.entityId, entity);
            return entity;
        }
        catch (Exception e)
        {
            FMLLog.log(Level.SEVERE, e, "A severe problem occurred during the spawning of an entity");
            throw Throwables.propagate(e);
        }
    }

    @Override
    public void adjustEntityLocationOnClient(EntitySpawnAdjustmentPacket packet)
    {
        Entity ent = client.field_71441_e.func_73045_a(packet.entityId);
        if (ent != null)
        {
            ent.field_70118_ct = packet.serverX;
            ent.field_70117_cu = packet.serverY;
            ent.field_70116_cv = packet.serverZ;
        }
        else
        {
            FMLLog.fine("Attempted to adjust the position of entity %d which is not present on the client", packet.entityId);
        }
    }

    @Override
    public void beginServerLoading(MinecraftServer server)
    {
        serverShouldBeKilledQuietly = false;
        // NOOP
    }

    @Override
    public void finishServerLoading()
    {
        // NOOP
    }

    @Override
    public MinecraftServer getServer()
    {
        return client.func_71401_C();
    }

    @Override
    public void sendPacket(Packet packet)
    {
        if(client.field_71439_g != null)
        {
            client.field_71439_g.field_71174_a.func_72552_c(packet);
        }
    }

    @Override
    public void displayMissingMods(ModMissingPacket modMissingPacket)
    {
        client.func_71373_a(new GuiModsMissingForServer(modMissingPacket));
    }

    /**
     * If the client is in the midst of loading, we disable saving so that custom settings aren't wiped out
     */
    public boolean isLoading()
    {
        return loading;
    }

    @Override
    public void handleTinyPacket(NetHandler handler, Packet131MapData mapData)
    {
        ((NetClientHandler)handler).fmlPacket131Callback(mapData);
    }

    @Override
    public void setClientCompatibilityLevel(byte compatibilityLevel)
    {
        NetClientHandler.setConnectionCompatibilityLevel(compatibilityLevel);
    }

    @Override
    public byte getClientCompatibilityLevel()
    {
        return NetClientHandler.getConnectionCompatibilityLevel();
    }

    public void warnIDMismatch(MapDifference<Integer, ItemData> idDifferences, boolean mayContinue)
    {
        GuiIdMismatchScreen mismatch = new GuiIdMismatchScreen(idDifferences, mayContinue);
        client.func_71373_a(mismatch);
    }

    public void callbackIdDifferenceResponse(boolean response)
    {
        if (response)
        {
            serverShouldBeKilledQuietly = false;
            GameData.releaseGate(true);
            client.continueWorldLoading();
        }
        else
        {
            serverShouldBeKilledQuietly = true;
            GameData.releaseGate(false);
            // Reset and clear the client state
            client.func_71403_a((WorldClient)null);
            client.func_71373_a(null);
        }
    }

    @Override
    public boolean shouldServerShouldBeKilledQuietly()
    {
        return serverShouldBeKilledQuietly;
    }

    @Override
    public void disconnectIDMismatch(MapDifference<Integer, ItemData> s, NetHandler toKill, INetworkManager mgr)
    {
        boolean criticalMismatch = !s.entriesOnlyOnLeft().isEmpty();
        for (Entry<Integer, ValueDifference<ItemData>> mismatch : s.entriesDiffering().entrySet())
        {
            ValueDifference<ItemData> vd = mismatch.getValue();
            if (!vd.leftValue().mayDifferByOrdinal(vd.rightValue()))
            {
                criticalMismatch = true;
            }
        }

        if (!criticalMismatch)
        {
            // We'll carry on with this connection, and just log a message instead
            return;
        }
        // Nuke the connection
        ((NetClientHandler)toKill).func_72553_e();
        // Stop GuiConnecting
        GuiConnecting.forceTermination((GuiConnecting)client.field_71462_r);
        // pulse the network manager queue to clear cruft
        mgr.func_74428_b();
        // Nuke the world client
        client.func_71403_a((WorldClient)null);
        // Show error screen
        warnIDMismatch(s, false);
    }
}
