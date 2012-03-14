package net.minecraft.src.core;

import net.minecraft.src.*;

public class InfiRecipeAxes
{
    public InfiRecipeAxes()
    {
    }

    public static int recipeStorm()
    {
        mod_InfiTools.stWoodAxe.setIconCoord(1, 0);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.stWoodAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiTools.saWoodAxe.setIconCoord(2, 0);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.saWoodAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_InfiTools.bWoodAxe.setIconCoord(3, 0);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bWoodAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.planks, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bWoodAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.pWoodAxe.setIconCoord(4, 0);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.pWoodAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        mod_InfiTools.nWoodAxe.setIconCoord(5, 0);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.nWoodAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiTools.sWoodAxe.setIconCoord(6, 0);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.sWoodAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiTools.cWoodAxe.setIconCoord(7, 0);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.cWoodAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiTools.fWoodAxe.setIconCoord(8, 0);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.fWoodAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiTools.brWoodAxe.setIconCoord(9, 0);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.brWoodAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.planks, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        ModLoader.addRecipe(new ItemStack(Item.axeStone, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.stone, Character.valueOf('|'), Item.stick
                });
        mod_InfiTools.stStoneAxe.setIconCoord(1, 1);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.stStoneAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.stStoneAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiTools.saStoneAxe.setIconCoord(2, 1);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.saStoneAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.saStoneAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_InfiTools.bStoneAxe.setIconCoord(3, 1);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bStoneAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bStoneAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.stone, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bStoneAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bStoneAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.pStoneAxe.setIconCoord(4, 1);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.pStoneAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.pStoneAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        mod_InfiTools.mStoneAxe.setIconCoord(5, 1);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.mStoneAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.mStoneAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_InfiTools.nStoneAxe.setIconCoord(6, 1);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.nStoneAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.nStoneAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiTools.iceStoneAxe.setIconCoord(7, 1);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iceStoneAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iceStoneAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_InfiTools.sStoneAxe.setIconCoord(8, 1);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.sStoneAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.sStoneAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiTools.cStoneAxe.setIconCoord(9, 1);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.cStoneAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.cStoneAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiTools.fStoneAxe.setIconCoord(1, 1);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.fStoneAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.fStoneAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiTools.brStoneAxe.setIconCoord(1, 1);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.brStoneAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.cobblestone, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.brStoneAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.stone, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_InfiTools.stIronAxe.setIconCoord(1, 2);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.stIronAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiTools.iIronAxe.setIconCoord(2, 2);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iIronAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_InfiTools.dIronAxe.setIconCoord(3, 2);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.dIronAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_InfiTools.gIronAxe.setIconCoord(4, 2);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.gIronAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.goldRod
                });
        mod_InfiTools.rIronAxe.setIconCoord(5, 2);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.rIronAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiTools.oIronAxe.setIconCoord(6, 2);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.oIronAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_InfiTools.bIronAxe.setIconCoord(7, 2);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bIronAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bIronAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.nIronAxe.setIconCoord(8, 2);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.nIronAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiTools.glIronAxe.setIconCoord(9, 2);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.glIronAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiTools.iceIronAxe.setIconCoord(10, 2);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iceIronAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_InfiTools.sIronAxe.setIconCoord(11, 2);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.sIronAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiTools.blIronAxe.setIconCoord(12, 2);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.blIronAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Item.ingotIron, Character.valueOf('|'), Item.blazeRod
                });
        mod_InfiTools.stDiamondAxe.setIconCoord(1, 3);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.stDiamondAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiTools.iDiamondAxe.setIconCoord(2, 3);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iDiamondAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_InfiTools.dDiamondAxe.setIconCoord(3, 3);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.dDiamondAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_InfiTools.gDiamondAxe.setIconCoord(4, 3);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.gDiamondAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.goldRod
                });
        mod_InfiTools.rDiamondAxe.setIconCoord(5, 3);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.rDiamondAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiTools.oDiamondAxe.setIconCoord(6, 3);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.oDiamondAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_InfiTools.bDiamondAxe.setIconCoord(7, 3);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bDiamondAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bDiamondAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.mDiamondAxe.setIconCoord(8, 3);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.mDiamondAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_InfiTools.nDiamondAxe.setIconCoord(9, 3);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.nDiamondAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiTools.glDiamondAxe.setIconCoord(10, 3);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.glDiamondAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiTools.blDiamondAxe.setIconCoord(11, 3);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.blDiamondAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Item.diamond, Character.valueOf('|'), Item.blazeRod
                });
        mod_InfiTools.stGoldAxe.setIconCoord(1, 4);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.stGoldAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiTools.gGoldAxe.setIconCoord(2, 4);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.gGoldAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.goldRod
                });
        mod_InfiTools.oGoldAxe.setIconCoord(3, 4);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.oGoldAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_InfiTools.saGoldAxe.setIconCoord(4, 4);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.saGoldAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_InfiTools.bGoldAxe.setIconCoord(5, 4);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bGoldAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bGoldAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.mGoldAxe.setIconCoord(6, 4);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.mGoldAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_InfiTools.nGoldAxe.setIconCoord(7, 4);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.nGoldAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiTools.glGoldAxe.setIconCoord(8, 4);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.glGoldAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiTools.iceGoldAxe.setIconCoord(9, 4);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iceGoldAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_InfiTools.sGoldAxe.setIconCoord(10, 4);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.sGoldAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiTools.fGoldAxe.setIconCoord(11, 4);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.fGoldAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Item.ingotGold, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiTools.wRedstoneAxe.setIconCoord(0, 5);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.wRedstoneAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), Item.stick
                });
        mod_InfiTools.stRedstoneAxe.setIconCoord(1, 5);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.stRedstoneAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiTools.iRedstoneAxe.setIconCoord(2, 5);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iRedstoneAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_InfiTools.dRedstoneAxe.setIconCoord(3, 5);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.dRedstoneAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_InfiTools.rRedstoneAxe.setIconCoord(4, 5);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.rRedstoneAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiTools.oRedstoneAxe.setIconCoord(5, 5);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.oRedstoneAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_InfiTools.bRedstoneAxe.setIconCoord(6, 5);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bRedstoneAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bRedstoneAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.mRedstoneAxe.setIconCoord(7, 5);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.mRedstoneAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_InfiTools.glRedstoneAxe.setIconCoord(8, 5);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.glRedstoneAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiTools.sRedstoneAxe.setIconCoord(9, 5);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.sRedstoneAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiTools.blRedstoneAxe.setIconCoord(10, 5);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.blRedstoneAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), mod_InfiTools.redstoneCrystal, Character.valueOf('|'), Item.blazeRod
                });
        mod_InfiTools.wObsidianAxe.setIconCoord(0, 6);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.wObsidianAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), Item.stick
                });
        mod_InfiTools.stObsidianAxe.setIconCoord(1, 6);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.stObsidianAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiTools.iObsidianAxe.setIconCoord(2, 6);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iObsidianAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_InfiTools.dObsidianAxe.setIconCoord(3, 6);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.dObsidianAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_InfiTools.gObsidianAxe.setIconCoord(4, 6);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.gObsidianAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.goldRod
                });
        mod_InfiTools.rObsidianAxe.setIconCoord(5, 6);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.rObsidianAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiTools.oObsidianAxe.setIconCoord(6, 6);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.oObsidianAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_InfiTools.bObsidianAxe.setIconCoord(7, 6);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bObsidianAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bObsidianAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.nObsidianAxe.setIconCoord(8, 6);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.nObsidianAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiTools.glObsidianAxe.setIconCoord(9, 6);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.glObsidianAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiTools.sObsidianAxe.setIconCoord(10, 6);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.sObsidianAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiTools.fObsidianAxe.setIconCoord(11, 6);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.fObsidianAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiTools.blObsidianAxe.setIconCoord(12, 6);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.blObsidianAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.obsidian, Character.valueOf('|'), Item.blazeRod
                });
        mod_InfiTools.wSandstoneAxe.setIconCoord(0, 7);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.wSandstoneAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), Item.stick
                });
        mod_InfiTools.stSandstoneAxe.setIconCoord(1, 7);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.stSandstoneAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiTools.saSandstoneAxe.setIconCoord(2, 7);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.saSandstoneAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_InfiTools.bSandstoneAxe.setIconCoord(3, 7);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bSandstoneAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bSandstoneAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.pSandstoneAxe.setIconCoord(4, 7);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.pSandstoneAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        mod_InfiTools.nSandstoneAxe.setIconCoord(5, 7);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.nSandstoneAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiTools.iceSandstoneAxe.setIconCoord(6, 7);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iceSandstoneAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_InfiTools.sSandstoneAxe.setIconCoord(7, 7);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.sSandstoneAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiTools.cSandstoneAxe.setIconCoord(8, 7);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.cSandstoneAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiTools.fSandstoneAxe.setIconCoord(9, 7);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.fSandstoneAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiTools.brSandstoneAxe.setIconCoord(10, 7);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.brSandstoneAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.sandStone, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_InfiTools.wBoneAxe.setIconCoord(0, 8);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.wBoneAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Item.bone, Character.valueOf('|'), Item.stick
                });
        mod_InfiTools.stBoneAxe.setIconCoord(1, 8);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.stBoneAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiTools.iBoneAxe.setIconCoord(2, 8);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iBoneAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_InfiTools.dBoneAxe.setIconCoord(3, 8);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.dBoneAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_InfiTools.rBoneAxe.setIconCoord(4, 8);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.rBoneAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiTools.oBoneAxe.setIconCoord(5, 8);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.oBoneAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_InfiTools.bBoneAxe.setIconCoord(6, 8);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bBoneAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Item.bone, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bBoneAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.mBoneAxe.setIconCoord(7, 8);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.mBoneAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_InfiTools.nBoneAxe.setIconCoord(8, 8);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.nBoneAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiTools.glBoneAxe.setIconCoord(9, 8);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.glBoneAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiTools.sBoneAxe.setIconCoord(10, 8);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.sBoneAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiTools.cBoneAxe.setIconCoord(11, 8);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.cBoneAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiTools.fBoneAxe.setIconCoord(12, 8);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.fBoneAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiTools.brBoneAxe.setIconCoord(13, 8);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.brBoneAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Item.bone, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_InfiTools.blBoneAxe.setIconCoord(14, 8);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.blBoneAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Item.bone, Character.valueOf('|'), Item.blazeRod
                });
        mod_InfiTools.wPaperAxe.setIconCoord(0, 9);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.wPaperAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), Item.stick
                });
        mod_InfiTools.saPaperAxe.setIconCoord(1, 9);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.saPaperAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_InfiTools.bPaperAxe.setIconCoord(2, 9);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bPaperAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bPaperAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.pPaperAxe.setIconCoord(3, 9);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.pPaperAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        mod_InfiTools.sPaperAxe.setIconCoord(4, 9);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.sPaperAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiTools.cPaperAxe.setIconCoord(5, 9);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.cPaperAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiTools.brPaperAxe.setIconCoord(6, 9);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.brPaperAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), mod_InfiTools.paperStack, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_InfiTools.stMossyAxe.setIconCoord(0, 10);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.stMossyAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiTools.dMossyAxe.setIconCoord(1, 10);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.dMossyAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_InfiTools.rMossyAxe.setIconCoord(2, 10);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.rMossyAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiTools.bMossyAxe.setIconCoord(3, 10);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bMossyAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bMossyAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.mMossyAxe.setIconCoord(4, 10);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.mMossyAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_InfiTools.glMossyAxe.setIconCoord(5, 10);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.glMossyAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), mod_InfiTools.mossBallCrafted, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiTools.wNetherrackAxe.setIconCoord(0, 11);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.wNetherrackAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), Item.stick
                });
        mod_InfiTools.stNetherrackAxe.setIconCoord(1, 11);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.stNetherrackAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiTools.iNetherrackAxe.setIconCoord(2, 11);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iNetherrackAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_InfiTools.rNetherrackAxe.setIconCoord(3, 11);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.rNetherrackAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiTools.oNetherrackAxe.setIconCoord(4, 11);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.oNetherrackAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_InfiTools.saNetherrackAxe.setIconCoord(5, 11);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.saNetherrackAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_InfiTools.bNetherrackAxe.setIconCoord(6, 11);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bNetherrackAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bNetherrackAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.mNetherrackAxe.setIconCoord(7, 11);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.mNetherrackAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_InfiTools.nNetherrackAxe.setIconCoord(8, 11);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.nNetherrackAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiTools.glNetherrackAxe.setIconCoord(9, 11);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.glNetherrackAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiTools.iceNetherrackAxe.setIconCoord(10, 11);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iceNetherrackAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_InfiTools.sNetherrackAxe.setIconCoord(11, 11);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.sNetherrackAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiTools.cNetherrackAxe.setIconCoord(12, 11);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.cNetherrackAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiTools.fNetherrackAxe.setIconCoord(13, 11);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.fNetherrackAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiTools.brNetherrackAxe.setIconCoord(14, 11);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.brNetherrackAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiTools.blNetherrackAxe.setIconCoord(15, 11);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.blNetherrackAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.netherrack, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiTools.wGlowstoneAxe.setIconCoord(0, 12);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.wGlowstoneAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), Item.stick
                });
        mod_InfiTools.stGlowstoneAxe.setIconCoord(1, 12);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.stGlowstoneAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiTools.iGlowstoneAxe.setIconCoord(2, 12);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iGlowstoneAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_InfiTools.dGlowstoneAxe.setIconCoord(3, 12);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.dGlowstoneAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_InfiTools.rGlowstoneAxe.setIconCoord(4, 12);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.rGlowstoneAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiTools.oGlowstoneAxe.setIconCoord(5, 12);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.oGlowstoneAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_InfiTools.bGlowstoneAxe.setIconCoord(6, 12);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bGlowstoneAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bGlowstoneAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.mGlowstoneAxe.setIconCoord(7, 12);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.mGlowstoneAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_InfiTools.nGlowstoneAxe.setIconCoord(8, 12);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.nGlowstoneAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiTools.glGlowstoneAxe.setIconCoord(9, 12);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.glGlowstoneAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiTools.iceGlowstoneAxe.setIconCoord(10, 12);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iceGlowstoneAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_InfiTools.lGlowstoneAxe.setIconCoord(11, 12);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.lGlowstoneAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiTools.sGlowstoneAxe.setIconCoord(12, 12);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.sGlowstoneAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiTools.blGlowstoneAxe.setIconCoord(13, 12);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.blGlowstoneAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), mod_InfiTools.glowstoneCrystal, Character.valueOf('|'), Item.blazeRod
                });
        mod_InfiTools.wIceAxe.setIconCoord(0, 13);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.wIceAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.ice, Character.valueOf('|'), Item.stick
                });
        mod_InfiTools.stIceAxe.setIconCoord(1, 13);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.stIceAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiTools.iIceAxe.setIconCoord(2, 13);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iIceAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_InfiTools.dIceAxe.setIconCoord(3, 13);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.dIceAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_InfiTools.gIceAxe.setIconCoord(4, 13);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.gIceAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.goldRod
                });
        mod_InfiTools.rIceAxe.setIconCoord(5, 13);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.rIceAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiTools.oIceAxe.setIconCoord(6, 13);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.oIceAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiTools.saIceAxe.setIconCoord(7, 13);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.saIceAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_InfiTools.bIceAxe.setIconCoord(8, 13);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bIceAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.ice, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bIceAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.glIceAxe.setIconCoord(9, 13);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.glIceAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiTools.iceIceAxe.setIconCoord(10, 13);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iceIceAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_InfiTools.sIceAxe.setIconCoord(11, 13);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.sIceAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiTools.cIceAxe.setIconCoord(12, 13);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.cIceAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiTools.fIceAxe.setIconCoord(13, 13);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.fIceAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiTools.brIceAxe.setIconCoord(14, 13);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.brIceAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.ice, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_InfiTools.dLavaAxe.setIconCoord(0, 14);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.dLavaAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_InfiTools.rLavaAxe.setIconCoord(1, 14);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.rLavaAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiTools.bLavaAxe.setIconCoord(2, 14);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bLavaAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bLavaAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.nLavaAxe.setIconCoord(3, 14);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.nLavaAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiTools.glLavaAxe.setIconCoord(4, 14);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.glLavaAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiTools.lLavaAxe.setIconCoord(5, 14);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.lLavaAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), mod_InfiTools.lavaRod
                });
        mod_InfiTools.blLavaAxe.setIconCoord(6, 14);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.blLavaAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), mod_InfiTools.lavaCrystal, Character.valueOf('|'), Item.blazeRod
                });
        mod_InfiTools.wSlimeAxe.setIconCoord(0, 15);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.wSlimeAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), Item.stick
                });
        mod_InfiTools.stSlimeAxe.setIconCoord(1, 15);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.stSlimeAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiTools.iSlimeAxe.setIconCoord(2, 15);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iSlimeAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_InfiTools.dSlimeAxe.setIconCoord(3, 15);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.dSlimeAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_InfiTools.gSlimeAxe.setIconCoord(4, 15);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.gSlimeAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.goldRod
                });
        mod_InfiTools.rSlimeAxe.setIconCoord(5, 15);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.rSlimeAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiTools.oSlimeAxe.setIconCoord(6, 15);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.oSlimeAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_InfiTools.saSlimeAxe.setIconCoord(7, 15);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.saSlimeAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_InfiTools.bSlimeAxe.setIconCoord(8, 15);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bSlimeAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bSlimeAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.pSlimeAxe.setIconCoord(9, 15);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.pSlimeAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        mod_InfiTools.mSlimeAxe.setIconCoord(10, 15);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.mSlimeAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.mossyRod
                });
        mod_InfiTools.nSlimeAxe.setIconCoord(11, 15);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.nSlimeAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiTools.glSlimeAxe.setIconCoord(12, 15);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.glSlimeAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiTools.iceSlimeAxe.setIconCoord(13, 15);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iceSlimeAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_InfiTools.lSlimeAxe.setIconCoord(14, 15);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.lSlimeAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.lavaRod
                });
        mod_InfiTools.sSlimeAxe.setIconCoord(15, 15);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.sSlimeAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiTools.cSlimeAxe.setIconCoord(12, 14);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.cSlimeAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiTools.fSlimeAxe.setIconCoord(13, 14);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.fSlimeAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiTools.brSlimeAxe.setIconCoord(14, 14);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.brSlimeAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_InfiTools.blSlimeAxe.setIconCoord(15, 14);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.blSlimeAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), mod_InfiTools.slimeCrystal, Character.valueOf('|'), Item.blazeRod
                });
        mod_InfiTools.wCactusAxe.setIconCoord(10, 0);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.wCactusAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), Item.stick
                });
        mod_InfiTools.stCactusAxe.setIconCoord(11, 0);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.stCactusAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiTools.saCactusAxe.setIconCoord(12, 0);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.saCactusAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_InfiTools.bCactusAxe.setIconCoord(13, 0);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bCactusAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bCactusAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.pCactusAxe.setIconCoord(14, 0);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.pCactusAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        mod_InfiTools.nCactusAxe.setIconCoord(15, 0);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.nCactusAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiTools.sCactusAxe.setIconCoord(12, 1);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.sCactusAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiTools.cCactusAxe.setIconCoord(13, 1);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.cCactusAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiTools.fCactusAxe.setIconCoord(14, 1);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.fCactusAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiTools.brCactusAxe.setIconCoord(15, 1);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.brCactusAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Block.cactus, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_InfiTools.wFlintAxe.setIconCoord(13, 2);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.wFlintAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Item.flint, Character.valueOf('|'), Item.stick
                });
        mod_InfiTools.stFlintAxe.setIconCoord(14, 2);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.stFlintAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiTools.iFlintAxe.setIconCoord(15, 2);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iFlintAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.ironRod
                });
        mod_InfiTools.gFlintAxe.setIconCoord(12, 3);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.gFlintAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.goldRod
                });
        mod_InfiTools.oFlintAxe.setIconCoord(13, 3);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.oFlintAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.obsidianRod
                });
        mod_InfiTools.saFlintAxe.setIconCoord(14, 3);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.saFlintAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_InfiTools.bFlintAxe.setIconCoord(15, 3);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bFlintAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Item.flint, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bFlintAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.nFlintAxe.setIconCoord(12, 4);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.nFlintAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiTools.iceFlintAxe.setIconCoord(13, 4);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iceFlintAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_InfiTools.sFlintAxe.setIconCoord(14, 4);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.sFlintAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiTools.cFlintAxe.setIconCoord(15, 4);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.cFlintAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiTools.fFlintAxe.setIconCoord(11, 5);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.fFlintAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiTools.brFlintAxe.setIconCoord(12, 5);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.brFlintAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Item.flint, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_InfiTools.blFlintAxe.setIconCoord(13, 5);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.blFlintAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Item.flint, Character.valueOf('|'), Item.blazeRod
                });
        mod_InfiTools.wBrickAxe.setIconCoord(14, 5);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.wBrickAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Item.brick, Character.valueOf('|'), Item.stick
                });
        mod_InfiTools.stBrickAxe.setIconCoord(15, 5);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.stBrickAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.stoneRod
                });
        mod_InfiTools.saBrickAxe.setIconCoord(13, 6);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.saBrickAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.sandstoneRod
                });
        mod_InfiTools.bBrickAxe.setIconCoord(14, 6);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bBrickAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Item.brick, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bBrickAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.pBrickAxe.setIconCoord(15, 6);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.pBrickAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.paperRod
                });
        mod_InfiTools.nBrickAxe.setIconCoord(11, 7);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.nBrickAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiTools.iceBrickAxe.setIconCoord(12, 7);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.iceBrickAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.iceRod
                });
        mod_InfiTools.sBrickAxe.setIconCoord(13, 7);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.sBrickAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.slimeRod
                });
        mod_InfiTools.cBrickAxe.setIconCoord(14, 7);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.cBrickAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.cactusRod
                });
        mod_InfiTools.fBrickAxe.setIconCoord(15, 7);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.fBrickAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiTools.brBrickAxe.setIconCoord(15, 8);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.brBrickAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), Item.brick, Character.valueOf('|'), mod_InfiTools.brickRod
                });
        mod_InfiTools.dBlazeAxe.setIconCoord(7, 9);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.dBlazeAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), mod_InfiTools.diamondRod
                });
        mod_InfiTools.rBlazeAxe.setIconCoord(8, 9);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.rBlazeAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), mod_InfiTools.redstoneRod
                });
        mod_InfiTools.bBlazeAxe.setIconCoord(9, 9);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bBlazeAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), Item.bone
                });
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.bBlazeAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), mod_InfiTools.boneRod
                });
        mod_InfiTools.nBlazeAxe.setIconCoord(10, 9);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.nBlazeAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), mod_InfiTools.netherrackRod
                });
        mod_InfiTools.glBlazeAxe.setIconCoord(11, 9);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.glBlazeAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), mod_InfiTools.glowstoneRod
                });
        mod_InfiTools.lBlazeAxe.setIconCoord(12, 9);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.lBlazeAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), mod_InfiTools.lavaRod
                });
        mod_InfiTools.fBlazeAxe.setIconCoord(13, 9);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.fBlazeAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), mod_InfiTools.flintRod
                });
        mod_InfiTools.blBlazeAxe.setIconCoord(14, 9);
        
        ModLoader.addRecipe(new ItemStack(mod_InfiTools.blBlazeAxe, 1), new Object[]
                {
                    "mm", "m|", " |", Character.valueOf('m'), mod_InfiTools.blazeCrystal, Character.valueOf('|'), Item.blazeRod
                });
        return 0;
    }
}
