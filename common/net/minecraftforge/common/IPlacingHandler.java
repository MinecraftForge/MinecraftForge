package net.minecraftforge.common;

public interface IPlacingHandler {
	/**
     * This is called before minecraft or player tries to place any of the blocks that extend
     * BlockFlower.class
     *
     * @param flowerID				ID of the flower that player is trying to place
     * @param plantableID			ID of the block that player tries to place flower on
     * @param plantableMeta			metadata of the block that player tries to place flower on
     * @return
     */
    public boolean canPlantFlower(int flowerID, int plantableID, int plantableMeta);

    /**
     * This is called before minecraft or player tries to place cactus
     *
     * @param plantableID			ID of the block that player tries to place flower on
     * @param plantableMeta			metadata of the block that player tries to place flower on
     * @return
     */

    public boolean canCactusGrowOn(int plantableID, int plantableMeta);

    /**
     * This is called before minecraft or player tries to place reeds
     *
     * @param plantableID			ID of the block that player tries to place flower on
     * @param plantableMeta			metadata of the block that player tries to place flower on
     * @return
     */

    public boolean canReedGrowOn(int plantableID, int plantableMeta);

    /**
     * This is called before minecraft or player tries to place torch or redstone torch(usefull for placing torches on top of fences)
     *
     * @param placeableID			ID of the block that player tries to place flower on
     * @param placeableMeta			metadata of the block that player tries to place flower on
     * @return
     */

    public boolean canPlaceTorchOn(int placeableID, int placeableMeta);
}
