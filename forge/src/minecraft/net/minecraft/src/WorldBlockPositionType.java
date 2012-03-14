package net.minecraft.src;

class WorldBlockPositionType
{
    int posX;
    int posY;
    int posZ;

    /**
     * Counts down 80 ticks until the position is accepted from the receive queue into the world.
     */
    int acceptCountdown;
    int blockID;
    int metadata;
}
