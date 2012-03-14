package net.minecraft.src.core;

import net.minecraft.src.*;

public class InfiRecipeHoes
{
    public InfiRecipeHoes()
    {
    }

    public static int recipeStorm()
    {
        mod_InfiTools.stWoodHoe.setIconCoord(1, 0);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.stWoodHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiTools.saWoodHoe.setIconCoord(2, 0);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.saWoodHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_InfiTools.bWoodHoe.setIconCoord(3, 0);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bWoodHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.planks, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bWoodHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.pWoodHoe.setIconCoord(4, 0);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.pWoodHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        mod_InfiTools.nWoodHoe.setIconCoord(5, 0);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.nWoodHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiTools.sWoodHoe.setIconCoord(6, 0);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.sWoodHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiTools.cWoodHoe.setIconCoord(7, 0);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.cWoodHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiTools.fWoodHoe.setIconCoord(8, 0);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.fWoodHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiTools.brWoodHoe.setIconCoord(9, 0);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.brWoodHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        ModLoader.addRecipe(new ItemStack(Item.hoeStone, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.stone, Character.valueOf('|'), Item.stick
                });
        mod_InfiTools.stStoneHoe.setIconCoord(1, 1);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.stStoneHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.stStoneHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiTools.saStoneHoe.setIconCoord(2, 1);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.saStoneHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.saStoneHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_InfiTools.bStoneHoe.setIconCoord(3, 1);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bStoneHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bStoneHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.stone, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bStoneHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bStoneHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.pStoneHoe.setIconCoord(4, 1);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.pStoneHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.pStoneHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        mod_InfiTools.mStoneHoe.setIconCoord(5, 1);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.mStoneHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.mStoneHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_InfiTools.nStoneHoe.setIconCoord(6, 1);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.nStoneHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.nStoneHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiTools.iceStoneHoe.setIconCoord(7, 1);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iceStoneHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iceStoneHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_InfiTools.sStoneHoe.setIconCoord(8, 1);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.sStoneHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.sStoneHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiTools.cStoneHoe.setIconCoord(9, 1);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.cStoneHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.cStoneHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiTools.fStoneHoe.setIconCoord(1, 1);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.fStoneHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.fStoneHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiTools.brStoneHoe.setIconCoord(1, 1);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.brStoneHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.brStoneHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_InfiTools.stIronHoe.setIconCoord(1, 2);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.stIronHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiTools.iIronHoe.setIconCoord(2, 2);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iIronHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_InfiTools.dIronHoe.setIconCoord(3, 2);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.dIronHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_InfiTools.gIronHoe.setIconCoord(4, 2);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.gIronHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.goldRod
                });
        mod_InfiTools.rIronHoe.setIconCoord(5, 2);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.rIronHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiTools.oIronHoe.setIconCoord(6, 2);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.oIronHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_InfiTools.bIronHoe.setIconCoord(7, 2);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bIronHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bIronHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.nIronHoe.setIconCoord(8, 2);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.nIronHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiTools.glIronHoe.setIconCoord(9, 2);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.glIronHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiTools.iceIronHoe.setIconCoord(10, 2);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iceIronHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_InfiTools.sIronHoe.setIconCoord(11, 2);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.sIronHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiTools.blIronHoe.setIconCoord(12, 2);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.blIronHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), Item.blazeRod
                });
        mod_InfiTools.stDiamondHoe.setIconCoord(1, 3);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.stDiamondHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiTools.iDiamondHoe.setIconCoord(2, 3);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iDiamondHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_InfiTools.dDiamondHoe.setIconCoord(3, 3);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.dDiamondHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_InfiTools.gDiamondHoe.setIconCoord(4, 3);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.gDiamondHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.goldRod
                });
        mod_InfiTools.rDiamondHoe.setIconCoord(5, 3);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.rDiamondHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiTools.oDiamondHoe.setIconCoord(6, 3);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.oDiamondHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_InfiTools.bDiamondHoe.setIconCoord(7, 3);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bDiamondHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bDiamondHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.mDiamondHoe.setIconCoord(8, 3);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.mDiamondHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_InfiTools.nDiamondHoe.setIconCoord(9, 3);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.nDiamondHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiTools.glDiamondHoe.setIconCoord(10, 3);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.glDiamondHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiTools.blDiamondHoe.setIconCoord(11, 3);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.blDiamondHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), Item.blazeRod
                });
        mod_InfiTools.stGoldHoe.setIconCoord(1, 4);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.stGoldHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiTools.gGoldHoe.setIconCoord(2, 4);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.gGoldHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.goldRod
                });
        mod_InfiTools.oGoldHoe.setIconCoord(3, 4);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.oGoldHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_InfiTools.saGoldHoe.setIconCoord(4, 4);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.saGoldHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_InfiTools.bGoldHoe.setIconCoord(5, 4);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bGoldHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bGoldHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.mGoldHoe.setIconCoord(6, 4);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.mGoldHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_InfiTools.nGoldHoe.setIconCoord(7, 4);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.nGoldHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiTools.glGoldHoe.setIconCoord(8, 4);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.glGoldHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiTools.iceGoldHoe.setIconCoord(9, 4);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iceGoldHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_InfiTools.sGoldHoe.setIconCoord(10, 4);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.sGoldHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiTools.fGoldHoe.setIconCoord(11, 4);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.fGoldHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiTools.wRedstoneHoe.setIconCoord(0, 5);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.wRedstoneHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), Item.stick
                });
        mod_InfiTools.stRedstoneHoe.setIconCoord(1, 5);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.stRedstoneHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiTools.iRedstoneHoe.setIconCoord(2, 5);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iRedstoneHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_InfiTools.dRedstoneHoe.setIconCoord(3, 5);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.dRedstoneHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_InfiTools.rRedstoneHoe.setIconCoord(4, 5);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.rRedstoneHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiTools.oRedstoneHoe.setIconCoord(5, 5);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.oRedstoneHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_InfiTools.bRedstoneHoe.setIconCoord(6, 5);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bRedstoneHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bRedstoneHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.mRedstoneHoe.setIconCoord(7, 5);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.mRedstoneHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_InfiTools.glRedstoneHoe.setIconCoord(8, 5);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.glRedstoneHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiTools.sRedstoneHoe.setIconCoord(9, 5);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.sRedstoneHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiTools.blRedstoneHoe.setIconCoord(10, 5);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.blRedstoneHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), Item.blazeRod
                });
        mod_InfiTools.wObsidianHoe.setIconCoord(0, 6);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.wObsidianHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), Item.stick
                });
        mod_InfiTools.stObsidianHoe.setIconCoord(1, 6);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.stObsidianHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiTools.iObsidianHoe.setIconCoord(2, 6);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iObsidianHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_InfiTools.dObsidianHoe.setIconCoord(3, 6);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.dObsidianHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_InfiTools.gObsidianHoe.setIconCoord(4, 6);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.gObsidianHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.goldRod
                });
        mod_InfiTools.rObsidianHoe.setIconCoord(5, 6);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.rObsidianHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiTools.oObsidianHoe.setIconCoord(6, 6);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.oObsidianHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_InfiTools.bObsidianHoe.setIconCoord(7, 6);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bObsidianHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bObsidianHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.nObsidianHoe.setIconCoord(8, 6);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.nObsidianHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiTools.glObsidianHoe.setIconCoord(9, 6);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.glObsidianHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiTools.sObsidianHoe.setIconCoord(10, 6);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.sObsidianHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiTools.fObsidianHoe.setIconCoord(11, 6);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.fObsidianHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiTools.blObsidianHoe.setIconCoord(12, 6);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.blObsidianHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), Item.blazeRod
                });
        mod_InfiTools.wSandstoneHoe.setIconCoord(0, 7);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.wSandstoneHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), Item.stick
                });
        mod_InfiTools.stSandstoneHoe.setIconCoord(1, 7);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.stSandstoneHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiTools.saSandstoneHoe.setIconCoord(2, 7);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.saSandstoneHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_InfiTools.bSandstoneHoe.setIconCoord(3, 7);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bSandstoneHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bSandstoneHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.pSandstoneHoe.setIconCoord(4, 7);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.pSandstoneHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        mod_InfiTools.nSandstoneHoe.setIconCoord(5, 7);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.nSandstoneHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiTools.iceSandstoneHoe.setIconCoord(6, 7);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iceSandstoneHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_InfiTools.sSandstoneHoe.setIconCoord(7, 7);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.sSandstoneHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiTools.cSandstoneHoe.setIconCoord(8, 7);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.cSandstoneHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiTools.fSandstoneHoe.setIconCoord(9, 7);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.fSandstoneHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiTools.brSandstoneHoe.setIconCoord(10, 7);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.brSandstoneHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_InfiTools.wBoneHoe.setIconCoord(0, 8);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.wBoneHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Item.bone, Character.valueOf('|'), Item.stick
                });
        mod_InfiTools.stBoneHoe.setIconCoord(1, 8);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.stBoneHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiTools.iBoneHoe.setIconCoord(2, 8);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iBoneHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_InfiTools.dBoneHoe.setIconCoord(3, 8);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.dBoneHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_InfiTools.rBoneHoe.setIconCoord(4, 8);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.rBoneHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiTools.oBoneHoe.setIconCoord(5, 8);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.oBoneHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_InfiTools.bBoneHoe.setIconCoord(6, 8);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bBoneHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Item.bone, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bBoneHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.mBoneHoe.setIconCoord(7, 8);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.mBoneHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_InfiTools.nBoneHoe.setIconCoord(8, 8);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.nBoneHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiTools.glBoneHoe.setIconCoord(9, 8);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.glBoneHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiTools.sBoneHoe.setIconCoord(10, 8);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.sBoneHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiTools.cBoneHoe.setIconCoord(11, 8);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.cBoneHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiTools.fBoneHoe.setIconCoord(12, 8);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.fBoneHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiTools.brBoneHoe.setIconCoord(13, 8);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.brBoneHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_InfiTools.blBoneHoe.setIconCoord(14, 8);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.blBoneHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Item.bone, Character.valueOf('|'), Item.blazeRod
                });
        mod_InfiTools.wPaperHoe.setIconCoord(0, 9);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.wPaperHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), Item.stick
                });
        mod_InfiTools.saPaperHoe.setIconCoord(1, 9);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.saPaperHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_InfiTools.bPaperHoe.setIconCoord(2, 9);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bPaperHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bPaperHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.pPaperHoe.setIconCoord(3, 9);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.pPaperHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        mod_InfiTools.sPaperHoe.setIconCoord(4, 9);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.sPaperHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiTools.cPaperHoe.setIconCoord(5, 9);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.cPaperHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiTools.brPaperHoe.setIconCoord(6, 9);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.brPaperHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_InfiTools.stMossyHoe.setIconCoord(0, 10);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.stMossyHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiTools.dMossyHoe.setIconCoord(1, 10);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.dMossyHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_InfiTools.rMossyHoe.setIconCoord(2, 10);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.rMossyHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiTools.bMossyHoe.setIconCoord(3, 10);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bMossyHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bMossyHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.mMossyHoe.setIconCoord(4, 10);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.mMossyHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_InfiTools.glMossyHoe.setIconCoord(5, 10);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.glMossyHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiTools.wNetherrackHoe.setIconCoord(0, 11);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.wNetherrackHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), Item.stick
                });
        mod_InfiTools.stNetherrackHoe.setIconCoord(1, 11);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.stNetherrackHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiTools.iNetherrackHoe.setIconCoord(2, 11);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iNetherrackHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_InfiTools.rNetherrackHoe.setIconCoord(3, 11);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.rNetherrackHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiTools.oNetherrackHoe.setIconCoord(4, 11);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.oNetherrackHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_InfiTools.saNetherrackHoe.setIconCoord(5, 11);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.saNetherrackHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_InfiTools.bNetherrackHoe.setIconCoord(6, 11);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bNetherrackHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bNetherrackHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.mNetherrackHoe.setIconCoord(7, 11);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.mNetherrackHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_InfiTools.nNetherrackHoe.setIconCoord(8, 11);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.nNetherrackHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiTools.glNetherrackHoe.setIconCoord(9, 11);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.glNetherrackHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiTools.iceNetherrackHoe.setIconCoord(10, 11);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iceNetherrackHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_InfiTools.sNetherrackHoe.setIconCoord(11, 11);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.sNetherrackHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiTools.cNetherrackHoe.setIconCoord(12, 11);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.cNetherrackHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiTools.fNetherrackHoe.setIconCoord(13, 11);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.fNetherrackHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiTools.brNetherrackHoe.setIconCoord(14, 11);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.brNetherrackHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiTools.blNetherrackHoe.setIconCoord(15, 11);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.blNetherrackHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiTools.wGlowstoneHoe.setIconCoord(0, 12);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.wGlowstoneHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), Item.stick
                });
        mod_InfiTools.stGlowstoneHoe.setIconCoord(1, 12);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.stGlowstoneHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiTools.iGlowstoneHoe.setIconCoord(2, 12);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iGlowstoneHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_InfiTools.dGlowstoneHoe.setIconCoord(3, 12);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.dGlowstoneHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_InfiTools.rGlowstoneHoe.setIconCoord(4, 12);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.rGlowstoneHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiTools.oGlowstoneHoe.setIconCoord(5, 12);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.oGlowstoneHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_InfiTools.bGlowstoneHoe.setIconCoord(6, 12);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bGlowstoneHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bGlowstoneHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.mGlowstoneHoe.setIconCoord(7, 12);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.mGlowstoneHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_InfiTools.nGlowstoneHoe.setIconCoord(8, 12);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.nGlowstoneHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiTools.glGlowstoneHoe.setIconCoord(9, 12);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.glGlowstoneHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiTools.iceGlowstoneHoe.setIconCoord(10, 12);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iceGlowstoneHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_InfiTools.lGlowstoneHoe.setIconCoord(11, 12);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.lGlowstoneHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiTools.sGlowstoneHoe.setIconCoord(12, 12);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.sGlowstoneHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiTools.blGlowstoneHoe.setIconCoord(13, 12);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.blGlowstoneHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), Item.blazeRod
                });
        mod_InfiTools.wIceHoe.setIconCoord(0, 13);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.wIceHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.ice, Character.valueOf('|'), Item.stick
                });
        mod_InfiTools.stIceHoe.setIconCoord(1, 13);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.stIceHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiTools.iIceHoe.setIconCoord(2, 13);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iIceHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_InfiTools.dIceHoe.setIconCoord(3, 13);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.dIceHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_InfiTools.gIceHoe.setIconCoord(4, 13);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.gIceHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.goldRod
                });
        mod_InfiTools.rIceHoe.setIconCoord(5, 13);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.rIceHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiTools.oIceHoe.setIconCoord(6, 13);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.oIceHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_InfiTools.saIceHoe.setIconCoord(7, 13);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.saIceHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_InfiTools.bIceHoe.setIconCoord(8, 13);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bIceHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.ice, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bIceHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.glIceHoe.setIconCoord(9, 13);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.glIceHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiTools.iceIceHoe.setIconCoord(10, 13);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iceIceHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_InfiTools.sIceHoe.setIconCoord(11, 13);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.sIceHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiTools.cIceHoe.setIconCoord(12, 13);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.cIceHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiTools.fIceHoe.setIconCoord(13, 13);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.fIceHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiTools.brIceHoe.setIconCoord(14, 13);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.brIceHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_InfiTools.dLavaHoe.setIconCoord(0, 14);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.dLavaHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_InfiTools.rLavaHoe.setIconCoord(1, 14);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.rLavaHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiTools.bLavaHoe.setIconCoord(2, 14);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bLavaHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bLavaHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.nLavaHoe.setIconCoord(3, 14);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.nLavaHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiTools.glLavaHoe.setIconCoord(4, 14);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.glLavaHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiTools.lLavaHoe.setIconCoord(5, 14);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.lLavaHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), mod_InfiTools.lavaRod
                });
        mod_InfiTools.blLavaHoe.setIconCoord(6, 14);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.blLavaHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), Item.blazeRod
                });
        mod_InfiTools.wSlimeHoe.setIconCoord(0, 15);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.wSlimeHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), Item.stick
                });
        mod_InfiTools.stSlimeHoe.setIconCoord(1, 15);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.stSlimeHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiTools.iSlimeHoe.setIconCoord(2, 15);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iSlimeHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_InfiTools.dSlimeHoe.setIconCoord(3, 15);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.dSlimeHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_InfiTools.gSlimeHoe.setIconCoord(4, 15);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.gSlimeHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.goldRod
                });
        mod_InfiTools.rSlimeHoe.setIconCoord(5, 15);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.rSlimeHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiTools.oSlimeHoe.setIconCoord(6, 15);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.oSlimeHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_InfiTools.saSlimeHoe.setIconCoord(7, 15);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.saSlimeHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_InfiTools.bSlimeHoe.setIconCoord(8, 15);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bSlimeHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bSlimeHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.pSlimeHoe.setIconCoord(9, 15);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.pSlimeHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        mod_InfiTools.mSlimeHoe.setIconCoord(10, 15);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.mSlimeHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_InfiTools.nSlimeHoe.setIconCoord(11, 15);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.nSlimeHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiTools.glSlimeHoe.setIconCoord(12, 15);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.glSlimeHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiTools.iceSlimeHoe.setIconCoord(13, 15);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iceSlimeHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_InfiTools.lSlimeHoe.setIconCoord(14, 15);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.lSlimeHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.lavaRod
                });
        mod_InfiTools.sSlimeHoe.setIconCoord(15, 15);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.sSlimeHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiTools.cSlimeHoe.setIconCoord(12, 14);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.cSlimeHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiTools.fSlimeHoe.setIconCoord(13, 14);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.fSlimeHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiTools.brSlimeHoe.setIconCoord(14, 14);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.brSlimeHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_InfiTools.blSlimeHoe.setIconCoord(15, 14);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.blSlimeHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), Item.blazeRod
                });
        mod_InfiTools.wCactusHoe.setIconCoord(10, 0);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.wCactusHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), Item.stick
                });
        mod_InfiTools.stCactusHoe.setIconCoord(11, 0);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.stCactusHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiTools.saCactusHoe.setIconCoord(12, 0);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.saCactusHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_InfiTools.bCactusHoe.setIconCoord(13, 0);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bCactusHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bCactusHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.pCactusHoe.setIconCoord(14, 0);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.pCactusHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        mod_InfiTools.nCactusHoe.setIconCoord(15, 0);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.nCactusHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiTools.sCactusHoe.setIconCoord(12, 1);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.sCactusHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiTools.cCactusHoe.setIconCoord(13, 1);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.cCactusHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiTools.fCactusHoe.setIconCoord(14, 1);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.fCactusHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiTools.brCactusHoe.setIconCoord(15, 1);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.brCactusHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_InfiTools.wFlintHoe.setIconCoord(13, 2);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.wFlintHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Item.flint, Character.valueOf('|'), Item.stick
                });
        mod_InfiTools.stFlintHoe.setIconCoord(14, 2);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.stFlintHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiTools.iFlintHoe.setIconCoord(15, 2);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iFlintHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_InfiTools.gFlintHoe.setIconCoord(12, 3);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.gFlintHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.goldRod
                });
        mod_InfiTools.oFlintHoe.setIconCoord(13, 3);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.oFlintHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_InfiTools.saFlintHoe.setIconCoord(14, 3);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.saFlintHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_InfiTools.bFlintHoe.setIconCoord(15, 3);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bFlintHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Item.flint, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bFlintHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.nFlintHoe.setIconCoord(12, 4);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.nFlintHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiTools.iceFlintHoe.setIconCoord(13, 4);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iceFlintHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_InfiTools.sFlintHoe.setIconCoord(14, 4);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.sFlintHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiTools.cFlintHoe.setIconCoord(15, 4);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.cFlintHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiTools.fFlintHoe.setIconCoord(11, 5);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.fFlintHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiTools.brFlintHoe.setIconCoord(12, 5);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.brFlintHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_InfiTools.blFlintHoe.setIconCoord(13, 5);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.blFlintHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Item.flint, Character.valueOf('|'), Item.blazeRod
                });
        mod_InfiTools.wBrickHoe.setIconCoord(14, 5);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.wBrickHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Item.brick, Character.valueOf('|'), Item.stick
                });
        mod_InfiTools.stBrickHoe.setIconCoord(15, 5);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.stBrickHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiTools.saBrickHoe.setIconCoord(13, 6);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.saBrickHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_InfiTools.bBrickHoe.setIconCoord(14, 6);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bBrickHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Item.brick, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bBrickHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.pBrickHoe.setIconCoord(15, 6);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.pBrickHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        mod_InfiTools.nBrickHoe.setIconCoord(11, 7);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.nBrickHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiTools.iceBrickHoe.setIconCoord(12, 7);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iceBrickHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_InfiTools.sBrickHoe.setIconCoord(13, 7);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.sBrickHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiTools.cBrickHoe.setIconCoord(14, 7);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.cBrickHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiTools.fBrickHoe.setIconCoord(15, 7);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.fBrickHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiTools.brBrickHoe.setIconCoord(15, 8);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.brBrickHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_InfiTools.dBlazeHoe.setIconCoord(7, 9);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.dBlazeHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_InfiTools.rBlazeHoe.setIconCoord(8, 9);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.rBlazeHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiTools.bBlazeHoe.setIconCoord(9, 9);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bBlazeHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bBlazeHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.nBlazeHoe.setIconCoord(10, 9);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.nBlazeHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiTools.glBlazeHoe.setIconCoord(11, 9);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.glBlazeHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiTools.lBlazeHoe.setIconCoord(12, 9);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.lBlazeHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), mod_InfiTools.lavaRod
                });
        mod_InfiTools.fBlazeHoe.setIconCoord(13, 9);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.fBlazeHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiTools.blBlazeHoe.setIconCoord(14, 9);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.blBlazeHoe, 1), new Object[]
                {
                    "mm", " |", " |", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), Item.blazeRod
                });
        return 0;
    }
}
