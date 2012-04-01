/*
 * The FML Forge Mod Loader suite.
 * Copyright (C) 2012 cpw
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
package fml.obf;

import java.util.Random;
import java.util.logging.Logger;

import fml.FMLHooks;
import fml.Loader;
import fml.ModContainer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.BaseMod;
import net.minecraft.src.IChunkProvider;
import net.minecraft.src.World;

public enum FMLHandler {
  INSTANCE;
  private MinecraftServer server;

  public void onPreLoad(MinecraftServer minecraftServer) {
    INSTANCE.server=minecraftServer;
    Loader.instance().loadMods();
  }

  public void onLoadComplete() {
    Loader.instance().initializeMods();
  }

  public void onPreTick() {
    FMLHooks.INSTANCE.serverTickStart();
  }

  public void onPostTick() {
    FMLHooks.INSTANCE.serverTickEnd();
  }

  public MinecraftServer getServer() {
    return server;
  }

  public static Logger getMinecraftLogger() {
    return MinecraftServer.logger;
  }

  public void onChunkPopulate(IChunkProvider chunkProvider, int chunkX, int chunkZ, World world, IChunkProvider generator) {
    Random fmlRandom=new Random(world.getSeed());
    long xSeed=fmlRandom.nextLong()>>2 + 1L;
    long zSeed=fmlRandom.nextLong()>>2 + 1L;
    
    fmlRandom.setSeed((xSeed * chunkX + zSeed * chunkZ)^world.getSeed());
    
    for (ModContainer mod : Loader.getModList()) {
      if (mod.generatesWorld()) {
        mod.getWorldGenerator().generate(fmlRandom, chunkX, chunkZ, world, generator, chunkProvider);
      }
    }
  }

  public int fuelLookup(int itemId, int itemDamage) {
    int fv=0;
    for (ModContainer mod : Loader.getModList()) {
      fv=Math.max(fv, mod.lookupFuelValue(itemId, itemDamage));
    }
    return fv;
  }
}
