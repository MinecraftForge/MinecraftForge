package net.minecraft.src.blocks;
import java.util.ArrayList;

import net.minecraft.src.*;

public class MagicSlabBase extends Block
{
	
    public MagicSlabBase(int i, int j, Material material)
    {
        super(i, j, material);
    }

    public boolean canPlaceBlockAt(World world, int i, int j, int k)
    {
        return super.canPlaceBlockAt(world, i, j, k);
    }

    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z)
    {
    	boolean east = canConnectSlabTo(world, x, y, z - 1);
		boolean west = canConnectSlabTo(world, x, y, z + 1);
		boolean south = canConnectSlabTo(world, x - 1, y, z);
		boolean north = canConnectSlabTo(world, x + 1, y, z);
		boolean below = canConnectSlabTo(world, x, y - 1, z);
		boolean above = canConnectSlabTo(world, x, y + 1, z);
		
		boolean eastSlab = isSlab(world, x, y, z - 1);
		boolean westSlab = isSlab(world, x, y, z + 1);
		boolean southSlab = isSlab(world, x - 1, y, z);
		boolean northSlab = isSlab(world, x + 1, y, z);
		boolean belowSlab = isSlab(world, x, y - 1, z);
		boolean aboveSlab = isSlab(world, x, y + 1, z);
		
		byte slabsEast = countNearbyBlocks(world, x, y, z - 1);
		byte slabsWest = countNearbyBlocks(world, x, y, z + 1);
		byte slabsSouth = countNearbyBlocks(world, x - 1, y, z);
		byte slabsNorth = countNearbyBlocks(world, x + 1, y, z);
		byte slabsBelow = countNearbyBlocks(world, x, y - 1, z);
		byte slabsAbove = countNearbyBlocks(world, x, y + 1, z);
		
		boolean eastTube = checkForTube(world, x, y, z - 1);
		boolean westTube = checkForTube(world, x, y, z + 1);
		boolean southTube = checkForTube(world, x - 1, y, z);
		boolean northTube = checkForTube(world, x + 1, y, z);
		boolean belowTube = checkForTube(world, x, y - 1, z);
		boolean aboveTube = checkForTube(world, x, y + 1, z);
		
		boolean eastSlabShape = checkForSlabShape(world, x, y, z - 1);
		boolean westSlabShape = checkForSlabShape(world, x, y, z + 1);
		boolean southSlabShape = checkForSlabShape(world, x - 1, y, z);
		boolean northSlabShape = checkForSlabShape(world, x + 1, y, z);
		boolean belowSlabShape = checkForSlabShape(world, x, y - 1, z);
		boolean aboveSlabShape = checkForSlabShape(world, x, y + 1, z);
		
		float bottomHeight = 0.0F;
		float middleHeight = 0.5F;
		float topHeight = 1.0F;
		float lowOffset = 0.3125F;
		float highOffset = 0.6875F;
		
		float bX = bottomHeight;
		float bY = bottomHeight;
		float bZ = bottomHeight;
		float tX = topHeight;
		float tY = topHeight;
		float tZ = topHeight;
		
		byte num = 0;
		num += east ? 1 : 0;
		num += west ? 1 : 0;
		num += south ? 1 : 0;
		num += north ? 1 : 0;
		num += above ? 1 : 0;
		num += below ? 1 : 0;
		
		byte numSlab = 0;
		numSlab += eastSlab ? 1 : 0;
		numSlab += westSlab ? 1 : 0;
		numSlab += southSlab ? 1 : 0;
		numSlab += northSlab ? 1 : 0;
		numSlab += aboveSlab ? 1 : 0;
		numSlab += belowSlab ? 1 : 0;
		
		if(num == 0) {
			bX = 0.25F;
			bY = 0.25F;
			bZ = 0.25F;
			tX = 0.75F;
			tY = 0.75F;
			tZ = 0.75F;
		} else
		
		if(num == 1)
		{
			if(below)
				tY = middleHeight;
			if(above)
				bY = middleHeight;
			if(south)
				tX = middleHeight;
			if(north)
				bX = middleHeight;
			if(east)
				tZ = middleHeight;
			if(west)
				bZ = middleHeight;
			
		} else
		
		if(num == 2)
		{
			//Tubes
			if(below && above) {
				bX = lowOffset;
				bZ = lowOffset;
				tX = highOffset;
				tZ = highOffset;
			} else
			
			if(east && west) {
				bX = lowOffset;
				bY = lowOffset;
				tX = highOffset;
				tY = highOffset;
			} else
			
			if(north && south) {
				bY = lowOffset;
				bZ = lowOffset;
				tY = highOffset;
				tZ = highOffset;
			} else
			
			if(below)
			{
				if(south) {
					if(southTube) {
						tY = highOffset;
						bZ = lowOffset;
						tZ = highOffset;
					}
					else
						tY = middleHeight;
					if(belowTube) {
						tX = highOffset;
						bZ = lowOffset;
						tZ = highOffset;
					} else {
						tX = middleHeight;
					}
				}

				if(north) {
					if(northTube) {
						tY = highOffset;
						bZ = lowOffset;
						tZ = highOffset;
					} else
						tY = middleHeight;
					if(belowTube) {
						bX = lowOffset;
						bZ = lowOffset;
						tZ = highOffset;
					} else
						bX = middleHeight;
				}
				
				if(east) {
					if(eastTube) {
						tY = highOffset;
						bX = lowOffset;
						tX = highOffset;
					} else
						tY = middleHeight;
					if(belowTube) {
						tZ = highOffset;
						bX = lowOffset;
						tX = highOffset;
					} else
						tZ = middleHeight;
				}
				
				if(west) {
					if(westTube) {
						tY = highOffset;
						bX = lowOffset;
						tX = highOffset;
					} else
						tY = middleHeight;
					if(belowTube) {
						bZ = lowOffset;
						bX = lowOffset;
						tX = highOffset;
					} else
						bZ = middleHeight;
				}
				
			} else
			
			if(above)
			{
				if(south) {
					if(southTube) {
						bY = lowOffset;
						bZ = lowOffset;
						tZ = highOffset;
					}
					else
						bY = middleHeight;
					if(aboveTube) {
						tX = highOffset;
						bZ = lowOffset;
						tZ = highOffset;
					} else {
						tX = middleHeight;
					}
				}

				if(north) {
					if(northTube) {
						bY = lowOffset;
						bZ = lowOffset;
						tZ = highOffset;
					} else
						bY = middleHeight;
					if(aboveTube) {
						bX = lowOffset;
						bZ = lowOffset;
						tZ = highOffset;
					} else
						bX = middleHeight;
				}
				
				if(east) {
					if(eastTube) {
						bY = lowOffset;
						bX = lowOffset;
						tX = highOffset;
					} else
						bY = middleHeight;
					if(aboveTube) {
						tZ = highOffset;
						bX = lowOffset;
						tX = highOffset;
					} else
						tZ = middleHeight;
				}
				
				if(west) {
					if(westTube) {
						bY = lowOffset;
						bX = lowOffset;
						tX = highOffset;
					} else
						bY = middleHeight;
					if(aboveTube) {
						bZ = lowOffset;
						bX = lowOffset;
						tX = highOffset;
					} else
						bZ = middleHeight;
				}
			} else
			
			if(west && north) {
				if(westTube) {
					bX = lowOffset;
					bY = lowOffset;
					tY = highOffset;
				} else
					bX = middleHeight;
				if(northTube) {
					bZ = lowOffset;
					bY = lowOffset;
					tY = highOffset;
				} else {
					bZ = middleHeight;
				}
			}
			
			if(west && south) {
				if(westTube) {
					tX = highOffset;
					bY = lowOffset;
					tY = highOffset;
				} else
					tX = middleHeight;
				if(southTube) {
					bZ = lowOffset;
					bY = lowOffset;
					tY = highOffset;
				} else {
					bZ = middleHeight;
				}
			} else
			
			if(east && north) {
				if(eastTube) {
					bX = lowOffset;
					bY = lowOffset;
					tY = highOffset;
				} else
					bX = middleHeight;
				if(northTube) {
					tZ = highOffset;
					bY = lowOffset;
					tY = highOffset;
				} else {
					tZ = middleHeight;
				}
			} else
			
			if(east && south) {
				if(eastTube) {
					tX = highOffset;
					bY = lowOffset;
					tY = highOffset;
				} else
					tX = middleHeight;
				if(southTube) {
					tZ = highOffset;
					bY = lowOffset;
					tY = highOffset;
				} else {
					tZ = middleHeight;
				}
			}
		} else
		
		if(num == 3) 
		{
			if(below && above) 
			{
				if(north) {
					if(belowTube || aboveTube)
						bX = lowOffset;
					else
						bX = middleHeight;
					bZ = lowOffset;
					tZ = highOffset;
				} else
				
				if(south) {
					if(belowTube || aboveTube)
						tX = highOffset;
					else
						tX = middleHeight;
					bZ = lowOffset;
					tZ = highOffset;
				}
				
				if(west) {
					bX = lowOffset;
					tX = highOffset;
					if(belowTube || aboveTube)
						bZ = lowOffset;
					else
						bZ = middleHeight;
				}
				
				if(east) {
					bX = lowOffset;
					tX = highOffset;
					if(belowTube || aboveTube)
						tZ = highOffset;
					else
						tZ = middleHeight;
				}
			} else
			
			if(north && south)
			{
				if(above) {
					if(northTube || southTube)
						bY = lowOffset;
					else
						bY = middleHeight;
					bZ = lowOffset;
					tZ = highOffset;
				} else
				
				if(below) {
					if(northTube || southTube)
						tY = highOffset;
					else
						tY = middleHeight;
					bZ = lowOffset;
					tZ = highOffset;
				}
				
				if(west) {
					bY = lowOffset;
					tY = highOffset;
					if(northTube || southTube)
						bZ = lowOffset;
					else
						bZ = middleHeight;
				}
				
				if(east) {
					bY = lowOffset;
					tY = highOffset;
					if(northTube || southTube)
						tZ = highOffset;
					else
						tZ = middleHeight;
				}
			} else
			
			if(east && west) 
			{
				if(north) {
					if(eastTube || westTube)
						bX = lowOffset;
					else
						bX = middleHeight;
					bY = lowOffset;
					tY = highOffset;
				} else
				
				if(south) {
					if(eastTube || westTube)
						tX = highOffset;
					else
						tX = middleHeight;
					bY = lowOffset;
					tY = highOffset;
				}
				
				if(above) {
					bX = lowOffset;
					tX = highOffset;
					if(eastTube || westTube)
						bY = lowOffset;
					else
						bY = middleHeight;
				}
				
				if(below) {
					bX = lowOffset;
					tX = highOffset;
					if(eastTube || westTube)
						tY = highOffset;
					else
						tY = middleHeight;
				}
			} else
				
			if(above)
			{
				bY = middleHeight;
				if(north){
					bX = middleHeight;
					if(east)
						tZ = middleHeight;
					if(west)
						bZ = middleHeight;
				} else
				
				if(south){
					tX = middleHeight;
					if(east)
						tZ = middleHeight;
					if(west)
						bZ = middleHeight;
				}
			} else
			
			if(below)
			{
				tY = middleHeight;
				if(north){
					bX = middleHeight;
					if(east)
						tZ = middleHeight;
					if(west)
						bZ = middleHeight;
				} else
				
				if(south){
					tX = middleHeight;
					if(east)
						tZ = middleHeight;
					if(west)
						bZ = middleHeight;
				}
			}
		} else
		
		if(num == 4)
		{
			if(above && below) {
				if(east && west)
				{
					bX = lowOffset;
					tX = highOffset;
				} else
				
				if(north && south) {
					bZ = lowOffset;
					tZ = highOffset;
				} else
					
				if(north)
				{
					bX = middleHeight;
					if(east)
						tZ = middleHeight;
					if(west)
						bZ = middleHeight;
				} else
				
				if(south)
				{
					tX = middleHeight;
					if(east)
						tZ = middleHeight;
					if(west)
						bZ = middleHeight;
				}
			} else
			
			if (north && south)
			{
				if(!above && !below) {
					bY = lowOffset;
					tY = highOffset;
				}
				
				if(above)
				{
					bY = middleHeight;
					if(east)
						tZ = middleHeight;
					if(west)
						bZ = middleHeight;
				} else
				
				if(below)
				{
					tY = middleHeight;
					if(east)
						tZ = middleHeight;
					if(west)
						bZ = middleHeight;
				}
			} else
			
			if(east && west) {
				if(above)
				{
					bY = middleHeight;
					if(south)
						tX = middleHeight;
					if(north)
						bX = middleHeight;
				} else
				
				if(below)
				{
					tY = middleHeight;
					if(south)
						tX = middleHeight;
					if(north)
						bX = middleHeight;
				}
			}
		} else
		
		if(num == 5)
		{
			if(!below)
			{
				if(northTube || southTube || eastTube || westTube
						|| northSlabShape || southSlabShape || eastSlabShape || westSlabShape) {
					bY = lowOffset;
				}
				else {
					bY = middleHeight;
				}
				
				if(slabsAbove == 2)
				{
					tY = highOffset;
					/*slab.setBlockBounds(bX, bY, bZ, tX, tY, tZ);
					renderblocks.renderStandardBlock(slab, x, y, z);*/
										
					bX = lowOffset;
					tX = highOffset;
					bZ = lowOffset;
					tZ = highOffset;
					bY = highOffset;
					tY = topHeight;
				}
			}
			if(!above)
			{
				if(northTube || southTube || eastTube || westTube
						|| northSlabShape || southSlabShape || eastSlabShape || westSlabShape) {
					tY = highOffset;
				}
				else {
					tY = middleHeight;
				}
				
				if(slabsBelow == 2)
				{
					bY = lowOffset;

					/*slab.setBlockBounds(bX, bY, bZ, tX, tY, tZ);
					renderblocks.renderStandardBlock(slab, x, y, z);*/
										
					bX = lowOffset;
					tX = highOffset;
					bZ = lowOffset;
					tZ = highOffset;
					tY = lowOffset;
					bY = bottomHeight;
				}
			}
			if(!south)
			{
				if(aboveTube || belowTube || eastTube || westTube
						|| aboveSlabShape || belowSlabShape || eastSlabShape || westSlabShape) {
					bX = lowOffset;
				}
				else {
					bX = middleHeight;
				}
				
				if(slabsNorth == 2)
				{
					tX = highOffset;
					/*slab.setBlockBounds(bX, bY, bZ, tX, tY, tZ);
					renderblocks.renderStandardBlock(slab, x, y, z);*/
										
					bY = lowOffset;
					tY = highOffset;
					bZ = lowOffset;
					tZ = highOffset;
					bX = highOffset;
					tX = topHeight;
				}
			}
			if(!north)
			{
				//tX = middleHeight;
				if(aboveTube || belowTube || eastTube || westTube
						|| aboveSlabShape || belowSlabShape || eastSlabShape || westSlabShape) {
					tX = highOffset;
				}
				else {
					tX = middleHeight;
				}
				
				if(slabsSouth == 2)
				{
					bX = lowOffset;
					/*slab.setBlockBounds(bX, bY, bZ, tX, tY, tZ);
					renderblocks.renderStandardBlock(slab, x, y, z);*/
										
					bY = lowOffset;
					tY = highOffset;
					bZ = lowOffset;
					tZ = highOffset;
					tX = lowOffset;
					bX = bottomHeight;
				}
			}
			if(!east)
			{
				//bZ = middleHeight;
				if(northTube || southTube || aboveTube || belowTube
						|| northSlabShape || southSlabShape || aboveSlabShape || belowSlabShape) {
					bZ = lowOffset;
				}
				else {
					bZ = middleHeight;
				}
				
				if(slabsWest == 2)
				{
					tZ = highOffset;
					/*slab.setBlockBounds(bX, bY, bZ, tX, tY, tZ);
					renderblocks.renderStandardBlock(slab, x, y, z);*/
										
					bY = lowOffset;
					tY = highOffset;
					bX = lowOffset;
					tX = highOffset;
					bZ = highOffset;
					tZ = topHeight;
				}
			}
			if(!west)
			{
				if(northTube || southTube || aboveTube || belowTube
						|| northSlabShape || southSlabShape || aboveSlabShape || belowSlabShape) {
					tZ = highOffset;
				}
				else {
					tZ = middleHeight;
				}
				
				if(slabsEast == 2)
				{
					bZ = lowOffset;
					/*slab.setBlockBounds(bX, bY, bZ, tX, tY, tZ);
					renderblocks.renderStandardBlock(slab, x, y, z);*/
										
					bX = lowOffset;
					tX = highOffset;
					bY = lowOffset;
					tY = highOffset;
					bZ = bottomHeight;
					tZ = lowOffset;
				}
			}
		}
        
        return AxisAlignedBB.getBoundingBoxFromPool((float)x + bX, (float)y + bY, (float)z + bZ, 
        		(float)x + tX, (float)y + tY, (float)z + tZ);
    }

    public void setBlockBoundsBasedOnState(IBlockAccess iblockaccess, int x, int y, int z)
    {
    	boolean east = canConnectSlabTo(iblockaccess, x, y, z - 1);
		boolean west = canConnectSlabTo(iblockaccess, x, y, z + 1);
		boolean south = canConnectSlabTo(iblockaccess, x - 1, y, z);
		boolean north = canConnectSlabTo(iblockaccess, x + 1, y, z);
		boolean below = canConnectSlabTo(iblockaccess, x, y - 1, z);
		boolean above = canConnectSlabTo(iblockaccess, x, y + 1, z);
		
		boolean eastSlab = isSlab(iblockaccess, x, y, z - 1);
		boolean westSlab = isSlab(iblockaccess, x, y, z + 1);
		boolean southSlab = isSlab(iblockaccess, x - 1, y, z);
		boolean northSlab = isSlab(iblockaccess, x + 1, y, z);
		boolean belowSlab = isSlab(iblockaccess, x, y - 1, z);
		boolean aboveSlab = isSlab(iblockaccess, x, y + 1, z);
		
		byte slabsEast = countNearbyBlocks(iblockaccess, x, y, z - 1);
		byte slabsWest = countNearbyBlocks(iblockaccess, x, y, z + 1);
		byte slabsSouth = countNearbyBlocks(iblockaccess, x - 1, y, z);
		byte slabsNorth = countNearbyBlocks(iblockaccess, x + 1, y, z);
		byte slabsBelow = countNearbyBlocks(iblockaccess, x, y - 1, z);
		byte slabsAbove = countNearbyBlocks(iblockaccess, x, y + 1, z);
		
		boolean eastTube = checkForTube(iblockaccess, x, y, z - 1);
		boolean westTube = checkForTube(iblockaccess, x, y, z + 1);
		boolean southTube = checkForTube(iblockaccess, x - 1, y, z);
		boolean northTube = checkForTube(iblockaccess, x + 1, y, z);
		boolean belowTube = checkForTube(iblockaccess, x, y - 1, z);
		boolean aboveTube = checkForTube(iblockaccess, x, y + 1, z);
		
		boolean eastSlabShape = checkForSlabShape(iblockaccess, x, y, z - 1);
		boolean westSlabShape = checkForSlabShape(iblockaccess, x, y, z + 1);
		boolean southSlabShape = checkForSlabShape(iblockaccess, x - 1, y, z);
		boolean northSlabShape = checkForSlabShape(iblockaccess, x + 1, y, z);
		boolean belowSlabShape = checkForSlabShape(iblockaccess, x, y - 1, z);
		boolean aboveSlabShape = checkForSlabShape(iblockaccess, x, y + 1, z);
		
		float bottomHeight = 0.0F;
		float middleHeight = 0.5F;
		float topHeight = 1.0F;
		float lowOffset = 0.3125F;
		float highOffset = 0.6875F;
		
		float bX = bottomHeight;
		float bY = bottomHeight;
		float bZ = bottomHeight;
		float tX = topHeight;
		float tY = topHeight;
		float tZ = topHeight;
		
		byte num = 0;
		num += east ? 1 : 0;
		num += west ? 1 : 0;
		num += south ? 1 : 0;
		num += north ? 1 : 0;
		num += above ? 1 : 0;
		num += below ? 1 : 0;
		
		byte numSlab = 0;
		numSlab += eastSlab ? 1 : 0;
		numSlab += westSlab ? 1 : 0;
		numSlab += southSlab ? 1 : 0;
		numSlab += northSlab ? 1 : 0;
		numSlab += aboveSlab ? 1 : 0;
		numSlab += belowSlab ? 1 : 0;
		
		if(num == 0) {
			bX = 0.25F;
			bY = 0.25F;
			bZ = 0.25F;
			tX = 0.75F;
			tY = 0.75F;
			tZ = 0.75F;
		} else
		
		if(num == 1)
		{
			if(below)
				tY = middleHeight;
			if(above)
				bY = middleHeight;
			if(south)
				tX = middleHeight;
			if(north)
				bX = middleHeight;
			if(east)
				tZ = middleHeight;
			if(west)
				bZ = middleHeight;
			
		} else
		
		if(num == 2)
		{
			//Tubes
			if(below && above) {
				bX = lowOffset;
				bZ = lowOffset;
				tX = highOffset;
				tZ = highOffset;
			} else
			
			if(east && west) {
				bX = lowOffset;
				bY = lowOffset;
				tX = highOffset;
				tY = highOffset;
			} else
			
			if(north && south) {
				bY = lowOffset;
				bZ = lowOffset;
				tY = highOffset;
				tZ = highOffset;
			} else
			
			if(below)
			{
				if(south) {
					if(southTube) {
						tY = highOffset;
						bZ = lowOffset;
						tZ = highOffset;
					}
					else
						tY = middleHeight;
					if(belowTube) {
						tX = highOffset;
						bZ = lowOffset;
						tZ = highOffset;
					} else {
						tX = middleHeight;
					}
				}

				if(north) {
					if(northTube) {
						tY = highOffset;
						bZ = lowOffset;
						tZ = highOffset;
					} else
						tY = middleHeight;
					if(belowTube) {
						bX = lowOffset;
						bZ = lowOffset;
						tZ = highOffset;
					} else
						bX = middleHeight;
				}
				
				if(east) {
					if(eastTube) {
						tY = highOffset;
						bX = lowOffset;
						tX = highOffset;
					} else
						tY = middleHeight;
					if(belowTube) {
						tZ = highOffset;
						bX = lowOffset;
						tX = highOffset;
					} else
						tZ = middleHeight;
				}
				
				if(west) {
					if(westTube) {
						tY = highOffset;
						bX = lowOffset;
						tX = highOffset;
					} else
						tY = middleHeight;
					if(belowTube) {
						bZ = lowOffset;
						bX = lowOffset;
						tX = highOffset;
					} else
						bZ = middleHeight;
				}
				
			} else
			
			if(above)
			{
				if(south) {
					if(southTube) {
						bY = lowOffset;
						bZ = lowOffset;
						tZ = highOffset;
					}
					else
						bY = middleHeight;
					if(aboveTube) {
						tX = highOffset;
						bZ = lowOffset;
						tZ = highOffset;
					} else {
						tX = middleHeight;
					}
				}

				if(north) {
					if(northTube) {
						bY = lowOffset;
						bZ = lowOffset;
						tZ = highOffset;
					} else
						bY = middleHeight;
					if(aboveTube) {
						bX = lowOffset;
						bZ = lowOffset;
						tZ = highOffset;
					} else
						bX = middleHeight;
				}
				
				if(east) {
					if(eastTube) {
						bY = lowOffset;
						bX = lowOffset;
						tX = highOffset;
					} else
						bY = middleHeight;
					if(aboveTube) {
						tZ = highOffset;
						bX = lowOffset;
						tX = highOffset;
					} else
						tZ = middleHeight;
				}
				
				if(west) {
					if(westTube) {
						bY = lowOffset;
						bX = lowOffset;
						tX = highOffset;
					} else
						bY = middleHeight;
					if(aboveTube) {
						bZ = lowOffset;
						bX = lowOffset;
						tX = highOffset;
					} else
						bZ = middleHeight;
				}
			} else
			
			if(west && north) {
				if(westTube) {
					bX = lowOffset;
					bY = lowOffset;
					tY = highOffset;
				} else
					bX = middleHeight;
				if(northTube) {
					bZ = lowOffset;
					bY = lowOffset;
					tY = highOffset;
				} else {
					bZ = middleHeight;
				}
			}
			
			if(west && south) {
				if(westTube) {
					tX = highOffset;
					bY = lowOffset;
					tY = highOffset;
				} else
					tX = middleHeight;
				if(southTube) {
					bZ = lowOffset;
					bY = lowOffset;
					tY = highOffset;
				} else {
					bZ = middleHeight;
				}
			} else
			
			if(east && north) {
				if(eastTube) {
					bX = lowOffset;
					bY = lowOffset;
					tY = highOffset;
				} else
					bX = middleHeight;
				if(northTube) {
					tZ = highOffset;
					bY = lowOffset;
					tY = highOffset;
				} else {
					tZ = middleHeight;
				}
			} else
			
			if(east && south) {
				if(eastTube) {
					tX = highOffset;
					bY = lowOffset;
					tY = highOffset;
				} else
					tX = middleHeight;
				if(southTube) {
					tZ = highOffset;
					bY = lowOffset;
					tY = highOffset;
				} else {
					tZ = middleHeight;
				}
			}
		} else
		
		if(num == 3) 
		{
			if(below && above) 
			{
				if(north) {
					if(belowTube || aboveTube)
						bX = lowOffset;
					else
						bX = middleHeight;
					bZ = lowOffset;
					tZ = highOffset;
				} else
				
				if(south) {
					if(belowTube || aboveTube)
						tX = highOffset;
					else
						tX = middleHeight;
					bZ = lowOffset;
					tZ = highOffset;
				}
				
				if(west) {
					bX = lowOffset;
					tX = highOffset;
					if(belowTube || aboveTube)
						bZ = lowOffset;
					else
						bZ = middleHeight;
				}
				
				if(east) {
					bX = lowOffset;
					tX = highOffset;
					if(belowTube || aboveTube)
						tZ = highOffset;
					else
						tZ = middleHeight;
				}
			} else
			
			if(north && south)
			{
				if(above) {
					if(northTube || southTube)
						bY = lowOffset;
					else
						bY = middleHeight;
					bZ = lowOffset;
					tZ = highOffset;
				} else
				
				if(below) {
					if(northTube || southTube)
						tY = highOffset;
					else
						tY = middleHeight;
					bZ = lowOffset;
					tZ = highOffset;
				}
				
				if(west) {
					bY = lowOffset;
					tY = highOffset;
					if(northTube || southTube)
						bZ = lowOffset;
					else
						bZ = middleHeight;
				}
				
				if(east) {
					bY = lowOffset;
					tY = highOffset;
					if(northTube || southTube)
						tZ = highOffset;
					else
						tZ = middleHeight;
				}
			} else
			
			if(east && west) 
			{
				if(north) {
					if(eastTube || westTube)
						bX = lowOffset;
					else
						bX = middleHeight;
					bY = lowOffset;
					tY = highOffset;
				} else
				
				if(south) {
					if(eastTube || westTube)
						tX = highOffset;
					else
						tX = middleHeight;
					bY = lowOffset;
					tY = highOffset;
				}
				
				if(above) {
					bX = lowOffset;
					tX = highOffset;
					if(eastTube || westTube)
						bY = lowOffset;
					else
						bY = middleHeight;
				}
				
				if(below) {
					bX = lowOffset;
					tX = highOffset;
					if(eastTube || westTube)
						tY = highOffset;
					else
						tY = middleHeight;
				}
			} else
				
			if(above)
			{
				bY = middleHeight;
				if(north){
					bX = middleHeight;
					if(east)
						tZ = middleHeight;
					if(west)
						bZ = middleHeight;
				} else
				
				if(south){
					tX = middleHeight;
					if(east)
						tZ = middleHeight;
					if(west)
						bZ = middleHeight;
				}
			} else
			
			if(below)
			{
				tY = middleHeight;
				if(north){
					bX = middleHeight;
					if(east)
						tZ = middleHeight;
					if(west)
						bZ = middleHeight;
				} else
				
				if(south){
					tX = middleHeight;
					if(east)
						tZ = middleHeight;
					if(west)
						bZ = middleHeight;
				}
			}
		} else
		
		if(num == 4)
		{
			if(above && below) {
				if(east && west)
				{
					bX = lowOffset;
					tX = highOffset;
				} else
				
				if(north && south) {
					bZ = lowOffset;
					tZ = highOffset;
				} else
					
				if(north)
				{
					bX = middleHeight;
					if(east)
						tZ = middleHeight;
					if(west)
						bZ = middleHeight;
				} else
				
				if(south)
				{
					tX = middleHeight;
					if(east)
						tZ = middleHeight;
					if(west)
						bZ = middleHeight;
				}
			} else
			
			if (north && south)
			{
				if(!above && !below) {
					bY = lowOffset;
					tY = highOffset;
				}
				
				if(above)
				{
					bY = middleHeight;
					if(east)
						tZ = middleHeight;
					if(west)
						bZ = middleHeight;
				} else
				
				if(below)
				{
					tY = middleHeight;
					if(east)
						tZ = middleHeight;
					if(west)
						bZ = middleHeight;
				}
			} else
			
			if(east && west) {
				if(above)
				{
					bY = middleHeight;
					if(south)
						tX = middleHeight;
					if(north)
						bX = middleHeight;
				} else
				
				if(below)
				{
					tY = middleHeight;
					if(south)
						tX = middleHeight;
					if(north)
						bX = middleHeight;
				}
			}
		} else
		
		if(num == 5)
		{
			if(!below)
			{
				if(northTube || southTube || eastTube || westTube
						|| northSlabShape || southSlabShape || eastSlabShape || westSlabShape) {
					bY = lowOffset;
				}
				else {
					bY = middleHeight;
				}
				
				if(slabsAbove == 2)
				{
					tY = highOffset;
					/*slab.setBlockBounds(bX, bY, bZ, tX, tY, tZ);
					renderblocks.renderStandardBlock(slab, x, y, z);*/
					/*setBlockBounds(bX, bY, bZ, tX, tY, tZ);
										
					bX = lowOffset;
					tX = highOffset;
					bZ = lowOffset;
					tZ = highOffset;
					bY = highOffset;
					tY = topHeight;*/
				}
			}
			if(!above)
			{
				if(northTube || southTube || eastTube || westTube
						|| northSlabShape || southSlabShape || eastSlabShape || westSlabShape) {
					tY = highOffset;
				}
				else {
					tY = middleHeight;
				}
				
				if(slabsBelow == 2)
				{
					bY = lowOffset;
					//bY = 0F;
					/*slab.setBlockBounds(bX, bY, bZ, tX, tY, tZ);
					renderblocks.renderStandardBlock(slab, x, y, z);*/
					/*setBlockBounds(bX, bY, bZ, tX, tY, tZ);*/
										
					/*bX = lowOffset;
					tX = highOffset;
					bZ = lowOffset;
					tZ = highOffset;
					tY = lowOffset;
					bY = bottomHeight;*/
				}
			}
			if(!south)
			{
				if(aboveTube || belowTube || eastTube || westTube
						|| aboveSlabShape || belowSlabShape || eastSlabShape || westSlabShape) {
					bX = lowOffset;
				}
				else {
					bX = middleHeight;
				}
				
				if(slabsNorth == 2)
				{
					tX = highOffset;
					/*slab.setBlockBounds(bX, bY, bZ, tX, tY, tZ);
					renderblocks.renderStandardBlock(slab, x, y, z);*/
					/*setBlockBounds(bX, bY, bZ, tX, tY, tZ);
										
					bY = lowOffset;
					tY = highOffset;
					bZ = lowOffset;
					tZ = highOffset;
					bX = highOffset;
					tX = topHeight;*/
				}
			}
			if(!north)
			{
				//tX = middleHeight;
				if(aboveTube || belowTube || eastTube || westTube
						|| aboveSlabShape || belowSlabShape || eastSlabShape || westSlabShape) {
					tX = highOffset;
				}
				else {
					tX = middleHeight;
				}
				
				if(slabsSouth == 2)
				{
					bX = lowOffset;
					/*slab.setBlockBounds(bX, bY, bZ, tX, tY, tZ);
					renderblocks.renderStandardBlock(slab, x, y, z);*/
					/*setBlockBounds(bX, bY, bZ, tX, tY, tZ);
										
					bY = lowOffset;
					tY = highOffset;
					bZ = lowOffset;
					tZ = highOffset;
					tX = lowOffset;
					bX = bottomHeight;*/
				}
			}
			if(!east)
			{
				//bZ = middleHeight;
				if(northTube || southTube || aboveTube || belowTube
						|| northSlabShape || southSlabShape || aboveSlabShape || belowSlabShape) {
					bZ = lowOffset;
				}
				else {
					bZ = middleHeight;
				}
				
				if(slabsWest == 2)
				{
					tZ = highOffset;
					/*slab.setBlockBounds(bX, bY, bZ, tX, tY, tZ);
					renderblocks.renderStandardBlock(slab, x, y, z);*/
					/*setBlockBounds(bX, bY, bZ, tX, tY, tZ);
										
					bY = lowOffset;
					tY = highOffset;
					bX = lowOffset;
					tX = highOffset;
					bZ = highOffset;
					tZ = topHeight;*/
				}
			}
			if(!west)
			{
				if(northTube || southTube || aboveTube || belowTube
						|| northSlabShape || southSlabShape || aboveSlabShape || belowSlabShape) {
					tZ = highOffset;
				}
				else {
					tZ = middleHeight;
				}
				
				if(slabsEast == 2)
				{
					bZ = lowOffset;
					/*slab.setBlockBounds(bX, bY, bZ, tX, tY, tZ);
					renderblocks.renderStandardBlock(slab, x, y, z);*/
					/*setBlockBounds(bX, bY, bZ, tX, tY, tZ);
										
					bX = lowOffset;
					tX = highOffset;
					bY = lowOffset;
					tY = highOffset;
					bZ = bottomHeight;
					tZ = lowOffset;*/
				}
			}
		}
        
        setBlockBounds(bX, bY, bZ, tX, tY, tZ);
    }

    public boolean isOpaqueCube()
    {
        return false;
    }

    public boolean renderAsNormalBlock()
    {
        return false;
    }

    public int getRenderType()
    {
        return mod_InfiBlocks.magicSlabModel;
    }
    
    protected int damageDropped(int md)
    {
        return md;
    }

    public boolean canConnectSlabTo(IBlockAccess iblockaccess, int i, int j, int k)
    {
        int bID = iblockaccess.getBlockId(i, j, k);
        if (Block.blocksList[bID] instanceof MagicSlabBase || bID == Block.stairSingle.blockID
        		|| Block.blocksList[bID] instanceof BlockDoor || Block.blocksList[bID] instanceof BlockPane
        		|| Block.blocksList[bID] instanceof PaneBase)
        {
            return true;
        }
        Block block = Block.blocksList[bID];
        if (block != null && block.blockMaterial.isOpaque() && block.renderAsNormalBlock())
        {
            return block.blockMaterial != Material.pumpkin;
        }
        else
        {
            return false;
        }
    }
    
    public boolean isSlab(IBlockAccess iblockaccess, int i, int j, int k)
    {
        int bID = iblockaccess.getBlockId(i, j, k);
        if (Block.blocksList[bID] instanceof MagicSlabBase || bID == Block.stairSingle.blockID)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    public byte countNearbyBlocks(IBlockAccess iblockaccess, int x, int y, int z)
    {		
    	boolean east = canConnectSlabTo(iblockaccess, x, y, z - 1);
        boolean west = canConnectSlabTo(iblockaccess, x, y, z + 1);
        boolean south = canConnectSlabTo(iblockaccess, x - 1, y, z);
        boolean north = canConnectSlabTo(iblockaccess, x + 1, y, z);
        boolean below = canConnectSlabTo(iblockaccess, x, y - 1, z);
        boolean above = canConnectSlabTo(iblockaccess, x, y + 1, z);
        
        byte num = 0;
        num += east ? 1 : 0;
        num += west ? 1 : 0;
        num += south ? 1 : 0;
        num += north ? 1 : 0;
        num += above ? 1 : 0;
        num += below ? 1 : 0;
    	
    	return num;
    }
    
    public boolean checkForTube(IBlockAccess iblockaccess, int x, int y, int z) 
    {
    	boolean isTube = false;
        byte num = countNearbyBlocks(iblockaccess, x, y, z);
        
        if(num == 2)
        {
	        if(canConnectSlabTo(iblockaccess, x, y, z - 1) && isSlab(iblockaccess, x, y, z-1)
	        		&& canConnectSlabTo(iblockaccess, x, y, z + 1) && isSlab(iblockaccess, x, y, z+1)) {
	        	isTube = true;
	        } else
	        if(canConnectSlabTo(iblockaccess, x - 1, y, z) && isSlab(iblockaccess, x-1, y, z)
	        		&& canConnectSlabTo(iblockaccess, x + 1, y, z) && isSlab(iblockaccess, x+1, y, z)) {
	        	isTube = true;
	        } else
	        if(canConnectSlabTo(iblockaccess, x, y - 1, z) && isSlab(iblockaccess, x, y-1, z)
	        		&& canConnectSlabTo(iblockaccess, x, y + 1, z) && isSlab(iblockaccess, x, y+1, z)) {
	        	isTube = true;
	        }
    	}
        
    	return isTube;
    }
    
    public boolean checkForSlabShape(IBlockAccess iblockaccess, int x, int y, int z)
    {
    	//boolean isSlab = false;
    	byte connections = 0;
        byte num = countNearbyBlocks(iblockaccess, x, y, z);
        
        if(num == 4) {
        	if(canConnectSlabTo(iblockaccess, x, y, z - 1) && canConnectSlabTo(iblockaccess, x, y, z + 1)) {
	        	connections += 1;
	        }
	        if(canConnectSlabTo(iblockaccess, x - 1, y, z) && canConnectSlabTo(iblockaccess, x + 1, y, z)) {
	        	connections += 1;
	        }
	        if(canConnectSlabTo(iblockaccess, x, y - 1, z) && canConnectSlabTo(iblockaccess, x, y + 1, z)) {
	        	connections += 1;
	        }
        }
        
        if(connections == 2) {
        	return true;
        } else {
        	return false;
        }
    }
}
