package net.minecraft.src.orizon;
import java.util.Random;

import net.minecraft.src.forge.*;
import net.minecraft.src.*;

public class LiquidBlockFlowing extends LiquidBlockBase
	implements ITextureProvider
{
    public LiquidBlockFlowing(int id, Material material)
    {
        super(id, material);
        enableStats = false;
        blockIndexInTexture = 232;
    }
    
    public boolean getEnableStats() {return false;}
    
    public String getTextureFile()
    {
        return "/oretex/slime.png";
    }

   /**
    * Indicates whether the flow direction is optimal. Each array index corresponds to one of the four cardinal
    * directions.
    */
   boolean[] isOptimalFlowDirection = new boolean[4];

   /**
    * The estimated cost to flow in a given direction from the current point. Each array index corresponds to one of
    * the four cardinal directions.
    */
   int[] flowCost = new int[4];

   /**
    * Updates the flow for the BlockFlowing object.
    */
   private void updateFlow(World par1World, int par2, int par3, int par4)
   {
       int var5 = par1World.getBlockMetadata(par2, par3, par4);
       par1World.setBlockAndMetadata(par2, par3, par4, this.blockID + 1, var5);
       par1World.markBlocksDirty(par2, par3, par4, par2, par3, par4);
       par1World.markBlockNeedsUpdate(par2, par3, par4);
   }

   public boolean func_48204_b(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
   {
       return this.blockMaterial != Material.lava;
   }

   /**
    * Ticks the block if it's been scheduled
    */
   public void updateTick(World world, int x, int y, int z, Random par5Random)
   {
       int var6 = this.getFlowDecay(world, x, y, z);
       byte var7 = 1;

       if (this.blockMaterial == Material.lava && !world.worldProvider.isHellWorld)
       {
           var7 = 2;
       }

       boolean var8 = true;
       int var10;

       if (var6 > 0)
       {
           byte var9 = -100;
           int var12 = this.getSmallestFlowDecay(world, x - 1, y, z, var9);
           var12 = this.getSmallestFlowDecay(world, x + 1, y, z, var12);
           var12 = this.getSmallestFlowDecay(world, x, y, z - 1, var12);
           var12 = this.getSmallestFlowDecay(world, x, y, z + 1, var12);
           var10 = var12 + var7;

           if (var10 >= 8 || var12 < 0)
           {
               var10 = -1;
           }

           if (this.getFlowDecay(world, x, y + 1, z) >= 0)
           {
               int var11 = this.getFlowDecay(world, x, y + 1, z);

               if (var11 >= 8)
               {
                   var10 = var11;
               }
               else
               {
                   var10 = var11 + 8;
               }
           }

           if (this.blockMaterial == Material.lava && var6 < 8 && var10 < 8 && var10 > var6 && par5Random.nextInt(4) != 0)
           {
               var10 = var6;
               var8 = false;
           }

           if (var10 != var6)
           {
               var6 = var10;

               if (var10 < 0)
               {
                   world.setBlockWithNotify(x, y, z, 0);
               }
               else
               {
                   world.setBlockMetadataWithNotify(x, y, z, var10);
                   world.scheduleBlockUpdate(x, y, z, this.blockID, this.tickRate());
                   world.notifyBlocksOfNeighborChange(x, y, z, this.blockID);
               }
           }
           else if (var8)
           {
               this.updateFlow(world, x, y, z);
           }
       }
       else
       {
           this.updateFlow(world, x, y, z);
       }

       if (this.liquidCanDisplaceBlock(world, x, y - 1, z))
       {
           if (this.blockMaterial == Material.lava && world.getBlockMaterial(x, y - 1, z) == Material.water)
           {
               world.setBlockWithNotify(x, y - 1, z, Block.stone.blockID);
               this.triggerLavaMixEffects(world, x, y - 1, z);
               return;
           }

           if (var6 >= 8)
           {
               world.setBlockAndMetadataWithNotify(x, y - 1, z, this.blockID, var6);
           }
           else
           {
               world.setBlockAndMetadataWithNotify(x, y - 1, z, this.blockID, var6 + 8);
           }
       }
       else if (var6 >= 0 && (var6 == 0 || this.blockBlocksFlow(world, x, y - 1, z)))
       {
           boolean[] var13 = this.getOptimalFlowDirections(world, x, y, z);
           var10 = var6 + var7;

           if (var6 >= 8)
           {
               var10 = 1;
           }

           if (var10 >= 8)
           {
               return;
           }

           if (var13[0])
           {
               this.flowIntoBlock(world, x - 1, y, z, var10);
           }

           if (var13[1])
           {
               this.flowIntoBlock(world, x + 1, y, z, var10);
           }

           if (var13[2])
           {
               this.flowIntoBlock(world, x, y, z - 1, var10);
           }

           if (var13[3])
           {
               this.flowIntoBlock(world, x, y, z + 1, var10);
           }
       }
   }

   /**
    * flowIntoBlock(World world, int x, int y, int z, int newFlowDecay) - Flows into the block at the coordinates and
    * changes the block type to the liquid.
    */
   private void flowIntoBlock(World par1World, int par2, int par3, int par4, int par5)
   {
       if (this.liquidCanDisplaceBlock(par1World, par2, par3, par4))
       {
           int var6 = par1World.getBlockId(par2, par3, par4);

           if (var6 > 0)
           {
               if (this.blockMaterial == Material.lava)
               {
                   this.triggerLavaMixEffects(par1World, par2, par3, par4);
               }
               else
               {
                   Block.blocksList[var6].dropBlockAsItem(par1World, par2, par3, par4, par1World.getBlockMetadata(par2, par3, par4), 0);
               }
           }

           par1World.setBlockAndMetadataWithNotify(par2, par3, par4, this.blockID, par5);
       }
   }

   /**
    * calculateFlowCost(World world, int x, int y, int z, int accumulatedCost, int previousDirectionOfFlow) - Used to
    * determine the path of least resistance, this method returns the lowest possible flow cost for the direction of
    * flow indicated. Each necessary horizontal flow adds to the flow cost.
    */
   private int calculateFlowCost(World par1World, int par2, int par3, int par4, int par5, int par6)
   {
       int var7 = 1000;

       for (int var8 = 0; var8 < 4; ++var8)
       {
           if ((var8 != 0 || par6 != 1) && (var8 != 1 || par6 != 0) && (var8 != 2 || par6 != 3) && (var8 != 3 || par6 != 2))
           {
               int var9 = par2;
               int var11 = par4;

               if (var8 == 0)
               {
                   var9 = par2 - 1;
               }

               if (var8 == 1)
               {
                   ++var9;
               }

               if (var8 == 2)
               {
                   var11 = par4 - 1;
               }

               if (var8 == 3)
               {
                   ++var11;
               }

               if (!this.blockBlocksFlow(par1World, var9, par3, var11) && (par1World.getBlockMaterial(var9, par3, var11) != this.blockMaterial || par1World.getBlockMetadata(var9, par3, var11) != 0))
               {
                   if (!this.blockBlocksFlow(par1World, var9, par3 - 1, var11))
                   {
                       return par5;
                   }

                   if (par5 < 4)
                   {
                       int var12 = this.calculateFlowCost(par1World, var9, par3, var11, par5 + 1, var8);

                       if (var12 < var7)
                       {
                           var7 = var12;
                       }
                   }
               }
           }
       }

       return var7;
   }

   /**
    * Returns a boolean array indicating which flow directions are optimal based on each direction's calculated flow
    * cost. Each array index corresponds to one of the four cardinal directions. A value of true indicates the
    * direction is optimal.
    */
   private boolean[] getOptimalFlowDirections(World par1World, int par2, int par3, int par4)
   {
       int var5;
       int var6;

       for (var5 = 0; var5 < 4; ++var5)
       {
           this.flowCost[var5] = 1000;
           var6 = par2;
           int var8 = par4;

           if (var5 == 0)
           {
               var6 = par2 - 1;
           }

           if (var5 == 1)
           {
               ++var6;
           }

           if (var5 == 2)
           {
               var8 = par4 - 1;
           }

           if (var5 == 3)
           {
               ++var8;
           }

           if (!this.blockBlocksFlow(par1World, var6, par3, var8) && (par1World.getBlockMaterial(var6, par3, var8) != this.blockMaterial || par1World.getBlockMetadata(var6, par3, var8) != 0))
           {
               if (!this.blockBlocksFlow(par1World, var6, par3 - 1, var8))
               {
                   this.flowCost[var5] = 0;
               }
               else
               {
                   this.flowCost[var5] = this.calculateFlowCost(par1World, var6, par3, var8, 1, var5);
               }
           }
       }

       var5 = this.flowCost[0];

       for (var6 = 1; var6 < 4; ++var6)
       {
           if (this.flowCost[var6] < var5)
           {
               var5 = this.flowCost[var6];
           }
       }

       for (var6 = 0; var6 < 4; ++var6)
       {
           this.isOptimalFlowDirection[var6] = this.flowCost[var6] == var5;
       }

       return this.isOptimalFlowDirection;
   }

   /**
    * Returns true if block at coords blocks fluids
    */
   private boolean blockBlocksFlow(World par1World, int par2, int par3, int par4)
   {
       int var5 = par1World.getBlockId(par2, par3, par4);

       if (var5 != Block.doorWood.blockID && var5 != Block.doorSteel.blockID && var5 != Block.signPost.blockID && var5 != Block.ladder.blockID && var5 != Block.reed.blockID)
       {
           if (var5 == 0)
           {
               return false;
           }
           else
           {
               Material var6 = Block.blocksList[var5].blockMaterial;
               return var6 == Material.portal ? true : var6.blocksMovement();
           }
       }
       else
       {
           return true;
       }
   }

   /**
    * getSmallestFlowDecay(World world, intx, int y, int z, int currentSmallestFlowDecay) - Looks up the flow decay at
    * the coordinates given and returns the smaller of this value or the provided currentSmallestFlowDecay. If one
    * value is valid and the other isn't, the valid value will be returned. Valid values are >= 0. Flow decay is the
    * amount that a liquid has dissipated. 0 indicates a source block.
    */
   protected int getSmallestFlowDecay(World par1World, int par2, int par3, int par4, int par5)
   {
       int var6 = this.getFlowDecay(par1World, par2, par3, par4);

       if (var6 < 0)
       {
           return par5;
       }
       else
       {

           if (var6 >= 8)
           {
               var6 = 0;
           }

           return par5 >= 0 && var6 >= par5 ? par5 : var6;
       }
   }

   /**
    * Returns true if the block at the coordinates can be displaced by the liquid.
    */
   private boolean liquidCanDisplaceBlock(World par1World, int par2, int par3, int par4)
   {
       Material var5 = par1World.getBlockMaterial(par2, par3, par4);
       return var5 == this.blockMaterial ? false : (var5 == Material.lava ? false : !this.blockBlocksFlow(par1World, par2, par3, par4));
   }

   /**
    * Called whenever the block is added into the world. Args: world, x, y, z
    */
   public void onBlockAdded(World par1World, int par2, int par3, int par4)
   {
       super.onBlockAdded(par1World, par2, par3, par4);

       if (par1World.getBlockId(par2, par3, par4) == this.blockID)
       {
           par1World.scheduleBlockUpdate(par2, par3, par4, this.blockID, this.tickRate());
       }
   }
}
