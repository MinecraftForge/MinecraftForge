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
package net.minecraft.src;

import java.util.List;
import java.util.logging.Logger;

import fml.Loader;
import fml.ml.ModLoaderModContainer;
import fml.obf.FMLHandler;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.Achievement;
import net.minecraft.src.BiomeGenBase;
import net.minecraft.src.Block;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EnumCreatureType;
import net.minecraft.src.IChunkProvider;
import net.minecraft.src.IInventory;
import net.minecraft.src.ItemBlock;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;

public class ModLoader {
  static void addAchievementDesc(Achievement achievement, String name, String description) {
  }

  static int addAllFuel(int id, int metadata) {
    return 0;
  }

  static int addArmor(String armor) {
    return 0;
  }

  static void addBiome(BiomeGenBase biome) {
  }

  static void addLocalization(String key, String value) {
  }

  static void addLocalization(String key, String lang, String value) {
  }

  static void addName(Object instance, String name) {
  }

  static void addName(Object instance, String lang, String name) {
  }

  static int addOverride(String fileToOverride, String fileToAdd) {
    return 0;
  }

  static void addOverride(String path, String overlayPath, int index) {
  }

  static void addRecipe(ItemStack output, Object... params) {
    CraftingManager.getInstance().addRecipe(output, params);
  }

  static void addShapelessRecipe(ItemStack output, Object... params) {
    CraftingManager.getInstance().addShapelessRecipe(output, params);
  }

  static void addSmelting(int input, ItemStack output) {
    FurnaceRecipes.smelting().addSmelting(input, output);
  }

  static void addSpawn(Class<? extends EntityLiving> entityClass, int weightedProb, int min, int max, EnumCreatureType spawnList) {
    CommonRegistry.addSpawn(entityClass, weightedProb, min, max, spawnList);
  }

  static void addSpawn(Class<? extends EntityLiving> entityClass, int weightedProb, int min, int max, EnumCreatureType spawnList, BiomeGenBase... biomes) {
    CommonRegistry.addSpawn(entityClass, weightedProb, min, max, spawnList, biomes);
  }

  static void addSpawn(String entityName, int weightedProb, int min, int max, EnumCreatureType spawnList) {
    CommonRegistry.addSpawn(entityName, weightedProb, min, max, spawnList);
  }

  static void addSpawn(String entityName, int weightedProb, int min, int max, EnumCreatureType spawnList, BiomeGenBase... biomes) {
    CommonRegistry.addSpawn(entityName, weightedProb, min, max, spawnList, biomes);
  }

  static boolean dispenseEntity(World world, double x, double y, double z, int xVel, int zVel, ItemStack item) {
    return false;
  }

  static void genericContainerRemoval(World world, int x, int y, int z) {
  }

  static List<BaseMod> getLoadedMods() {
    return ModLoaderModContainer.findAll();
  }

  public static Logger getLogger() {
    return FMLHandler.getMinecraftLogger();
  }

  static <T, E> T getPrivateValue(Class<? super E> instanceclass, E instance, int fieldindex) {
    return null;
  }

  static <T, E> T getPrivateValue(Class<? super E> instanceclass, E instance, String field) {
    return null;
  }

  static int getUniqueEntityId() {
    return 0;
  }

  static boolean isModLoaded(String modname) {
    return Loader.isModLoaded(modname);
  }

  static void receivePacket(Packet250CustomPayload packet) {
  }

  static void registerBlock(Block block) {
    CommonRegistry.registerBlock(block);
  }

  static void registerBlock(Block block, Class<? extends ItemBlock> itemclass) {
    CommonRegistry.registerBlock(block, itemclass);
  }

  static void registerEntityID(Class<? extends Entity> entityClass, String entityName, int id) {
    CommonRegistry.registerEntityID(entityClass, entityName, id);
  }

  static void registerEntityID(Class<? extends Entity> entityClass, String entityName, int id, int background, int foreground) {
    CommonRegistry.registerEntityID(entityClass, entityName, id, background, foreground);
  }

  static void registerPacketChannel(BaseMod mod, String channel) {
  }

  static void registerTileEntity(Class<? extends TileEntity> tileEntityClass, String id) {
    CommonRegistry.registerTileEntity(tileEntityClass, id);
  }

  static void removeBiome(BiomeGenBase biome) {
    CommonRegistry.removeBiome(biome);
  }

  static void removeSpawn(Class<? extends EntityLiving> entityClass, EnumCreatureType spawnList) {
    CommonRegistry.removeSpawn(entityClass, spawnList);
  }

  static void removeSpawn(Class<? extends EntityLiving> entityClass, EnumCreatureType spawnList, BiomeGenBase... biomes) {
    CommonRegistry.removeSpawn(entityClass, spawnList, biomes);
  }

  static void removeSpawn(String entityName, EnumCreatureType spawnList) {
    CommonRegistry.removeSpawn(entityName, spawnList);
  }

  static void removeSpawn(String entityName, EnumCreatureType spawnList, BiomeGenBase... biomes) {
    CommonRegistry.removeSpawn(entityName, spawnList, biomes);
  }

  static void saveConfig() {
  }

  static void serverChat(String text) {
  }

  /**
   * Indicate that you want to receive ticks
   * 
   * @param mod receiving the events
   * @param enable indicates whether you want to recieve them or not
   * @param useClock Not used in server side: all ticks are sent on the server side (no render subticks)
   */
  static void setInGameHook(BaseMod mod, boolean enable, boolean useClock) {
    ModLoaderModContainer mlmc=(ModLoaderModContainer) ModLoaderModContainer.findContainerFor(mod);
    mlmc.setTicking(enable);
  }

  static <T, E> void setPrivateValue(Class<? super T> instanceclass, T instance, int fieldindex, E value) {
  }

  static <T, E> void setPrivateValue(Class<? super T> instanceclass, T instance, String field, E value) {
  }

  static void takenFromCrafting(EntityPlayer player, ItemStack item, IInventory matrix) {
  }

  static void takenFromFurnace(EntityPlayer player, ItemStack item) {
  }

  static void throwException(String message, Throwable e) {
  }

  public static MinecraftServer getMinecraftServerInstance() {
    return FMLHandler.INSTANCE.getServer();
  }
}