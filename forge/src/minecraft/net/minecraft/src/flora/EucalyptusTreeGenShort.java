package net.minecraft.src.flora;

import java.util.Random;
import net.minecraft.src.*;

public class EucalyptusTreeGenShort extends WorldGenerator
{
	private int mdWood;
	private int mdLeaves;

	public EucalyptusTreeGenShort(int i, int j)
	{
		mdWood = i;
		mdLeaves = j;
	}

	public boolean generate(World world, Random random, int i, int j, int k)
	{
		int l = findGround(world, i, j, k);
		generateRandomTree(world, random, i, l, k);
		return true;
	}

	int findGround(World world, int i, int j, int k)
	{
		int l = 0;
		int i1 = world.getBlockId(i, j - 1, k);
		if (!Block.opaqueCubeLookup[world.getBlockId(i, j, k)] && (i1 == Block.grass.blockID || i1 == Block.dirt.blockID))
		{
			return j;
		}
		int k1 = 96;
		do
		{
			if (k1 < 32)
			{
				break;
			}
			int j1 = world.getBlockId(i, k1, k);
			if ((j1 == Block.grass.blockID || j1 == Block.dirt.blockID) && !Block.opaqueCubeLookup[world.getBlockId(i, k1 + 1, k)])
			{
				l = k1 + 1;
				break;
			}
			k1--;
		}
		while (true);
		return l;
	}

	public boolean generateRandomTree(World world, Random random, int posX, int posY, int posZ)
	{
		int height = random.nextInt(3) + 6; //Height
		boolean flag = true;
		if (posY < 1 || posY + height + 1 > 256)
		{
			return false;
		}
		for (int i1 = posY; i1 <= posY + 1 + height; i1++)
		{
			byte byte0 = 1;
			if (i1 == posY)
			{
				byte0 = 0;
			}
			if (i1 >= (posY + 1 + height) - 2)
			{
				byte0 = 2;
			}
			label0:
			for (int l1 = posX - byte0; l1 <= posX + byte0 && flag; l1++)
			{
				int j2 = posZ - byte0;
				do
				{
					if (j2 > posZ + byte0 || !flag)
					{
						continue label0;
					}
					if (i1 >= 0 && i1 < 256)
					{
						int k2 = world.getBlockId(l1, i1, j2);
						if (k2 != 0 && k2 != mod_FloraSoma.floraLeaves.blockID)
						{
							flag = false;
							continue label0;
						}
					}
					else
					{
						flag = false;
						continue label0;
					}
					j2++;
				}
				while (true);
			}
		}

		if (!flag)
		{
			return false;
		}
		int j1 = world.getBlockId(posX, posY - 1, posZ);
		if (j1 != Block.grass.blockID && j1 != Block.dirt.blockID || posY >= 256 - height - 1)
		{
			return false;
		}
		world.setBlock(posX, posY - 1, posZ, Block.dirt.blockID);
		for (int k1 = 0; k1 < height; k1++)
		{
			int i2 = world.getBlockId(posX, posY + k1, posZ);
			if (i2 == 0 || i2 == mod_FloraSoma.floraLeaves.blockID)
			{
				setBlockAndMetadata(world, posX, posY + k1, posZ, mod_FloraSoma.redwood.blockID, mdWood);
			}
		}

		genBranch(world, random, posX, posY, posZ, height, 1);
		genBranch(world, random, posX, posY, posZ, height, 2);
		genBranch(world, random, posX, posY, posZ, height, 3);
		genBranch(world, random, posX, posY, posZ, height, 4);
		genStraightBranch(world, random, posX, posY, posZ, height, 1);
		genStraightBranch(world, random, posX, posY, posZ, height, 2);
		genStraightBranch(world, random, posX, posY, posZ, height, 3);
		genStraightBranch(world, random, posX, posY, posZ, height, 4);
		generateNode(world, random, posX, posY + height, posZ);
		return true;
	}

	private void genBranch(World world, Random random, int x, int y, int z, int height, int direction)
	{
		int posX = x;
		int posY = y + height - 3;
		int posZ = z;
		byte byte0 = 0;
		byte byte1 = 0;
		switch (direction)
		{
			case 1:
				byte0 = 1;
				byte1 = 1;
				break;

			case 2:
				byte0 = -1;
				byte1 = 1;
				break;

			case 3:
				byte0 = 1;
				byte1 = -1;
				break;

			case 4:
				byte0 = -1;
				byte1 = -1;
				break;
		}
		int heightShift = random.nextInt(6);
		for (int bIter = 4; bIter > 0; bIter--)
		{
			if (heightShift % 3 != 0)
			{
				posX += byte0;
			}
			if (heightShift % 3 != 1)
			{
				posZ += byte1;
			}
			int branch = heightShift % 3;
			posY += branch;
			if(branch == 2)
				setBlockAndMetadata(world, posX, posY-1, posZ, mod_FloraSoma.redwood.blockID, mdWood);
			setBlockAndMetadata(world, posX, posY, posZ, mod_FloraSoma.redwood.blockID, mdWood);
			if(bIter == 1)
				generateNode(world, random, posX, posY, posZ);
			heightShift = random.nextInt(6);
		}
	}

	private void genStraightBranch(World world, Random random, int x, int y, int z, int height, int direction)
	{
		int posX = x;
		int posY = y + height - 3;
		int posZ = z;
		byte xShift = 0;
		byte zShift = 0;
		switch (direction)
		{
			case 1:
				xShift = 1;
				zShift = 0;
				break;

			case 2:
				xShift = 0;
				zShift = 1;
				break;

			case 3:
				xShift = -1;
				zShift = 0;
				break;

			case 4:
				xShift = 0;
				zShift = -1;
				break;
		}
		int heightShift = random.nextInt(6);
		for (int j2 = 4; j2 > 0; j2--)
		{
			if (xShift == 0)
			{
				posX = (posX + random.nextInt(3)) - 1;
				posZ += zShift;
			}
			if (zShift == 0)
			{
				posX += xShift;
				posZ = (posZ + random.nextInt(3)) - 1;
			}
			int branch = heightShift % 3;
			posY += branch;
			if(branch == 2)
				setBlockAndMetadata(world, posX, posY-1, posZ, mod_FloraSoma.redwood.blockID, mdWood);
			setBlockAndMetadata(world, posX, posY, posZ, mod_FloraSoma.redwood.blockID, mdWood);
			if(j2 == 1)
				generateNode(world, random, posX, posY, posZ);
			heightShift = random.nextInt(6);
		}
	}

	public boolean generateNode(World world, Random random, int x, int y, int z)
	{
		setBlockAndMetadata(world, x, y, z, mod_FloraSoma.redwood.blockID, mdWood);
		for (int xIter = x - 2; xIter <= x + 2; xIter++)
		{
			for (int zIter = z - 1; zIter <= z + 1; zIter++)
			{
				int bID = world.getBlockId(xIter, y, zIter);
				if (bID != mod_FloraSoma.floraLeaves.blockID && !Block.opaqueCubeLookup[bID])
				{	
					setBlockAndMetadata(world, xIter, y, zIter, mod_FloraSoma.floraLeaves.blockID, mdLeaves);
				}
			}
		}

		for (int xIter = x - 1; xIter <= x + 1; xIter++)
		{
			for (int zIter = z - 2; zIter <= z + 2; zIter++)
			{
				int bID = world.getBlockId(xIter, y, zIter);
				if (bID != mod_FloraSoma.floraLeaves.blockID && !Block.opaqueCubeLookup[bID])
				{	
					setBlockAndMetadata(world, xIter, y, zIter, mod_FloraSoma.floraLeaves.blockID, mdLeaves);
				}
			}
		}
		
		for (int xIter = x - 1; xIter <= x + 1; xIter++)
		{
			for (int zIter = z - 1; zIter <= z + 1; zIter++)
			{
				int bID = world.getBlockId(xIter, y+1, zIter);
				if (bID != mod_FloraSoma.floraLeaves.blockID && !Block.opaqueCubeLookup[bID])
				{	
					setBlockAndMetadata(world, xIter, y+1, zIter, mod_FloraSoma.floraLeaves.blockID, mdLeaves);
				}
			}
		}

		return true;
	}
}
