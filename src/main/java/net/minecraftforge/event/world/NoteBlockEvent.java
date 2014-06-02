package net.minecraftforge.event.world;

import cpw.mods.fml.common.eventhandler.Cancelable;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;

/**
 * NoteBlock Events
 * 
 * @author SoniEx2
 * 
 */
public class NoteBlockEvent extends BlockEvent {
    /**
     * NoteBlock Instruments. UNKNOWN should be used for custom instruments.
     * 
     * @author SoniEx2
     * 
     */
    public enum Instrument {
        PIANO, BASSDRUM, SNARE, CLICKS, BASSGUITAR, UNKNOWN;

        private static final Instrument[] cache = values();

        public static Instrument getInstrument(byte id)
        {
            if (id < 0 || id >= cache.length - 1)
            {
                return UNKNOWN;
            }
            else
            {
                return cache[id];
            }
        }
    }

    public final byte note;
    public final byte instrument;

    /**
     * Construct a new NoteBlock event.
     * 
     * @param x
     * @param y
     * @param z
     * @param world
     * @param block
     * @param blockMetadata
     * @param note
     *            0 is F#3, -33 is A0, 54 is C8. Minecraft's 2-octave limit is
     *            0-24, or F#3-F#5.
     * @param instrument
     */
    public NoteBlockEvent(int x, int y, int z, World world, Block block, int blockMetadata, byte note, byte instrument)
    {
        super(x, y, z, world, block, blockMetadata);
        this.note = note;
        this.instrument = instrument;
    }

    /**
     * NoteBlock Play Event. Triggers when a NoteBlock plays.
     * 
     * @author SoniEx2
     * 
     */
    @Cancelable
    public static class Play extends NoteBlockEvent {
        /**
         * Construct a new NoteBlock Play event.
         * 
         * @param x
         * @param y
         * @param z
         * @param world
         * @param block
         * @param blockMetadata
         * @param note
         *            0 is F#3, -33 is A0, 54 is C8. Minecraft's 2-octave limit
         *            is 0-24, or F#3-F#5.
         * @param instrument
         */
        public Play(int x, int y, int z, World world, Block block, int blockMetadata, byte note, byte instrument)
        {
            super(x, y, z, world, block, blockMetadata, note, instrument);
        }

        /**
         * Construct a new NoteBlockEvent. Let Forge guess the block.
         * 
         * @param x
         * @param y
         * @param z
         * @param world
         * @param note
         *            0 is F#3, -33 is A0, 54 is C8. Minecraft's 2-octave limit
         *            is 0-24, or F#3-F#5.
         * @param instrument
         */
        public Play(int x, int y, int z, World world, byte note, byte instrument)
        {
            this(x, y, z, world, world.getBlock(x, y, z), world.getBlockMetadata(x, y, z), note, instrument);
        }
    }

    /**
     * NoteBlock Tune event. Triggers when a TileEntityNote is tuned.
     * 
     * @author SoniEx2
     * 
     */
    @Cancelable
    public static class Tune extends NoteBlockEvent {
        public final byte oldNote;

        /**
         * Construct a new NoteBlock Tune event.
         * 
         * @param x
         * @param y
         * @param z
         * @param world
         * @param block
         * @param blockMetadata
         * @param note
         *            0 is F#3, -33 is A0, 54 is C8. Minecraft's 2-octave limit
         *            is 0-24, or F#3-F#5.
         * @param instrument
         * @param oldNote
         */
        public Tune(int x, int y, int z, World world, Block block, int blockMetadata, byte note, byte instrument, byte oldNote)
        {
            super(x, y, z, world, block, blockMetadata, note, instrument);
            this.oldNote = oldNote;
        }

        private static final byte getInstrument(World world, int x, int y, int z)
        {
            Material material = world.getBlock(x, y - 1, z).getMaterial();
            byte b0 = 0;

            if (material == Material.rock)
            {
                b0 = 1;
            }

            if (material == Material.sand)
            {
                b0 = 2;
            }

            if (material == Material.glass)
            {
                b0 = 3;
            }

            if (material == Material.wood)
            {
                b0 = 4;
            }
            return b0;
        }

        /**
         * Construct a new NoteBlock Tune event. Let Forge guess the block and
         * instrument.
         * 
         * @param x
         * @param y
         * @param z
         * @param world
         * @param note
         *            0 is F#3, -33 is A0, 54 is C8. Minecraft's 2-octave limit
         *            is 0-24, or F#3-F#5.
         * @param oldNote
         */
        public Tune(int x, int y, int z, World world, byte note, byte oldNote)
        {
            super(x, y, z, world, world.getBlock(x, y, z), world.getBlockMetadata(x, y, z), note, getInstrument(world, x, y, z));
            this.oldNote = oldNote;
        }
    }

}
