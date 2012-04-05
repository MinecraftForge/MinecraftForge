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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Logger;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.BaseMod;
import net.minecraft.src.BiomeGenBase;
import net.minecraft.src.EntityItem;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IChunkProvider;
import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NetworkManager;
import net.minecraft.src.Packet1Login;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.Packet3Chat;
import net.minecraft.src.World;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.IFMLSidedHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;

public class FMLServerHandler implements IFMLSidedHandler
{
    private static final FMLServerHandler INSTANCE = new FMLServerHandler();

    private MinecraftServer server;

    private BiomeGenBase[] defaultOverworldBiomes;

    public void onPreLoad(MinecraftServer minecraftServer)
    {
        server = minecraftServer;
        Loader.instance().loadMods();
    }

    public void onLoadComplete()
    {
        Loader.instance().initializeMods();
    }

    public void onPreTick()
    {
        FMLCommonHandler.instance().gameTickStart();
    }

    public void onPostTick()
    {
        FMLCommonHandler.instance().gameTickEnd();
    }

    public MinecraftServer getServer()
    {
        return server;
    }

    public Logger getMinecraftLogger()
    {
        return MinecraftServer.field_6038_a;
    }

    public void onChunkPopulate(IChunkProvider chunkProvider, int chunkX, int chunkZ, World world, IChunkProvider generator)
    {
        Random fmlRandom = new Random(world.func_22079_j());
        long xSeed = fmlRandom.nextLong() >> 2 + 1L;
        long zSeed = fmlRandom.nextLong() >> 2 + 1L;
        fmlRandom.setSeed((xSeed * chunkX + zSeed * chunkZ) ^ world.func_22079_j());

        for (ModContainer mod : Loader.getModList())
        {
            if (mod.generatesWorld())
            {
                mod.getWorldGenerator().generate(fmlRandom, chunkX, chunkZ, world, generator, chunkProvider);
            }
        }
    }

    public int fuelLookup(int itemId, int itemDamage)
    {
        int fv = 0;

        for (ModContainer mod : Loader.getModList())
        {
            fv = Math.max(fv, mod.lookupFuelValue(itemId, itemDamage));
        }

        return fv;
    }

    public boolean isModLoaderMod(Class<?> clazz)
    {
        return BaseMod.class.isAssignableFrom(clazz);
    }

    public ModContainer loadBaseModMod(Class<?> clazz, String canonicalPath)
    {
        @SuppressWarnings("unchecked")
        Class <? extends BaseMod > bmClazz = (Class <? extends BaseMod >) clazz;
        return new ModLoaderModContainer(bmClazz, canonicalPath);
    }

    public void notifyItemPickup(EntityItem entityItem, EntityPlayer entityPlayer)
    {
        for (ModContainer mod : Loader.getModList())
        {
            if (mod.wantsPickupNotification())
            {
                mod.getPickupNotifier().notifyPickup(entityItem, entityPlayer);
            }
        }
    }

    public void raiseException(Throwable exception, String message, boolean stopGame)
    {
        FMLCommonHandler.instance().getFMLLogger().throwing("FMLHandler", "raiseException", exception);
        throw new RuntimeException(exception);
    }

    /**
     * @param p_21036_1_
     * @param var13
     * @param var15
     * @param var17
     * @param var9
     * @param var10
     * @param var12
     * @return
     */
    public boolean tryDispensingEntity(World world, double x, double y, double z, byte xVelocity, byte zVelocity, ItemStack item)
    {
        for (ModContainer mod : Loader.getModList())
        {
            if (mod.wantsToDispense() && mod.getDispenseHandler().dispense(x, y, z, xVelocity, zVelocity, world, item))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * @return the instance
     */
    public static FMLServerHandler instance()
    {
        return INSTANCE;
    }

    /**
     * @return
     */
    public BiomeGenBase[] getDefaultOverworldBiomes()
    {
        if (defaultOverworldBiomes == null)
        {
            ArrayList<BiomeGenBase> biomes = new ArrayList<BiomeGenBase>(20);

            for (int i = 0; i < 23; i++)
            {
                if ("Sky".equals(BiomeGenBase.field_35521_a[i].field_6163_m) || "Hell".equals(BiomeGenBase.field_35521_a[i].field_6163_m))
                {
                    continue;
                }

                biomes.add(BiomeGenBase.field_35521_a[i]);
            }

            defaultOverworldBiomes = new BiomeGenBase[biomes.size()];
            biomes.toArray(defaultOverworldBiomes);
        }

        return defaultOverworldBiomes;
    }

    public void onItemCrafted(EntityPlayer player, ItemStack craftedItem, IInventory craftingGrid)
    {
        for (ModContainer mod : Loader.getModList())
        {
            if (mod.wantsCraftingNotification())
            {
                mod.getCraftingHandler().onCrafting(player, craftedItem, craftingGrid);
            }
        }
    }

    public void onItemSmelted(EntityPlayer player, ItemStack smeltedItem)
    {
        for (ModContainer mod : Loader.getModList())
        {
            if (mod.wantsCraftingNotification())
            {
                mod.getCraftingHandler().onSmelting(player, smeltedItem);
            }
        }
    }

    public boolean handleChatPacket(Packet3Chat chat, EntityPlayer player)
    {
        for (ModContainer mod : Loader.getModList())
        {
            if (mod.wantsNetworkPackets() && mod.getNetworkHandler().onChat(chat, player))
            {
                return true;
            }
        }

        return false;
    }

    public void handlePacket250(Packet250CustomPayload packet, EntityPlayer player)
    {
        if ("REGISTER".equals(packet.field_44005_a) || "UNREGISTER".equals(packet.field_44005_a))
        {
            handleClientRegistration(packet, player);
            return;
        }

        ModContainer mod = FMLCommonHandler.instance().getModForChannel(packet.field_44005_a);

        if (mod != null)
        {
            mod.getNetworkHandler().onPacket250Packet(packet, player);
        }
    }

    /**
     * @param packet
     */
    private void handleClientRegistration(Packet250CustomPayload packet, EntityPlayer player)
    {
        try
        {
            for (String channel : new String(packet.field_44004_c, "UTF8").split("\0"))
            {
                // Skip it if we don't know it
                if (FMLCommonHandler.instance().getModForChannel(channel) == null)
                {
                    continue;
                }

                if ("REGISTER".equals(packet.field_44005_a))
                {
                    FMLCommonHandler.instance().activateChannel(player, channel);
                }
                else
                {
                    FMLCommonHandler.instance().deactivateChannel(player, channel);
                }
            }
        }
        catch (UnsupportedEncodingException e)
        {
            getMinecraftLogger().warning("Received invalid registration packet");
        }
    }

    public void handleLogin(Packet1Login loginPacket, NetworkManager networkManager)
    {
        for (ModContainer mod : Loader.getModList())
        {
            if (mod.wantsNetworkPackets())
            {
                mod.getNetworkHandler().onLogin(loginPacket, networkManager);
            }
        }

        Packet250CustomPayload packet = new Packet250CustomPayload();
        packet.field_44005_a = "REGISTER";
        packet.field_44004_c = FMLCommonHandler.instance().getPacketRegistry();
        packet.field_44003_b = packet.field_44004_c.length;
        networkManager.func_745_a(packet);
    }

    @Override
    public boolean isServer()
    {
        return true;
    }

    @Override
    public boolean isClient()
    {
        return false;
    }
}
