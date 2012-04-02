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


public class CommonRegistry {
  public static void addRecipe(ItemStack output, Object... params) {
    CraftingManager.func_20151_a().func_20153_a(output, params);
  }

  public static void addShapelessRecipe(ItemStack output, Object... params) {
    CraftingManager.func_20151_a().func_21146_b(output, params);
  }

  public static void addSmelting(int input, ItemStack output) {
    FurnaceRecipes.func_21162_a().func_21160_a(input, output);
  }

  public static void registerBlock(Block block) {
    registerBlock(block,ItemBlock.class);
  }

  public static void registerBlock(Block block, Class<? extends ItemBlock> itemclass) {
    try {
      assert block!=null : "registerBlock: block cannot be null";
      assert itemclass!=null : "registerBlock: itemclass cannot be null";
      int blockItemId=block.field_573_bc-256;
      itemclass.getConstructor(int.class).newInstance(blockItemId);
    } catch (Exception e) {
      //HMMM
    }
  }

  public static void registerEntityID(Class<? extends Entity> entityClass, String entityName, int id) {
    EntityList.addNewEntityListMapping(entityClass, entityName, id);
  }

  public static void registerEntityID(Class<? extends Entity> entityClass, String entityName, int id, int backgroundEggColour, int foregroundEggColour) {
    EntityList.addNewEntityListMapping(entityClass, entityName, id, backgroundEggColour, foregroundEggColour);
  }

  public static void registerTileEntity(Class<? extends TileEntity> tileEntityClass, String id) {
    TileEntity.addNewTileEntityMapping(tileEntityClass, id);
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
