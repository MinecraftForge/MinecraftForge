package net.minecraft.src;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class ContainerCreative extends Container
{
    /** the list of items in this container */
    public List itemList = new ArrayList();

    public ContainerCreative(EntityPlayer par1EntityPlayer)
    {
        Block[] var2 = new Block[] {Block.cobblestone, Block.stone, Block.oreDiamond, Block.oreGold, Block.oreIron, Block.oreCoal, Block.oreLapis, Block.oreRedstone, Block.stoneBrick, Block.stoneBrick, Block.stoneBrick, Block.stoneBrick, Block.blockClay, Block.blockDiamond, Block.blockGold, Block.blockSteel, Block.bedrock, Block.blockLapis, Block.brick, Block.cobblestoneMossy, Block.stairSingle, Block.stairSingle, Block.stairSingle, Block.stairSingle, Block.stairSingle, Block.stairSingle, Block.obsidian, Block.netherrack, Block.slowSand, Block.glowStone, Block.wood, Block.wood, Block.wood, Block.wood, Block.leaves, Block.leaves, Block.leaves, Block.leaves, Block.dirt, Block.grass, Block.sand, Block.sandStone, Block.gravel, Block.web, Block.planks, Block.sapling, Block.sapling, Block.sapling, Block.sapling, Block.deadBush, Block.sponge, Block.ice, Block.blockSnow, Block.plantYellow, Block.plantRed, Block.mushroomBrown, Block.mushroomRed, Block.cactus, Block.melon, Block.pumpkin, Block.pumpkinLantern, Block.vine, Block.fenceIron, Block.thinGlass, Block.netherBrick, Block.netherFence, Block.stairsNetherBrick, Block.whiteStone, Block.mycelium, Block.waterlily, Block.tallGrass, Block.tallGrass, Block.chest, Block.workbench, Block.glass, Block.tnt, Block.bookShelf, Block.cloth, Block.cloth, Block.cloth, Block.cloth, Block.cloth, Block.cloth, Block.cloth, Block.cloth, Block.cloth, Block.cloth, Block.cloth, Block.cloth, Block.cloth, Block.cloth, Block.cloth, Block.cloth, Block.dispenser, Block.stoneOvenIdle, Block.music, Block.jukebox, Block.pistonStickyBase, Block.pistonBase, Block.fence, Block.fenceGate, Block.ladder, Block.rail, Block.railPowered, Block.railDetector, Block.torchWood, Block.stairCompactPlanks, Block.stairCompactCobblestone, Block.stairsBrick, Block.stairsStoneBrickSmooth, Block.lever, Block.pressurePlateStone, Block.pressurePlatePlanks, Block.torchRedstoneActive, Block.button, Block.trapdoor, Block.enchantmentTable, Block.field_48209_bL};
        int var3 = 0;
        int var4 = 0;
        int var5 = 0;
        int var6 = 0;
        int var7 = 0;
        int var8 = 0;
        int var9 = 1;
        int var10;
        int var11;

        for (var10 = 0; var10 < var2.length; ++var10)
        {
            var11 = 0;

            if (var2[var10] == Block.cloth)
            {
                var11 = var3++;
            }
            else if (var2[var10] == Block.stairSingle)
            {
                var11 = var4++;
            }
            else if (var2[var10] == Block.wood)
            {
                var11 = var5++;
            }
            else if (var2[var10] == Block.sapling)
            {
                var11 = var6++;
            }
            else if (var2[var10] == Block.stoneBrick)
            {
                var11 = var7++;
            }
            else if (var2[var10] == Block.tallGrass)
            {
                var11 = var9++;
            }
            else if (var2[var10] == Block.leaves)
            {
                var11 = var8++;
            }

            this.itemList.add(new ItemStack(var2[var10], 1, var11));
        }
        
        for (Block block : Block.blocksList)
        {
            if (block != null)
            {
                block.addCreativeItems((ArrayList)itemList);
            }
        }
        
        int x = 0;
        for (Item item : Item.itemsList)
        {
            if (x++ >= 256 && item != null)
            {
                item.addCreativeItems((ArrayList)itemList);
            }
        }

        for (var10 = 1; var10 < 16; ++var10)
        {
            this.itemList.add(new ItemStack(Item.dyePowder.shiftedIndex, 1, var10));
        }

        Iterator var14 = EntityList.entityEggs.keySet().iterator();

        while (var14.hasNext())
        {
            Integer var15 = (Integer)var14.next();
            this.itemList.add(new ItemStack(Item.monsterPlacer.shiftedIndex, 1, var15.intValue()));
        }

        InventoryPlayer var13 = par1EntityPlayer.inventory;

        for (var11 = 0; var11 < 9; ++var11)
        {
            for (int var12 = 0; var12 < 8; ++var12)
            {
                this.addSlot(new Slot(GuiContainerCreative.getInventory(), var12 + var11 * 8, 8 + var12 * 18, 18 + var11 * 18));
            }
        }

        for (var11 = 0; var11 < 9; ++var11)
        {
            this.addSlot(new Slot(var13, var11, 8 + var11 * 18, 184));
        }

        this.scrollTo(0.0F);
    }

    public boolean canInteractWith(EntityPlayer par1EntityPlayer)
    {
        return true;
    }

    /**
     * Updates the gui slots ItemStack's based on scroll position.
     */
    public void scrollTo(float par1)
    {
        int var2 = this.itemList.size() / 8 - 8 + 1;
        int var3 = (int)((double)(par1 * (float)var2) + 0.5D);

        if (var3 < 0)
        {
            var3 = 0;
        }

        for (int var4 = 0; var4 < 9; ++var4)
        {
            for (int var5 = 0; var5 < 8; ++var5)
            {
                int var6 = var5 + (var4 + var3) * 8;

                if (var6 >= 0 && var6 < this.itemList.size())
                {
                    GuiContainerCreative.getInventory().setInventorySlotContents(var5 + var4 * 8, (ItemStack)this.itemList.get(var6));
                }
                else
                {
                    GuiContainerCreative.getInventory().setInventorySlotContents(var5 + var4 * 8, (ItemStack)null);
                }
            }
        }
    }

    protected void retrySlotClick(int par1, int par2, boolean par3, EntityPlayer par4EntityPlayer) {}
}
