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
package fml;

import net.minecraft.src.BiomeGenBase;
import net.minecraft.src.Block;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.EnumCreatureType;
import net.minecraft.src.Item;
import net.minecraft.src.ItemBlock;
import net.minecraft.src.ItemStack;
import net.minecraft.src.TileEntity;

public class CommonRegistry {
  static void addRecipe(ItemStack output, Object... params) {
  }

  static void addShapelessRecipe(ItemStack output, Object... params) {
  }

  static void addSmelting(int input, ItemStack output) {
  }

  public static void registerBlock(Block block) {
    Block.blocksList[block.blockID]=block;
  }

  public static void registerBlock(Block block, Class<? extends ItemBlock> itemclass) {
    registerBlock(block);
    try {
      ItemBlock bl=itemclass.getConstructor(int.class).newInstance(block.blockID);
      Item.itemsList[bl.shiftedIndex-256]=bl;
    } catch (Exception e) {
      //HMMM
    }
  }

  public static void registerEntityID(Class<? extends Entity> entityClass, String entityName, int id) {
  }

  public static void registerEntityID(Class<? extends Entity> entityClass, String entityName, int id, int background, int foreground) {
  }

  public static void registerTileEntity(Class<? extends TileEntity> tileEntityClass, String id) {
  }

  public static void addBiome(BiomeGenBase biome) {
  }

  public static void addSpawn(Class<? extends EntityLiving> entityClass, int weightedProb, int min, int max, EnumCreatureType spawnList) {
  }

  public static void addSpawn(Class<? extends EntityLiving> entityClass, int weightedProb, int min, int max, EnumCreatureType spawnList, BiomeGenBase... biomes) {
  }

  public static void addSpawn(String entityName, int weightedProb, int min, int max, EnumCreatureType spawnList) {
  }

  public static void addSpawn(String entityName, int weightedProb, int min, int max, EnumCreatureType spawnList, BiomeGenBase... biomes) {
  }

  public static void removeBiome(BiomeGenBase biome) {
  }

  public static void removeSpawn(Class<? extends EntityLiving> entityClass, EnumCreatureType spawnList) {
  }

  public static void removeSpawn(Class<? extends EntityLiving> entityClass, EnumCreatureType spawnList, BiomeGenBase... biomes) {
  }

  public static void removeSpawn(String entityName, EnumCreatureType spawnList) {
  }

  public static void removeSpawn(String entityName, EnumCreatureType spawnList, BiomeGenBase... biomes) {
  }

}
