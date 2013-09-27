package org.spigotmc;

import gnu.trove.set.TByteSet;
import gnu.trove.set.hash.TByteHashSet;
import net.minecraft.block.Block;
import net.minecraft.world.World;

public class AntiXray
{

    private static final CustomTimingsHandler update = new CustomTimingsHandler( "xray - update" );
    private static final CustomTimingsHandler obfuscate = new CustomTimingsHandler( "xray - obfuscate" );
    /*========================================================================*/
    // Used to keep track of which blocks to obfuscate
    private final boolean[] obfuscateBlocks = new boolean[ Short.MAX_VALUE ];
    // Used to select a random replacement ore
    private byte[] replacementOres;

    public AntiXray(SpigotWorldConfig config)
    {
        // Set all listed blocks as true to be obfuscated
        for ( int id : config.blocks )
        {
            obfuscateBlocks[id] = true;
        }

        // For every block
        TByteSet blocks = new TByteHashSet();
        for ( int i = 0; i < obfuscateBlocks.length; i++ )
        {
            // If we are obfuscating it
            if ( obfuscateBlocks[i] )
            {
                Block block = Block.blocksList[i];
                // Check it exists and is not a tile entity
                if ( block != null && !block.hasTileEntity() /* isTileEntity */ )
                {
                    // Add it to the set of replacement blocks
                    blocks.add( (byte) i );
                }
            }
        }
        // Bake it to a flat array of replacements
        replacementOres = blocks.toArray();
    }

    /**
     * Starts the timings handler, then updates all blocks within the set radius
     * of the given coordinate, revealing them if they are hidden ores.
     */
    public void updateNearbyBlocks(World world, int x, int y, int z)
    {
        if ( world.spigotConfig.antiXray )
        {
            update.startTiming();
            updateNearbyBlocks( world, x, y, z, 2, false ); // 2 is the radius, we shouldn't change it as that would make it exponentially slower
            update.stopTiming();
        }
    }

    /**
     * Starts the timings handler, and then removes all non exposed ores from
     * the chunk buffer.
     */
    public void obfuscateSync(int chunkX, int chunkY, int bitmask, byte[] buffer, World world)
    {
        if ( world.spigotConfig.antiXray )
        {
            obfuscate.startTiming();
            obfuscate( chunkX, chunkY, bitmask, buffer, world );
            obfuscate.stopTiming();
        }
    }

    /**
     * Removes all non exposed ores from the chunk buffer.
     */
    public void obfuscate(int chunkX, int chunkY, int bitmask, byte[] buffer, World world)
    {
        // If the world is marked as obfuscated
        if ( world.spigotConfig.antiXray )
        {
            // Initial radius to search around for air
            int initialRadius = 1;
            // Which block in the buffer we are looking at, anywhere from 0 to 16^4
            int index = 0;
            // The iterator marking which random ore we should use next
            int randomOre = 0;

            // Chunk corner X and Z blocks
            int startX = chunkX << 4;
            int startZ = chunkY << 4;

            // Chunks can have up to 16 sections
            for ( int i = 0; i < 16; i++ )
            {
                // If the bitmask indicates this chunk is sent...
                if ( ( bitmask & 1 << i ) != 0 )
                {
                    // Work through all blocks in the chunk, y,z,x
                    for ( int y = 0; y < 16; y++ )
                    {
                        for ( int z = 0; z < 16; z++ )
                        {
                            for ( int x = 0; x < 16; x++ )
                            {
                                // For some reason we can get too far ahead of ourselves (concurrent modification on bulk chunks?) so if we do, just abort and move on
                                if ( index >= buffer.length )
                                {
                                    continue;
                                }
                                // Grab the block ID in the buffer.
                                // TODO: extended IDs are not yet supported
                                int blockId = buffer[index] & 0xFF;
                                // Check if the block should be obfuscated
                                if ( obfuscateBlocks[blockId] )
                                {
                                    // TODO: Don't really understand this, but if radius is not 0 and the world isn't loaded, bail out
                                    if ( initialRadius != 0 && !isLoaded( world, startX + x, ( i << 4 ) + y, startZ + z, initialRadius ) )
                                    {
                                        continue;
                                    }
                                    // On the otherhand, if radius is 0, or the nearby blocks are all non air, we can obfuscate
                                    if ( initialRadius == 0 || !hasTransparentBlockAdjacent( world, startX + x, ( i << 4 ) + y, startZ + z, initialRadius ) )
                                    {
                                        switch ( world.spigotConfig.engineMode )
                                        {
                                            case 1:
                                                // Replace with stone
                                                buffer[index] = (byte) Block.stone.blockID;
                                                break;
                                            case 2:
                                                // Replace with random ore.
                                                if ( randomOre >= replacementOres.length )
                                                {
                                                    randomOre = 0;
                                                }
                                                buffer[index] = replacementOres[randomOre++];
                                                break;
                                        }
                                    }
                                }

                                index++;
                            }
                        }
                    }
                }
            }
        }
    }

    private void updateNearbyBlocks(World world, int x, int y, int z, int radius, boolean updateSelf)
    {
        // If the block in question is loaded
        if ( world.blockExists( x, y, z ) )
        {
            // Get block id
            int id = world.getBlockId( x, y, z );

            // See if it needs update
            if ( updateSelf && obfuscateBlocks[id] )
            {
                // Send the update
                world.markBlockForUpdate( x, y, z );
            }

            // Check other blocks for updates
            if ( radius > 0 )
            {
                updateNearbyBlocks( world, x + 1, y, z, radius - 1, true );
                updateNearbyBlocks( world, x - 1, y, z, radius - 1, true );
                updateNearbyBlocks( world, x, y + 1, z, radius - 1, true );
                updateNearbyBlocks( world, x, y - 1, z, radius - 1, true );
                updateNearbyBlocks( world, x, y, z + 1, radius - 1, true );
                updateNearbyBlocks( world, x, y, z - 1, radius - 1, true );
            }
        }
    }

    private static boolean isLoaded(World world, int x, int y, int z, int radius)
    {
        return world.blockExists( x, y, z )
                || ( radius > 0
                && ( isLoaded( world, x + 1, y, z, radius - 1 )
                || isLoaded( world, x - 1, y, z, radius - 1 )
                || isLoaded( world, x, y + 1, z, radius - 1 )
                || isLoaded( world, x, y - 1, z, radius - 1 )
                || isLoaded( world, x, y, z + 1, radius - 1 )
                || isLoaded( world, x, y, z - 1, radius - 1 ) ) );
    }

    private static boolean hasTransparentBlockAdjacent(World world, int x, int y, int z, int radius)
    {
        return !Block.isNormalCube( world.getBlockId( x, y, z ) ) /* isSolidBlock */
                || ( radius > 0
                && ( hasTransparentBlockAdjacent( world, x + 1, y, z, radius - 1 )
                || hasTransparentBlockAdjacent( world, x - 1, y, z, radius - 1 )
                || hasTransparentBlockAdjacent( world, x, y + 1, z, radius - 1 )
                || hasTransparentBlockAdjacent( world, x, y - 1, z, radius - 1 )
                || hasTransparentBlockAdjacent( world, x, y, z + 1, radius - 1 )
                || hasTransparentBlockAdjacent( world, x, y, z - 1, radius - 1 ) ) );
    }
}
