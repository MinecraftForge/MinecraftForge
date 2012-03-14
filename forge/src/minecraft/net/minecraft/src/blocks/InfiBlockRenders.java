package net.minecraft.src.blocks;

import org.lwjgl.opengl.GL11;

import net.minecraft.src.Block;
import net.minecraft.src.EntityRenderer;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.RenderBlocks;
import net.minecraft.src.Tessellator;
import net.minecraft.src.mod_InfiBlocks;
import net.minecraft.src.forge.ICustomItemRenderer;
import net.minecraft.src.forge.MinecraftForgeClient;

public class InfiBlockRenders {
	
	public InfiBlockRenders() {}
	
	public static boolean RenderMagicWorld(RenderBlocks renderblocks, 
			IBlockAccess iblockaccess, int x, int y, int z, MagicSlabBase slab)
	{
		boolean east = slab.canConnectSlabTo(iblockaccess, x, y, z - 1);
		boolean west = slab.canConnectSlabTo(iblockaccess, x, y, z + 1);
		boolean south = slab.canConnectSlabTo(iblockaccess, x - 1, y, z);
		boolean north = slab.canConnectSlabTo(iblockaccess, x + 1, y, z);
		boolean below = slab.canConnectSlabTo(iblockaccess, x, y - 1, z);
		boolean above = slab.canConnectSlabTo(iblockaccess, x, y + 1, z);
		
		boolean eastSlab = slab.isSlab(iblockaccess, x, y, z - 1);
		boolean westSlab = slab.isSlab(iblockaccess, x, y, z + 1);
		boolean southSlab = slab.isSlab(iblockaccess, x - 1, y, z);
		boolean northSlab = slab.isSlab(iblockaccess, x + 1, y, z);
		boolean belowSlab = slab.isSlab(iblockaccess, x, y - 1, z);
		boolean aboveSlab = slab.isSlab(iblockaccess, x, y + 1, z);
		
		byte slabsEast = slab.countNearbyBlocks(iblockaccess, x, y, z - 1);
		byte slabsWest = slab.countNearbyBlocks(iblockaccess, x, y, z + 1);
		byte slabsSouth = slab.countNearbyBlocks(iblockaccess, x - 1, y, z);
		byte slabsNorth = slab.countNearbyBlocks(iblockaccess, x + 1, y, z);
		byte slabsBelow = slab.countNearbyBlocks(iblockaccess, x, y - 1, z);
		byte slabsAbove = slab.countNearbyBlocks(iblockaccess, x, y + 1, z);
		
		boolean eastTube = slab.checkForTube(iblockaccess, x, y, z - 1);
		boolean westTube = slab.checkForTube(iblockaccess, x, y, z + 1);
		boolean southTube = slab.checkForTube(iblockaccess, x - 1, y, z);
		boolean northTube = slab.checkForTube(iblockaccess, x + 1, y, z);
		boolean belowTube = slab.checkForTube(iblockaccess, x, y - 1, z);
		boolean aboveTube = slab.checkForTube(iblockaccess, x, y + 1, z);
		
		boolean eastSlabShape = slab.checkForSlabShape(iblockaccess, x, y, z - 1);
		boolean westSlabShape = slab.checkForSlabShape(iblockaccess, x, y, z + 1);
		boolean southSlabShape = slab.checkForSlabShape(iblockaccess, x - 1, y, z);
		boolean northSlabShape = slab.checkForSlabShape(iblockaccess, x + 1, y, z);
		boolean belowSlabShape = slab.checkForSlabShape(iblockaccess, x, y - 1, z);
		boolean aboveSlabShape = slab.checkForSlabShape(iblockaccess, x, y + 1, z);
		
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
					slab.setBlockBounds(bX, bY, bZ, tX, tY, tZ);
					renderblocks.renderStandardBlock(slab, x, y, z);
										
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
					//bY = 0F;
					slab.setBlockBounds(bX, bY, bZ, tX, tY, tZ);
					renderblocks.renderStandardBlock(slab, x, y, z);
										
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
					slab.setBlockBounds(bX, bY, bZ, tX, tY, tZ);
					renderblocks.renderStandardBlock(slab, x, y, z);
										
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
					slab.setBlockBounds(bX, bY, bZ, tX, tY, tZ);
					renderblocks.renderStandardBlock(slab, x, y, z);
										
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
					slab.setBlockBounds(bX, bY, bZ, tX, tY, tZ);
					renderblocks.renderStandardBlock(slab, x, y, z);
										
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
					slab.setBlockBounds(bX, bY, bZ, tX, tY, tZ);
					renderblocks.renderStandardBlock(slab, x, y, z);
										
					bX = lowOffset;
					tX = highOffset;
					bY = lowOffset;
					tY = highOffset;
					bZ = bottomHeight;
					tZ = lowOffset;
				}
			}
		}

		slab.setBlockBounds(bX, bY, bZ, tX, tY, tZ);
		renderblocks.renderStandardBlock(slab, x, y, z);
		return false;
	}
	
	public static void RenderMagicInv(RenderBlocks renderblocks, Block block, int i)
	{
		block.setBlockBounds(0.3125F, 0.1875F, 0.1875F, 0.6875F, 0.8125F, 0.8125F);
		RenderDo(renderblocks, block, i);
	}

	private static void RenderDo(RenderBlocks renderblocks, Block block, int i)
	{
		Tessellator tessellator = Tessellator.instance;
		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, -1F, 0.0F);
		renderblocks.renderBottomFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(0, i));
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 1.0F, 0.0F);
		renderblocks.renderTopFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(1, i));
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, -1F);
		renderblocks.renderEastFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(2, i));
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, 1.0F);
		renderblocks.renderWestFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(3, i));
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(-1F, 0.0F, 0.0F);
		renderblocks.renderNorthFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(4, i));
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(1.0F, 0.0F, 0.0F);
		renderblocks.renderSouthFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(5, i));
		tessellator.draw();
		GL11.glTranslatef(0.5F, 0.5F, 0.5F);
	}
	
	public static boolean RenderPaneInWorld(RenderBlocks renderblocks, 
			IBlockAccess iblockaccess, int x, int y, int z, PaneBase pane)
	{
		int l = iblockaccess.getWorldHeight();
        Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(pane.getMixedBrightnessForBlock(iblockaccess, x, y, z));
        float f = 1.0F;
        int i1 = pane.colorMultiplier(iblockaccess, x, y, z);
        float f1 = (float)(i1 >> 16 & 0xff) / 255F;
        float f2 = (float)(i1 >> 8 & 0xff) / 255F;
        float f3 = (float)(i1 & 0xff) / 255F;
        if (EntityRenderer.anaglyphEnable)
        {
            float f4 = (f1 * 30F + f2 * 59F + f3 * 11F) / 100F;
            float f5 = (f1 * 30F + f2 * 70F) / 100F;
            float f6 = (f1 * 30F + f3 * 70F) / 100F;
            f1 = f4;
            f2 = f5;
            f3 = f6;
        }
        tessellator.setColorOpaque_F(f * f1, f * f2, f * f3);
        boolean flag = false;
        boolean flag1 = false;
        int l1;
        int i2;
        
        int j1 = iblockaccess.getBlockMetadata(x, y, z);
        l1 = pane.getBlockTextureFromSideAndMetadata(0, j1);
        i2 = pane.getSideTextureIndex(j1);
        
        int k1 = (l1 & 0xf) << 4;
        int j2 = l1 & 0xf0;
        double d = (float)k1 / 256F;
        double d1 = ((float)k1 + 7.99F) / 256F;
        double d2 = ((float)k1 + 15.99F) / 256F;
        double d3 = (float)j2 / 256F;
        double d4 = ((float)j2 + 15.99F) / 256F;
        int k2 = (i2 & 0xf) << 4;
        int l2 = i2 & 0xf0;
        double d5 = (float)(k2 + 7) / 256F;
        double d6 = ((float)k2 + 8.99F) / 256F;
        double d7 = (float)l2 / 256F;
        double d8 = (float)(l2 + 8) / 256F;
        double d9 = ((float)l2 + 15.99F) / 256F;
        double xBot = x;
        double xMid = (double)x + 0.5D;
        double xTop = x + 1;
        double zBot = z;
        double zMid = (double)z + 0.5D;
        double zTop = z + 1;
        double xMidDown = ((double)x + 0.5D) - 0.0625D;
        double xMidUp = (double)x + 0.5D + 0.0625D;
        double zMidDown = ((double)z + 0.5D) - 0.0625D;
        double zMidUp = (double)z + 0.5D + 0.0625D;
        boolean west = pane.canConnectTo(iblockaccess.getBlockId(x, y, z - 1));
        boolean east = pane.canConnectTo(iblockaccess.getBlockId(x, y, z + 1));
        boolean south = pane.canConnectTo(iblockaccess.getBlockId(x - 1, y, z));
        boolean north = pane.canConnectTo(iblockaccess.getBlockId(x + 1, y, z));
        boolean renderAbove = pane.shouldSideBeRendered(iblockaccess, x, y + 1, z, 1);
        boolean renderBelow = pane.shouldSideBeRendered(iblockaccess, x, y - 1, z, 0);
        if ((!south || !north) && (south || north || west || east))
        {
            if (south && !north)
            {
                /*tessellator.addVertexWithUV(xBot, y + 1, zMid, d, d3);
                tessellator.addVertexWithUV(xBot, y + 0, zMid, d, d4);
                tessellator.addVertexWithUV(xMid, y + 0, zMid, d1, d4);
                tessellator.addVertexWithUV(xMid, y + 1, zMid, d1, d3);
                tessellator.addVertexWithUV(xMid, y + 1, zMid, d, d3);
                tessellator.addVertexWithUV(xMid, y + 0, zMid, d, d4);
                tessellator.addVertexWithUV(xBot, y + 0, zMid, d1, d4);
                tessellator.addVertexWithUV(xBot, y + 1, zMid, d1, d3);*/
            	tessellator.addVertexWithUV(xBot, y + 1, zMidDown, d, d3);
                tessellator.addVertexWithUV(xBot, y + 0, zMidDown, d, d4);
                tessellator.addVertexWithUV(xMid, y + 0, zMidDown, d1, d4);
                tessellator.addVertexWithUV(xMid, y + 1, zMidDown, d1, d3);
                tessellator.addVertexWithUV(xMid, y + 1, zMidUp, d, d3);
                tessellator.addVertexWithUV(xMid, y + 0, zMidUp, d, d4);
                tessellator.addVertexWithUV(xBot, y + 0, zMidUp, d1, d4);
                tessellator.addVertexWithUV(xBot, y + 1, zMidUp, d1, d3);
                if (!east && !west)
                {
                    tessellator.addVertexWithUV(xMid, y + 1, zMidUp, d5, d7);
                    tessellator.addVertexWithUV(xMid, y + 0, zMidUp, d5, d9);
                    tessellator.addVertexWithUV(xMid, y + 0, zMidDown, d6, d9);
                    tessellator.addVertexWithUV(xMid, y + 1, zMidDown, d6, d7);
                    /*tessellator.addVertexWithUV(xMid, y + 1, zMidDown, d5, d7);
                    tessellator.addVertexWithUV(xMid, y + 0, zMidDown, d5, d9);
                    tessellator.addVertexWithUV(xMid, y + 0, zMidUp, d6, d9);
                    tessellator.addVertexWithUV(xMid, y + 1, zMidUp, d6, d7);*/
                }
                if (renderAbove || y < l - 1 && iblockaccess.isAirBlock(x - 1, y + 1, z))
                {
                    tessellator.addVertexWithUV(xBot, (double)(y + 1) + 0.01D, zMidUp, d6, d8);
                    tessellator.addVertexWithUV(xMid, (double)(y + 1) + 0.01D, zMidUp, d6, d9);
                    tessellator.addVertexWithUV(xMid, (double)(y + 1) + 0.01D, zMidDown, d5, d9);
                    tessellator.addVertexWithUV(xBot, (double)(y + 1) + 0.01D, zMidDown, d5, d8);
                    /*tessellator.addVertexWithUV(xMid, (double)(y + 1) + 0.01D, zMidUp, d6, d8);
                    tessellator.addVertexWithUV(xBot, (double)(y + 1) + 0.01D, zMidUp, d6, d9);
                    tessellator.addVertexWithUV(xBot, (double)(y + 1) + 0.01D, zMidDown, d5, d9);
                    tessellator.addVertexWithUV(xMid, (double)(y + 1) + 0.01D, zMidDown, d5, d8);*/
                }
                if (renderBelow || y > 1 && iblockaccess.isAirBlock(x - 1, y - 1, z))
                {
                    tessellator.addVertexWithUV(xBot, (double)y - 0.01D, zMidUp, d6, d8);
                    tessellator.addVertexWithUV(xMid, (double)y - 0.01D, zMidUp, d6, d9);
                    tessellator.addVertexWithUV(xMid, (double)y - 0.01D, zMidDown, d5, d9);
                    tessellator.addVertexWithUV(xBot, (double)y - 0.01D, zMidDown, d5, d8);
                    /*tessellator.addVertexWithUV(xMid, (double)y - 0.01D, zMidUp, d6, d8);
                    tessellator.addVertexWithUV(xBot, (double)y - 0.01D, zMidUp, d6, d9);
                    tessellator.addVertexWithUV(xBot, (double)y - 0.01D, zMidDown, d5, d9);
                    tessellator.addVertexWithUV(xMid, (double)y - 0.01D, zMidDown, d5, d8);*/
                }
            }
            else if (!south && north)
            {
                tessellator.addVertexWithUV(xMid, y + 1, zMidDown, d1, d3);
                tessellator.addVertexWithUV(xMid, y + 0, zMidDown, d1, d4);
                tessellator.addVertexWithUV(xTop, y + 0, zMidDown, d2, d4);
                tessellator.addVertexWithUV(xTop, y + 1, zMidDown, d2, d3);
                tessellator.addVertexWithUV(xTop, y + 1, zMidUp, d1, d3);
                tessellator.addVertexWithUV(xTop, y + 0, zMidUp, d1, d4);
                tessellator.addVertexWithUV(xMid, y + 0, zMidUp, d2, d4);
                tessellator.addVertexWithUV(xMid, y + 1, zMidUp, d2, d3);
                if (!east && !west)
                {
                    tessellator.addVertexWithUV(xMid, y + 1, zMidDown, d5, d7);
                    tessellator.addVertexWithUV(xMid, y + 0, zMidDown, d5, d9);
                    tessellator.addVertexWithUV(xMid, y + 0, zMidUp, d6, d9);
                    tessellator.addVertexWithUV(xMid, y + 1, zMidUp, d6, d7);
                   /* tessellator.addVertexWithUV(xMid, y + 1, zMidUp, d5, d7);
                    tessellator.addVertexWithUV(xMid, y + 0, zMidUp, d5, d9);
                    tessellator.addVertexWithUV(xMid, y + 0, zMidDown, d6, d9);
                    tessellator.addVertexWithUV(xMid, y + 1, zMidDown, d6, d7);*/
                }
                if (renderAbove || y < l - 1 && iblockaccess.isAirBlock(x + 1, y + 1, z))
                {
                    tessellator.addVertexWithUV(xMid, (double)(y + 1) + 0.01D, zMidUp, d6, d7);
                    tessellator.addVertexWithUV(xTop, (double)(y + 1) + 0.01D, zMidUp, d6, d8);
                    tessellator.addVertexWithUV(xTop, (double)(y + 1) + 0.01D, zMidDown, d5, d8);
                    tessellator.addVertexWithUV(xMid, (double)(y + 1) + 0.01D, zMidDown, d5, d7);
                    /*tessellator.addVertexWithUV(xTop, (double)(y + 1) + 0.01D, zMidUp, d6, d7);
                    tessellator.addVertexWithUV(xMid, (double)(y + 1) + 0.01D, zMidUp, d6, d8);
                    tessellator.addVertexWithUV(xMid, (double)(y + 1) + 0.01D, zMidDown, d5, d8);
                    tessellator.addVertexWithUV(xTop, (double)(y + 1) + 0.01D, zMidDown, d5, d7);*/
                }
                if (renderBelow || y > 1 && iblockaccess.isAirBlock(x + 1, y - 1, z))
                {
                    tessellator.addVertexWithUV(xMid, (double)y - 0.01D, zMidUp, d6, d7);
                    tessellator.addVertexWithUV(xTop, (double)y - 0.01D, zMidUp, d6, d8);
                    tessellator.addVertexWithUV(xTop, (double)y - 0.01D, zMidDown, d5, d8);
                    tessellator.addVertexWithUV(xMid, (double)y - 0.01D, zMidDown, d5, d7);
                    /*tessellator.addVertexWithUV(xTop, (double)y - 0.01D, zMidUp, d6, d7);
                    tessellator.addVertexWithUV(xMid, (double)y - 0.01D, zMidUp, d6, d8);
                    tessellator.addVertexWithUV(xMid, (double)y - 0.01D, zMidDown, d5, d8);
                    tessellator.addVertexWithUV(xTop, (double)y - 0.01D, zMidDown, d5, d7);*/
                }
            }
        }
        else
        {
            tessellator.addVertexWithUV(xBot, y + 1, zMidDown, d, d3);
            tessellator.addVertexWithUV(xBot, y + 0, zMidDown, d, d4);
            tessellator.addVertexWithUV(xTop, y + 0, zMidDown, d2, d4);
            tessellator.addVertexWithUV(xTop, y + 1, zMidDown, d2, d3);
            tessellator.addVertexWithUV(xTop, y + 1, zMidUp, d, d3);
            tessellator.addVertexWithUV(xTop, y + 0, zMidUp, d, d4);
            tessellator.addVertexWithUV(xBot, y + 0, zMidUp, d2, d4);
            tessellator.addVertexWithUV(xBot, y + 1, zMidUp, d2, d3);
            if (renderAbove)
            {
                tessellator.addVertexWithUV(xBot, (double)(y + 1) + 0.01D, zMidUp, d6, d9);
                tessellator.addVertexWithUV(xTop, (double)(y + 1) + 0.01D, zMidUp, d6, d7);
                tessellator.addVertexWithUV(xTop, (double)(y + 1) + 0.01D, zMidDown, d5, d7);
                tessellator.addVertexWithUV(xBot, (double)(y + 1) + 0.01D, zMidDown, d5, d9);
                /*tessellator.addVertexWithUV(xTop, (double)(y + 1) + 0.01D, zMidUp, d6, d9);
                tessellator.addVertexWithUV(xBot, (double)(y + 1) + 0.01D, zMidUp, d6, d7);
                tessellator.addVertexWithUV(xBot, (double)(y + 1) + 0.01D, zMidDown, d5, d7);
                tessellator.addVertexWithUV(xTop, (double)(y + 1) + 0.01D, zMidDown, d5, d9);*/
            }
            else
            {
                if (y < l - 1 && iblockaccess.isAirBlock(x - 1, y + 1, z))
                {
                    tessellator.addVertexWithUV(xBot, (double)(y + 1) + 0.01D, zMidUp, d6, d8);
                    tessellator.addVertexWithUV(xMid, (double)(y + 1) + 0.01D, zMidUp, d6, d9);
                    tessellator.addVertexWithUV(xMid, (double)(y + 1) + 0.01D, zMidDown, d5, d9);
                    tessellator.addVertexWithUV(xBot, (double)(y + 1) + 0.01D, zMidDown, d5, d8);
                    /*tessellator.addVertexWithUV(xMid, (double)(y + 1) + 0.01D, zMidUp, d6, d8);
                    tessellator.addVertexWithUV(xBot, (double)(y + 1) + 0.01D, zMidUp, d6, d9);
                    tessellator.addVertexWithUV(xBot, (double)(y + 1) + 0.01D, zMidDown, d5, d9);
                    tessellator.addVertexWithUV(xMid, (double)(y + 1) + 0.01D, zMidDown, d5, d8);*/
                }
                if (y < l - 1 && iblockaccess.isAirBlock(x + 1, y + 1, z))
                {
                    tessellator.addVertexWithUV(xMid, (double)(y + 1) + 0.01D, zMidUp, d6, d7);
                    tessellator.addVertexWithUV(xTop, (double)(y + 1) + 0.01D, zMidUp, d6, d8);
                    tessellator.addVertexWithUV(xTop, (double)(y + 1) + 0.01D, zMidDown, d5, d8);
                    tessellator.addVertexWithUV(xMid, (double)(y + 1) + 0.01D, zMidDown, d5, d7);
                    /*tessellator.addVertexWithUV(xTop, (double)(y + 1) + 0.01D, zMidUp, d6, d7);
                    tessellator.addVertexWithUV(xMid, (double)(y + 1) + 0.01D, zMidUp, d6, d8);
                    tessellator.addVertexWithUV(xMid, (double)(y + 1) + 0.01D, zMidDown, d5, d8);
                    tessellator.addVertexWithUV(xTop, (double)(y + 1) + 0.01D, zMidDown, d5, d7);*/
                }
            }
            if (renderBelow)
            {
                tessellator.addVertexWithUV(xBot, (double)y - 0.01D, zMidUp, d6, d9);
                tessellator.addVertexWithUV(xTop, (double)y - 0.01D, zMidUp, d6, d7);
                tessellator.addVertexWithUV(xTop, (double)y - 0.01D, zMidDown, d5, d7);
                tessellator.addVertexWithUV(xBot, (double)y - 0.01D, zMidDown, d5, d9);
                /*tessellator.addVertexWithUV(xTop, (double)y - 0.01D, zMidUp, d6, d9);
                tessellator.addVertexWithUV(xBot, (double)y - 0.01D, zMidUp, d6, d7);
                tessellator.addVertexWithUV(xBot, (double)y - 0.01D, zMidDown, d5, d7);
                tessellator.addVertexWithUV(xTop, (double)y - 0.01D, zMidDown, d5, d9);*/
            }
            else
            {
                if (y > 1 && iblockaccess.isAirBlock(x - 1, y - 1, z))
                {
                    tessellator.addVertexWithUV(xBot, (double)y - 0.01D, zMidUp, d6, d8);
                    tessellator.addVertexWithUV(xMid, (double)y - 0.01D, zMidUp, d6, d9);
                    tessellator.addVertexWithUV(xMid, (double)y - 0.01D, zMidDown, d5, d9);
                    tessellator.addVertexWithUV(xBot, (double)y - 0.01D, zMidDown, d5, d8);
                    /*tessellator.addVertexWithUV(xMid, (double)y - 0.01D, zMidUp, d6, d8);
                    tessellator.addVertexWithUV(xBot, (double)y - 0.01D, zMidUp, d6, d9);
                    tessellator.addVertexWithUV(xBot, (double)y - 0.01D, zMidDown, d5, d9);
                    tessellator.addVertexWithUV(xMid, (double)y - 0.01D, zMidDown, d5, d8);*/
                }
                if (y > 1 && iblockaccess.isAirBlock(x + 1, y - 1, z))
                {
                    tessellator.addVertexWithUV(xMid, (double)y - 0.01D, zMidUp, d6, d7);
                    tessellator.addVertexWithUV(xTop, (double)y - 0.01D, zMidUp, d6, d8);
                    tessellator.addVertexWithUV(xTop, (double)y - 0.01D, zMidDown, d5, d8);
                    tessellator.addVertexWithUV(xMid, (double)y - 0.01D, zMidDown, d5, d7);
                    /*tessellator.addVertexWithUV(xTop, (double)y - 0.01D, zMidUp, d6, d7);
                    tessellator.addVertexWithUV(xMid, (double)y - 0.01D, zMidUp, d6, d8);
                    tessellator.addVertexWithUV(xMid, (double)y - 0.01D, zMidDown, d5, d8);
                    tessellator.addVertexWithUV(xTop, (double)y - 0.01D, zMidDown, d5, d7);*/
                }
            }
        }
        if ((!west || !east) && (south || north || west || east))
        {
            if (west && !east)
            {
                tessellator.addVertexWithUV(xMidDown, y + 1, zBot, d, d3);
                tessellator.addVertexWithUV(xMidDown, y + 0, zBot, d, d4);
                tessellator.addVertexWithUV(xMidDown, y + 0, zMid, d1, d4);
                tessellator.addVertexWithUV(xMidDown, y + 1, zMid, d1, d3);
                tessellator.addVertexWithUV(xMidUp, y + 1, zMid, d, d3);
                tessellator.addVertexWithUV(xMidUp, y + 0, zMid, d, d4);
                tessellator.addVertexWithUV(xMidUp, y + 0, zBot, d1, d4);
                tessellator.addVertexWithUV(xMidUp, y + 1, zBot, d1, d3);
                if (!north && !south)
                {
                    tessellator.addVertexWithUV(xMidDown, y + 1, zMid, d5, d7);
                    tessellator.addVertexWithUV(xMidDown, y + 0, zMid, d5, d9);
                    tessellator.addVertexWithUV(xMidUp, y + 0, zMid, d6, d9);
                    tessellator.addVertexWithUV(xMidUp, y + 1, zMid, d6, d7);
                    /*tessellator.addVertexWithUV(xMidUp, y + 1, zMid, d5, d7);
                    tessellator.addVertexWithUV(xMidUp, y + 0, zMid, d5, d9);
                    tessellator.addVertexWithUV(xMidDown, y + 0, zMid, d6, d9);
                    tessellator.addVertexWithUV(xMidDown, y + 1, zMid, d6, d7);*/
                }
                if (renderAbove || y < l - 1 && iblockaccess.isAirBlock(x, y + 1, z - 1))
                {
                    tessellator.addVertexWithUV(xMidDown, y + 1, zBot, d6, d7);
                    tessellator.addVertexWithUV(xMidDown, y + 1, zMid, d6, d8);
                    tessellator.addVertexWithUV(xMidUp, y + 1, zMid, d5, d8);
                    tessellator.addVertexWithUV(xMidUp, y + 1, zBot, d5, d7);
                    /*tessellator.addVertexWithUV(xMidDown, y + 1, zMid, d6, d7);
                    tessellator.addVertexWithUV(xMidDown, y + 1, zBot, d6, d8);
                    tessellator.addVertexWithUV(xMidUp, y + 1, zBot, d5, d8);
                    tessellator.addVertexWithUV(xMidUp, y + 1, zMid, d5, d7);*/
                }
                if (renderBelow || y > 1 && iblockaccess.isAirBlock(x, y - 1, z - 1))
                {
                    tessellator.addVertexWithUV(xMidDown, y, zBot, d6, d7);
                    tessellator.addVertexWithUV(xMidDown, y, zMid, d6, d8);
                    tessellator.addVertexWithUV(xMidUp, y, zMid, d5, d8);
                    tessellator.addVertexWithUV(xMidUp, y, zBot, d5, d7);
                    /*tessellator.addVertexWithUV(xMidDown, y, zMid, d6, d7);
                    tessellator.addVertexWithUV(xMidDown, y, zBot, d6, d8);
                    tessellator.addVertexWithUV(xMidUp, y, zBot, d5, d8);
                    tessellator.addVertexWithUV(xMidUp, y, zMid, d5, d7);*/
                }
            }
            else if (!west && east)
            {
                tessellator.addVertexWithUV(xMidDown, y + 1, zMid, d1, d3);
                tessellator.addVertexWithUV(xMidDown, y + 0, zMid, d1, d4);
                tessellator.addVertexWithUV(xMidDown, y + 0, zTop, d2, d4);
                tessellator.addVertexWithUV(xMidDown, y + 1, zTop, d2, d3);
                tessellator.addVertexWithUV(xMidUp, y + 1, zTop, d1, d3);
                tessellator.addVertexWithUV(xMidUp, y + 0, zTop, d1, d4);
                tessellator.addVertexWithUV(xMidUp, y + 0, zMid, d2, d4);
                tessellator.addVertexWithUV(xMidUp, y + 1, zMid, d2, d3);
                if (!north && !south)
                {
                    tessellator.addVertexWithUV(xMidUp, y + 1, zMid, d5, d7);
                    tessellator.addVertexWithUV(xMidUp, y + 0, zMid, d5, d9);
                    tessellator.addVertexWithUV(xMidDown, y + 0, zMid, d6, d9);
                    tessellator.addVertexWithUV(xMidDown, y + 1, zMid, d6, d7);
                    /*tessellator.addVertexWithUV(xMidDown, y + 1, zMid, d5, d7);
                    tessellator.addVertexWithUV(xMidDown, y + 0, zMid, d5, d9);
                    tessellator.addVertexWithUV(xMidUp, y + 0, zMid, d6, d9);
                    tessellator.addVertexWithUV(xMidUp, y + 1, zMid, d6, d7);*/
                }
                if (renderAbove || y < l - 1 && iblockaccess.isAirBlock(x, y + 1, z + 1))
                {
                    tessellator.addVertexWithUV(xMidDown, y + 1, zMid, d5, d8);
                    tessellator.addVertexWithUV(xMidDown, y + 1, zTop, d5, d9);
                    tessellator.addVertexWithUV(xMidUp, y + 1, zTop, d6, d9);
                    tessellator.addVertexWithUV(xMidUp, y + 1, zMid, d6, d8);
                    /*tessellator.addVertexWithUV(xMidDown, y + 1, zTop, d5, d8);
                    tessellator.addVertexWithUV(xMidDown, y + 1, zMid, d5, d9);
                    tessellator.addVertexWithUV(xMidUp, y + 1, zMid, d6, d9);
                    tessellator.addVertexWithUV(xMidUp, y + 1, zTop, d6, d8);*/
                }
                if (renderBelow || y > 1 && iblockaccess.isAirBlock(x, y - 1, z + 1))
                {
                    tessellator.addVertexWithUV(xMidDown, y, zMid, d5, d8);
                    tessellator.addVertexWithUV(xMidDown, y, zTop, d5, d9);
                    tessellator.addVertexWithUV(xMidUp, y, zTop, d6, d9);
                    tessellator.addVertexWithUV(xMidUp, y, zMid, d6, d8);
                    /*tessellator.addVertexWithUV(xMidDown, y, zTop, d5, d8);
                    tessellator.addVertexWithUV(xMidDown, y, zMid, d5, d9);
                    tessellator.addVertexWithUV(xMidUp, y, zMid, d6, d9);
                    tessellator.addVertexWithUV(xMidUp, y, zTop, d6, d8);*/
                }
            }
        }
        else
        {
            tessellator.addVertexWithUV(xMidDown, y + 1, zTop, d, d3);
            tessellator.addVertexWithUV(xMidDown, y + 0, zTop, d, d4);
            tessellator.addVertexWithUV(xMidDown, y + 0, zBot, d2, d4);
            tessellator.addVertexWithUV(xMidDown, y + 1, zBot, d2, d3);
            tessellator.addVertexWithUV(xMidUp, y + 1, zBot, d, d3);
            tessellator.addVertexWithUV(xMidUp, y + 0, zBot, d, d4);
            tessellator.addVertexWithUV(xMidUp, y + 0, zTop, d2, d4);
            tessellator.addVertexWithUV(xMidUp, y + 1, zTop, d2, d3);
            if (renderAbove)
            {
                tessellator.addVertexWithUV(xMidUp, y + 1, zTop, d6, d9);
                tessellator.addVertexWithUV(xMidUp, y + 1, zBot, d6, d7);
                tessellator.addVertexWithUV(xMidDown, y + 1, zBot, d5, d7);
                tessellator.addVertexWithUV(xMidDown, y + 1, zTop, d5, d9);
                /*tessellator.addVertexWithUV(xMidUp, y + 1, zBot, d6, d9);
                tessellator.addVertexWithUV(xMidUp, y + 1, zTop, d6, d7);
                tessellator.addVertexWithUV(xMidDown, y + 1, zTop, d5, d7);
                tessellator.addVertexWithUV(xMidDown, y + 1, zBot, d5, d9);*/
            }
            else
            {
                if (y < l - 1 && iblockaccess.isAirBlock(x, y + 1, z - 1))
                {
                    tessellator.addVertexWithUV(xMidDown, y + 1, zBot, d6, d7);
                    tessellator.addVertexWithUV(xMidDown, y + 1, zMid, d6, d8);
                    tessellator.addVertexWithUV(xMidUp, y + 1, zMid, d5, d8);
                    tessellator.addVertexWithUV(xMidUp, y + 1, zBot, d5, d7);
                    /*tessellator.addVertexWithUV(xMidDown, y + 1, zMid, d6, d7);
                    tessellator.addVertexWithUV(xMidDown, y + 1, zBot, d6, d8);
                    tessellator.addVertexWithUV(xMidUp, y + 1, zBot, d5, d8);
                    tessellator.addVertexWithUV(xMidUp, y + 1, zMid, d5, d7);*/
                }
                if (y < l - 1 && iblockaccess.isAirBlock(x, y + 1, z + 1))
                {
                    tessellator.addVertexWithUV(xMidDown, y + 1, zMid, d5, d8);
                    tessellator.addVertexWithUV(xMidDown, y + 1, zTop, d5, d9);
                    tessellator.addVertexWithUV(xMidUp, y + 1, zTop, d6, d9);
                    tessellator.addVertexWithUV(xMidUp, y + 1, zMid, d6, d8);
                    /*tessellator.addVertexWithUV(xMidDown, y + 1, zTop, d5, d8);
                    tessellator.addVertexWithUV(xMidDown, y + 1, zMid, d5, d9);
                    tessellator.addVertexWithUV(xMidUp, y + 1, zMid, d6, d9);
                    tessellator.addVertexWithUV(xMidUp, y + 1, zTop, d6, d8);*/
                }
            }
            if (renderBelow)
            {
                tessellator.addVertexWithUV(xMidUp, y, zTop, d6, d9);
                tessellator.addVertexWithUV(xMidUp, y, zBot, d6, d7);
                tessellator.addVertexWithUV(xMidDown, y, zBot, d5, d7);
                tessellator.addVertexWithUV(xMidDown, y, zTop, d5, d9);
                tessellator.addVertexWithUV(xMidUp, y, zBot, d6, d9);
                tessellator.addVertexWithUV(xMidUp, y, zTop, d6, d7);
                tessellator.addVertexWithUV(xMidDown, y, zTop, d5, d7);
                tessellator.addVertexWithUV(xMidDown, y, zBot, d5, d9);
            }
            else
            {
                if (y > 1 && iblockaccess.isAirBlock(x, y - 1, z - 1))
                {
                    tessellator.addVertexWithUV(xMidDown, y, zBot, d6, d7);
                    tessellator.addVertexWithUV(xMidDown, y, zMid, d6, d8);
                    tessellator.addVertexWithUV(xMidUp, y, zMid, d5, d8);
                    tessellator.addVertexWithUV(xMidUp, y, zBot, d5, d7);
                    /*tessellator.addVertexWithUV(xMidDown, y, zMid, d6, d7);
                    tessellator.addVertexWithUV(xMidDown, y, zBot, d6, d8);
                    tessellator.addVertexWithUV(xMidUp, y, zBot, d5, d8);
                    tessellator.addVertexWithUV(xMidUp, y, zMid, d5, d7);*/
                }
                if (y > 1 && iblockaccess.isAirBlock(x, y - 1, z + 1))
                {
                    tessellator.addVertexWithUV(xMidDown, y, zMid, d5, d8);
                    tessellator.addVertexWithUV(xMidDown, y, zTop, d5, d9);
                    tessellator.addVertexWithUV(xMidUp, y, zTop, d6, d9);
                    tessellator.addVertexWithUV(xMidUp, y, zMid, d6, d8);
                    /*tessellator.addVertexWithUV(xMidDown, y, zTop, d5, d8);
                    tessellator.addVertexWithUV(xMidDown, y, zMid, d5, d9);
                    tessellator.addVertexWithUV(xMidUp, y, zMid, d6, d9);
                    tessellator.addVertexWithUV(xMidUp, y, zTop, d6, d8);*/
                }
            }
        }
        return true;
	}
	
	public static void RenderPaneInv(RenderBlocks renderblocks, Block block, int i)
	{
		block.setBlockBounds(0.0F, 0.0F, 0.4375F, 1.0F, 1.0F, 0.5625F);
		RenderDo(renderblocks, block, i);
	}
}
