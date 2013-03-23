package net.minecraftforge.client.renderer;

import java.util.Comparator;

public class TriComparator implements Comparator<Integer> {
	private float mPlayerX;
	private float mPlayerY;
	private float mPlayerZ;
	
	private int[] mBuffer;
	
	public TriComparator(int[] buffer, float playerX, float playerY, float playerZ)
	{
		mBuffer = buffer;
		mPlayerX = playerX;
		mPlayerY = playerY;
		mPlayerZ = playerZ;
	}

	@Override
	public int compare(Integer left, Integer right) {
		float leftVert1X = Float.intBitsToFloat(mBuffer[left]) - mPlayerX;
		float leftVert1Y = Float.intBitsToFloat(mBuffer[left+1]) - mPlayerY;
		float leftVert1Z = Float.intBitsToFloat(mBuffer[left+2]) - mPlayerZ;
		float leftVert2X = Float.intBitsToFloat(mBuffer[left+8]) - mPlayerX;
		float leftVert2Y = Float.intBitsToFloat(mBuffer[left+9]) - mPlayerY;
		float leftVert2Z = Float.intBitsToFloat(mBuffer[left+10]) - mPlayerZ;
		float leftVert3X = Float.intBitsToFloat(mBuffer[left+16]) - mPlayerX;
		float leftVert3Y = Float.intBitsToFloat(mBuffer[left+17]) - mPlayerY;
		float leftVert3Z = Float.intBitsToFloat(mBuffer[left+18]) - mPlayerZ;
		
		float rightVert1X = Float.intBitsToFloat(mBuffer[right]) - mPlayerX;
		float rightVert1Y = Float.intBitsToFloat(mBuffer[right+1]) - mPlayerY;
		float rightVert1Z = Float.intBitsToFloat(mBuffer[right+2]) - mPlayerZ;
		float rightVert2X = Float.intBitsToFloat(mBuffer[right+8]) - mPlayerX;
		float rightVert2Y = Float.intBitsToFloat(mBuffer[right+9]) - mPlayerY;
		float rightVert2Z = Float.intBitsToFloat(mBuffer[right+10]) - mPlayerZ;
		float rightVert3X = Float.intBitsToFloat(mBuffer[right+16]) - mPlayerX;
		float rightVert3Y = Float.intBitsToFloat(mBuffer[right+17]) - mPlayerY;
		float rightVert3Z = Float.intBitsToFloat(mBuffer[right+18]) - mPlayerZ;
		
		float leftScore = leftVert1X * leftVert1X + leftVert1Y * leftVert1Y + leftVert1Z * leftVert1Z + 
				leftVert2X * leftVert2X + leftVert2Y * leftVert2Y + leftVert2Z * leftVert2Z + 
				leftVert3X * leftVert3X + leftVert3Y * leftVert3Y + leftVert3Z * leftVert3Z;
		float rightScore = rightVert1X * rightVert1X + rightVert1Y * rightVert1Y + rightVert1Z * rightVert1Z + 
				rightVert2X * rightVert2X + rightVert2Y * rightVert2Y + rightVert2Z * rightVert2Z + 
				rightVert3X * rightVert3X + rightVert3Y * rightVert3Y + rightVert3Z * rightVert3Z;
		
		return Float.compare(rightScore, leftScore);
	}
}
