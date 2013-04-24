package net.minecraftforge.client.renderer;

public class TessellatorState {
	private int[] mRawBuffer;
	private int mRawBufferIndex;
	private int mRawBufferSize;
	
	private int mVertexCount;
	
	private boolean mHasTexture;
	private boolean mHasBrightness;
	private boolean mHasNormal;
	private boolean mHasColor;
	
	public TessellatorState(int[] buffer, int bufferIndex, int bufferSize, int vertexCount, boolean hasTexture, boolean hasBrightness, boolean hasNormal, boolean hasColor)
	{
		mRawBuffer = buffer;
		mRawBufferIndex = bufferIndex;
		mRawBufferSize = bufferSize;
		mVertexCount = vertexCount;
		mHasTexture = hasTexture;
		mHasBrightness = hasBrightness;
		mHasNormal = hasNormal;
		mHasColor = hasColor;
	}
	
	public int[] getRawBuffer() { return mRawBuffer; }
	public int getRawBufferIndex() { return mRawBufferIndex; }
	public int getRawBufferSize() { return mRawBufferSize; }
	public int getVertexCount() { return mVertexCount; }
	public boolean getHasTexture() { return mHasTexture; }
	public boolean getHasBrightness() { return mHasBrightness; }
	public boolean getHasNormal() { return mHasNormal; }
	public boolean getHasColor() { return mHasColor; }
}
