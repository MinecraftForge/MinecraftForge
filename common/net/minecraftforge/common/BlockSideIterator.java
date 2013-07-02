package net.minecraftforge.common;

public class BlockSideIterator{
	
	private final int x, y, z;
	
	private int currentSide = 0;
	
	public BlockSideIterator(int x, int y, int z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public boolean hasNext(){
		return currentSide < 6;
	}
	
	public int[] next(){
		if(!hasNext()){
			return new int[]{0, 0, 0};
		}
		int[] rtn = null;
		
		switch(currentSide){
		case 0:
			rtn = new int[] {x, y - 1, z};
			break;
		case 1:
			rtn = new int[] {x, y + 1, z};
			break;
		case 2:
			rtn = new int[] {x - 1, y, z};
			break;
		case 3:
			rtn = new int[] {x + 1, y, z};
			break;
		case 4:
			rtn = new int[] {x, y, z - 1};
			break;
		case 5:
			rtn = new int[] {x, y, z + 1};
			break;
		default:
			rtn = new int[] {0, 0, 0};
		}
		
		currentSide++;
		
		return rtn;
	}
	
	public int getSide(){
		return currentSide;
	}
	
	public int getPrevSide(){
		return currentSide - 1;
	}
	
	public BlockSideIterator recreate(){
		return new BlockSideIterator(x, y, z);
	}
	
	public static int[] getLocationFromInt(ForgeDirection dir, int x, int y, int z){
		int[] rtn = null;
		
		int var1 = convertForgeDirToInt(dir);
		
		switch(var1){
		case 0:
			rtn = new int[] {x, y - 1, z};
			break;
		case 1:
			rtn = new int[] {x, y + 1, z};
			break;
		case 2:
			rtn = new int[] {x - 1, y, z};
			break;
		case 3:
			rtn = new int[] {x + 1, y, z};
			break;
		case 4:
			rtn = new int[] {x, y, z - 1};
			break;
		case 5:
			rtn = new int[] {x, y, z + 1};
			break;
		default:
			rtn = new int[] {0, 0, 0};
		}
		
		return rtn;
	}
	
	public static int[] getLocationFromInt(int par1, int x, int y, int z){
		int[] rtn = null;
		
		switch(par1){
		case 0:
			rtn = new int[] {x, y - 1, z};
			break;
		case 1:
			rtn = new int[] {x, y + 1, z};
			break;
		case 2:
			rtn = new int[] {x - 1, y, z};
			break;
		case 3:
			rtn = new int[] {x + 1, y, z};
			break;
		case 4:
			rtn = new int[] {x, y, z - 1};
			break;
		case 5:
			rtn = new int[] {x, y, z + 1};
			break;
		default:
			rtn = new int[] {0, 0, 0};
		}
		
		return rtn;
	}
	
	public static int convertForgeDirToInt(ForgeDirection dir){
		switch(dir){
		case DOWN:
			return 0;
		case UP:
			return 1;
		case WEST:
			return 2;
		case EAST:
			return 3;
		case SOUTH:
			return 4;
		case NORTH:
			return 5;
		case UNKNOWN:
			return -1;
		}
		return -1;
	}
}
