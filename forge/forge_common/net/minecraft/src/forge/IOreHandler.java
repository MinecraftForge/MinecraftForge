/**
 * This software is provided under the terms of the Minecraft Forge Public
 * License v1.0.
 */

package net.minecraft.src.forge;

import net.minecraft.src.ItemStack;

/** The current list of known classes.
 * oreTin, oreCopper, oreSilver, oreUranium, oreZinc
 * oreTurquoise, oreChalcocite, oreCassiterite, oreSphalerite, oreCerussite
 * oreCobalt, oreArdite, oreMyuvil, oreGalena, oreIvymetal, oreAggregate
 * oreBloodbite, oreGrudge, oreWistful, oreFlamelash, oreTears, oreOnyx
 * ingotTin, ingotCopper, ingotSilver, ingotBrass, ingotBronze, ingotZinc
 * ingotUranium, ingotRefinedIron
 * ingotCobalt, ingotArdite, ingotIvymetal
 * ingotCordite, ingotRootedCobalt, ingotManyullyn
 * ingotSteel, ingotLead
 * ingotBloodbite, ingotGrudge, ingotWistful, ingotFlamelash, ingotTears, ingotOnyx
 * dyeBlue
 * gemRuby, gemEmerald, gemSapphire, gemTopaz
 * gemAmethyst, gemQuartz, gemRoseQuartz, gemRockCrystal
 * itemDropUranium
 * woodRubber
 * itemRubber
 * redstoneCrystal, glowstoneCrystal, lavaCrystal, blazeCrystal, slimeCrystal
 * stoneRod, ironRod, diamondRod, goldRod, redstoneRod, obsidianRod,
 * sandstoneRod, paperRod, mossyRod, netherrackRod, glowstoneRod, lavaRod,
 * iceRod, slimeRod, cactusRod, flintRod, brickRod,
 * woodShard, stoneShard, ironShard, diamondShard, goldShard, redstoneShard,
 * obsidianShard, sandstoneShard, netherrackShard, glowstoneShard, iceShard,
 * lavaShard, slimeShard, flintShard, brickShard, blazeShard,
 * glassShard, coalShard
 * customStone, customCobblestone
 */

public interface IOreHandler
{
    /** Called when a new ore is registered with the ore dictionary.
     * @param oreClass The string class of the ore.
     * @param ore The ItemStack for the ore.
     */
    public void registerOre(String oreClass, ItemStack ore);
}

