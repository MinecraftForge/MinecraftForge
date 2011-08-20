/**
 * This software is provided under the terms of the Minecraft Forge Public 
 * License v1.0.
 */

package net.minecraft.src.forge;

import net.minecraft.src.BiomeGenBase;
import net.minecraft.src.Block;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ItemTool;
import net.minecraft.src.World;

import java.util.*;

public class MinecraftForge {

    private static LinkedList<IBucketHandler> bucketHandlers = new LinkedList<IBucketHandler>();

    /**
     * Registers a new custom bucket handler.
     * @deprecated Spelling mistake, don't use this function!  It will go away
     * soon.
     */
    public static void registerCustomBucketHander(IBucketHandler handler) {
        bucketHandlers.add(handler);
    }

    /**
     * Register a new custom bucket handler.
     */
    public static void registerCustomBucketHandler(IBucketHandler handler) {
        bucketHandlers.add(handler);
    }

    /**
     * Registers a new sleeping handler.
     */
    public static void registerSleepHandler(ISleepHandler handler) {
	ForgeHooks.sleepHandlers.add(handler);
    }

    /**
     * Registers a new destroy tool handler.
     */
    public static void registerDestroyToolHandler(IDestroyToolHandler handler) {
	ForgeHooks.destroyToolHandlers.add(handler);
    }

    /**
     * Registers a new crafting handler.
     */
    public static void registerCraftingHandler(ICraftingHandler handler) {
        ForgeHooks.craftingHandlers.add(handler);
    }

    /**
     * This is not supposed to be called outside of Minecraft internals.
     */
    public static ItemStack fillCustomBucket(World w, int i, int j, int k) {
        for (IBucketHandler handler : bucketHandlers) {
            ItemStack stack = handler.fillCustomBucket(w, i, j, k);

            if (stack != null) {
                return stack;
            }
        }

        return null;
    }

    /** Register a tool as a tool class with a given harvest level.
     *
     * @param tool The custom tool to register.
     * @param tclass The tool class to register as.  The predefined tool
     * clases are "pickaxe", "shovel", "axe".  You can add others for custom
     * tools.
     * @param hlevel The harvest level of the tool.
     */
    public static void setToolClass(Item tool, String tclass, int hlevel) {
	    ForgeHooks.initTools();
	    ForgeHooks.toolClasses.put(tool.shiftedIndex,
		Arrays.asList(tclass,hlevel));
    }

    /** Register a block to be harvested by a tool class.  This is the metadata
     * sensitive version, use it if your blocks are using metadata variants.
     * By default, this sets the block class as effective against that type.
     *
     * @param bl The block to register.
     * @param md The metadata for the block subtype.
     * @param tclass The tool class to register as able to remove this block.
     * You may register the same block multiple times with different tool
     * classes, if multiple tool types can be used to harvest this block.
     * @param hlevel The minimum tool harvest level required to successfully
     * harvest the block.
     * @see setToolClass for details on tool classes.
     */
    public static void setBlockHarvestLevel(Block bl, int md, String tclass, 
		int hlevel) {
	    ForgeHooks.initTools();
	    List key=Arrays.asList(bl.blockID,md,tclass);
	    ForgeHooks.toolHarvestLevels.put(key,hlevel);
	    ForgeHooks.toolEffectiveness.add(key);
    }

    /** Remove a block effectiveness mapping.  Since setBlockHarvestLevel
     * makes the tool class effective against the block by default, this can be
     * used to remove that mapping.  This will force a block to be harvested at
     * the same speed regardless of tool quality, while still requiring a given
     * harvesting level.
     * @param bl The block to remove effectiveness from.
     * @param md The metadata for the block subtype.
     * @param tclass The tool class to remove the effectiveness mapping from.
     * @see setToolClass for details on tool classes.
     */
    public static void removeBlockEffectiveness(Block bl, int md,
		    String tclass) {
	    ForgeHooks.initTools();
	    List key=Arrays.asList(bl.blockID,md,tclass);
	    ForgeHooks.toolEffectiveness.remove(key);
    }

    /** Register a block to be harvested by a tool class.
     * By default, this sets the block class as effective against that type.
     *
     * @param bl The block to register.
     * @param tclass The tool class to register as able to remove this block.
     * You may register the same block multiple times with different tool
     * classes, if multiple tool types can be used to harvest this block.
     * @param hlevel The minimum tool harvest level required to successfully
     * harvest the block.
     * @see setToolClass for details on tool classes.
     */
    public static void setBlockHarvestLevel(Block bl, String tclass, 
		int hlevel) {
	    ForgeHooks.initTools();
	    for(int md=0; md<16; md++) {
		    List key=Arrays.asList(bl.blockID,md,tclass);
		    ForgeHooks.toolHarvestLevels.put(key,hlevel);
		    ForgeHooks.toolEffectiveness.add(key);
	    }
    }

    /** Remove a block effectiveness mapping.  Since setBlockHarvestLevel
     * makes the tool class effective against the block by default, this can be
     * used to remove that mapping.  This will force a block to be harvested at
     * the same speed regardless of tool quality, while still requiring a given
     * harvesting level.
     * @param bl The block to remove effectiveness from.
     * @param tclass The tool class to remove the effectiveness mapping from.
     * @see setToolClass for details on tool classes.
     */
    public static void removeBlockEffectiveness(Block bl, String tclass) {
	    ForgeHooks.initTools();
	    for(int md=0; md<16; md++) {
		    List key=Arrays.asList(bl.blockID,md,tclass);
		    ForgeHooks.toolEffectiveness.remove(key);
	    }
    }

    /**
     * This function is deprecated and will be removed eventually.
     */
    public static void addPickaxeBlockEffectiveAgainst (Block block) {
	setBlockHarvestLevel(block,"pickaxe",0);
    }

    /**
     * Kill minecraft with an error message.
     */
    public static void killMinecraft(String modname, String msg) {
	    throw new RuntimeException(modname+": "+msg);
    }

    /**
     * Version checking.  Ensures that a sufficiently recent version of Forge
     * is installed.  Will result in a fatal error if the major versions
     * mismatch or if the version is too old.  Will print a warning message if
     * the minor versions don't match.
     */
    public static void versionDetect(String modname,
		    int major, int minor, int revision) {
	    if(major!=ForgeHooks.majorVersion) {
		    killMinecraft(modname,"MinecraftForge Major Version Mismatch, expecting "+major+".x.x");
	    } else if(minor!=ForgeHooks.minorVersion) {
		    if(minor>ForgeHooks.minorVersion) {
			    killMinecraft(modname,"MinecraftForge Too Old, need at least "+major+"."+minor+"."+revision);
		    } else {
			    System.out.println(modname + ": MinecraftForge minor version mismatch, expecting "+major+"."+minor+".x, may lead to unexpected behavior");
		    }
	    } else if(revision>ForgeHooks.revisionVersion) {
		    killMinecraft(modname,"MinecraftForge Too Old, need at least "+major+"."+minor+"."+revision);
	    }
    }

    /**
     * Strict version checking.  Ensures that a sufficiently recent version of
     * Forge is installed.  Will result in a fatal error if the major or minor 
     * versions mismatch or if the version is too old.  Use this function for
     * mods that use recent, new, or unstable APIs to prevent
     * incompatibilities.
     */
    public static void versionDetectStrict(String modname,
		    int major, int minor, int revision) {
	    if(major!=ForgeHooks.majorVersion) {
		    killMinecraft(modname,"MinecraftForge Major Version Mismatch, expecting "+major+".x.x");
	    } else if(minor!=ForgeHooks.minorVersion) {
		    if(minor>ForgeHooks.minorVersion) {
			    killMinecraft(modname,"MinecraftForge Too Old, need at least "+major+"."+minor+"."+revision);
		    } else {
			    killMinecraft(modname,"MinecraftForge minor version mismatch, expecting "+major+"."+minor+".x");
		    }
	    } else if(revision>ForgeHooks.revisionVersion) {
		    killMinecraft(modname,"MinecraftForge Too Old, need at least "+major+"."+minor+"."+revision);
	    }
    }

}
