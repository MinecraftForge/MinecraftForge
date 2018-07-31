package net.minecraftforge.event.world;

public class FoundChunksForSpawningEventCreatureTypeData
{
    private final int k4;
    private final int l4;

    public FoundChunksForSpawningEventCreatureTypeData()
    {
        k4 = -1;
        l4 = -1;
    }

    public FoundChunksForSpawningEventCreatureTypeData(int k4In, int l4In)
    {
        k4 = k4In;
        l4 = l4In;
    }

    /**
     * The number of creatures of the given type that currently reside in the eligible spawn chunks. A value of -1 indicates that no attempt was made to calculate this number.
     */
    public int getk4()
    {
        return k4;
    }

    /**
     * The maximum number of creatures of the given type that can reside in the eligible spawn chunks before the game stops attempting to spawn more. A value of -1 indicates that
     * no attempt was made to calculate this number.
     */
    public int getl4()
    {
        return l4;
    }
}
