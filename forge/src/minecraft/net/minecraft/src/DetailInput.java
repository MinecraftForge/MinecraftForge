package net.minecraft.src;

public class DetailInput {

	private int blockIDIn;
	private int metadataIn;
	private int blockIDOut;
	private int metadataOut;
	
	public DetailInput(int bIDin, int mdIn, int bIDout, int mdOut) {
		blockIDIn = bIDin;
		metadataIn = mdIn;
		blockIDOut = bIDout;
		metadataOut = mdOut;
	}
	
	public int getBlockID() {
		return blockIDIn;
	}
	
	public int getMetadata() {
		return metadataIn;
	}
	
	public int getReplacementID() {
		return blockIDOut;
	}
	
	public int getReplacementMetadata() {
		return metadataOut;
	}
}
