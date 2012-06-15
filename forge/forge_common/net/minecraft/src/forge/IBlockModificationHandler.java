package net.minecraft.src.forge;

public interface IBlockModificationHandler
{
	/**
	 * Allows you to interrupt the destruction of a block at a low level.
	 * (Before the block is physically removed from the Chunk)
	 * Note: Gets called client and server side in SMP.
	 * 
	 * @param x The X coordinate of the Block in question
	 * @param y The Y coordinate of the Block in question
	 * @param z The Z coordinate of the Block in question
	 * @param blockID The BlockID of the Block in question
	 * @param metadata The Metadata of the Block in question
	 * @return True to cancel the destruction of this block.
	 */
	public boolean onBlockDestroy(int x, int y, int z, int blockID, int metadata);
	
	/**
	 * Allows you to interrupt the change of a block at a low level.
	 * (Before the block is physically changed in the Chunk)
	 * Note: Gets called client and server side in SMP.
	 * 
	 * @param x The X coordinate of the Block in question
	 * @param y The Y coordinate of the Block in question
	 * @param z The Z coordinate of the Block in question
	 * @param blockID The BlockID of the Block in question
	 * @param metadata The Metadata of the Block in question
	 * @return True to cancel the change of this block.
	 */
	public boolean onBlockChange(int x, int y, int z, int blockID, int metadata);
	
	/**
	 * Allows you to interrupt the change of a block at a low level.
	 * (Before the block is physically added to the Chunk)
	 * Note: Gets called client and server side in SMP.
	 * 
	 * @param x The X coordinate of the Block in question
	 * @param y The Y coordinate of the Block in question
	 * @param z The Z coordinate of the Block in question
	 * @param blockID The BlockID of the Block in question
	 * @param metadata The Metadata of the Block in question
	 * @return True to cancel the creation of this block.
	 */
	public boolean onBlockCreate(int x, int y, int z, int blockID, int metadata);
}