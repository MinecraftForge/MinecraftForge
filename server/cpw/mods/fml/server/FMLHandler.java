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

import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Logger;

import cpw.mods.fml.common.FMLHooks;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.BaseMod;
import net.minecraft.src.BiomeGenBase;
import net.minecraft.src.EntityItem;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IChunkProvider;
import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;

public class FMLHandler {
  private static final FMLHandler INSTANCE=new FMLHandler();
  
  private MinecraftServer server;

  private BiomeGenBase[] defaultOverworldBiomes;

  public void onPreLoad(MinecraftServer minecraftServer) {
    server = minecraftServer;
    Loader.instance().loadMods();
  }

  public void onLoadComplete() {
    Loader.instance().initializeMods();
  }

  public void onPreTick() {
    FMLHooks.instance().gameTickStart();
  }

  public void onPostTick() {
    FMLHooks.instance().gameTickEnd();
  }

  public MinecraftServer getServer() {
    return server;
  }

  public static Logger getMinecraftLogger() {
    return MinecraftServer.field_6038_a;
  }

  public void onChunkPopulate(IChunkProvider chunkProvider, int chunkX, int chunkZ, World world, IChunkProvider generator) {
    Random fmlRandom = new Random(world.func_22079_j());
    long xSeed = fmlRandom.nextLong() >> 2 + 1L;
    long zSeed = fmlRandom.nextLong() >> 2 + 1L;

    fmlRandom.setSeed((xSeed * chunkX + zSeed * chunkZ) ^ world.func_22079_j());

    for (ModContainer mod : Loader.getModList()) {
      if (mod.generatesWorld()) {
        mod.getWorldGenerator().generate(fmlRandom, chunkX, chunkZ, world, generator, chunkProvider);
      }
    }
  }

  public int fuelLookup(int itemId, int itemDamage) {
    int fv = 0;
    for (ModContainer mod : Loader.getModList()) {
      fv = Math.max(fv, mod.lookupFuelValue(itemId, itemDamage));
    }
    return fv;
  }

  public boolean isModLoaderMod(Class<?> clazz) {
    return BaseMod.class.isAssignableFrom(clazz);
  }

  public ModContainer loadBaseModMod(Class<?> clazz, String canonicalPath) {
    @SuppressWarnings("unchecked")
    Class<? extends BaseMod> bmClazz = (Class<? extends BaseMod>) clazz;
    return new ModLoaderModContainer(bmClazz, canonicalPath);
  }

  public void notifyItemPickup(EntityItem entityItem, EntityPlayer entityPlayer) {
    for (ModContainer mod : Loader.getModList()) {
      if (mod.wantsPickupNotification()) {
        mod.getPickupNotifier().notifyPickup(entityItem, entityPlayer);
      }
    }
  }

  public static Logger getFMLLogger() {
    return Loader.log;
  }

  public void raiseException(Throwable exception, String message, boolean stopGame) {
    getFMLLogger().throwing("FMLHandler", "raiseException", exception);
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
  public boolean tryDispensingEntity(World world, double x, double y, double z, byte xVelocity, byte zVelocity, ItemStack item) {
    for (ModContainer mod : Loader.getModList()) {
      if (mod.wantsToDispense() && mod.getDispenseHandler().dispense(x, y, z, xVelocity, zVelocity, world, item)) {
        return true;
      }
    }
    return false;
  }

  /**
   * @return the instance
   */
  public static FMLHandler instance() {
    return INSTANCE;
  }

  /**
   * @return
   */
  public BiomeGenBase[] getDefaultOverworldBiomes() {
    if (defaultOverworldBiomes==null) {
      ArrayList<BiomeGenBase> biomes=new ArrayList<BiomeGenBase>(20);
      for (int i=0; i<23; i++) {
        if ("Sky".equals(BiomeGenBase.field_35521_a[i].field_6163_m) || "Hell".equals(BiomeGenBase.field_35521_a[i].field_6163_m)) {
          continue;
        }
        biomes.add(BiomeGenBase.field_35521_a[i]);
      }
      defaultOverworldBiomes=new BiomeGenBase[biomes.size()];
      biomes.toArray(defaultOverworldBiomes);
    }
    return defaultOverworldBiomes;
  }
  
  public void onItemCrafted(EntityPlayer player, ItemStack craftedItem, IInventory craftingGrid) {
    for (ModContainer mod : Loader.getModList()) {
      if (mod.wantsCraftingNotification()) {
        mod.getCraftingHandler().onCrafting(player,craftedItem,craftingGrid);
      }
    }
  }
  
  public void onItemSmelted(EntityPlayer player, ItemStack smeltedItem) {
    for (ModContainer mod : Loader.getModList()) {
      if (mod.wantsCraftingNotification()) {
        mod.getCraftingHandler().onSmelting(player,smeltedItem);
      }
    }
  }
}
