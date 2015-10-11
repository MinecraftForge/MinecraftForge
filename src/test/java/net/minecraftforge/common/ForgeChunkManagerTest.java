package net.minecraftforge.common;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import scala.actors.threadpool.Arrays;

public class ForgeChunkManagerTest
{
    @Before
    public void setUp() throws Exception
    {
    }

    @After
    public void tearDown() throws Exception
    {
    }

    @Test
    public void testPackedChunkCoordIntPair()
    {
        int[] data = new int[]{0, -1, 1, -2, 2, -3, 3, Integer.MIN_VALUE, Integer.MAX_VALUE};
        for(int x = 0; x < data.length; x++)
        {
            for(int z = 0; z < data.length; z++)
            {
                long packed = ForgeChunkManager.packedChunkCoordIntPair(x, z);
                assertThat(ForgeChunkManager.packedChunkCoordX(packed), is(x));
                assertThat(ForgeChunkManager.packedChunkCoordZ(packed), is(z));
            }
        }
    }
}
