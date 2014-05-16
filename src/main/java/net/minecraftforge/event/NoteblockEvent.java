package net.minecraftforge.event;

import com.google.common.base.Preconditions;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;

/**
 * Base class for Noteblock Events
 * @author diesieben07
 *
 */
public class NoteblockEvent extends BlockEvent
{

    private int noteId;

    NoteblockEvent(int note, int x, int y, int z, World world, int blockMetadata)
    {
        super(x, y, z, world, Blocks.noteblock, blockMetadata);
        this.noteId = note;
    }
    
    /**
     * Get the Note the Noteblock is tuned to
     * @return the Note
     */
    public Note getNote()
    {
        return Note.fromId(noteId);
    }

    /**
     * Get the Octave of the note this Noteblock is tuned to 
     * @return the Octave
     */
    public Octave getOctave()
    {
        return Octave.fromId(noteId);
    }

    /**
     * get the vanilla note-id, which contains information about both Note and Octave. Most modders should not need this.
     * @return an ID for the note
     */
    public int getVanillaNoteId()
    {
        return noteId;
    }

    /**
     * Set Note and Octave for this event.<br>
     * If octave is Octave.HIGH, note may only be Note.F_SHARP 
     * @param note the Note
     * @param octave the Octave
     */
    public void setNote(Note note, Octave octave)
    {
        Preconditions.checkArgument(octave != Octave.HIGH || note == Note.F_SHARP, "Octave.HIGH is only valid for Note.F_SHARP!");
        this.noteId = note.ordinal() + octave.ordinal() * 12;
    }

    /**
     * Fired when a Noteblock plays it's note. You can override the note and instrument with {@link #setNote(Note, Octave)} and {@link #setInstrument(Instrument)}.
     * Canceling this event will stop the note from playing.
     *
     */
    @Cancelable
    public static class Play extends NoteblockEvent
    {
        
        private int instrumentId;

        Play(int instrument, int note, int x, int y, int z, World world, int blockMetadata)
        {
            super(note, x, y, z, world, blockMetadata);
            this.instrumentId = instrument;
        }
        
        /**
         * get the Instrument the Noteblock is about to play. This is based on the material below the noteblock.
         * @return the Instrument being played
         */
        public Instrument getInstrument()
        {
            return Instrument.fromId(instrumentId);
        }
        
        /**
         * Override the Instrument to be played by the Noteblock
         * @param instrument the Instrument
         */
        public void setInstrument(Instrument instrument)
        {
            this.instrumentId = instrument.ordinal();
        }
        
        /**
         * get the vanilla id for the instrument. Most modders should not need this.
         * @return an ID for the instrument
         */
        public int getVanillaInstrumentId()
        {
            return instrumentId;
        }

    }
    
    /**
     * Fired when a Noteblock is changed. You can adjust the note it will change to via {@link #setNote(Note, Octave)}.
     * Canceling this event will not change the note and also stop the Noteblock from playing it's note.
     *
     */
    @Cancelable
    public static class Change extends NoteblockEvent
    {

        private final Note oldNote;
        private final Octave oldOctave;
        
        Change(int oldNote, int newNote, int x, int y, int z, World world, int blockMetadata)
        {
            super(newNote, x, y, z, world, blockMetadata);
            this.oldNote = Note.fromId(oldNote);
            this.oldOctave = Octave.fromId(oldNote);
        }
        
        /**
         * Get the Note that the Noteblock had before the change
         * @return the old Note
         */
        public Note getOldNote()
        {
            return oldNote;
        }
        
        /**
         * Get the Octave that the Noteblock had before the change
         * @return the old octave
         */
        public Octave getOldOctave()
        {
            return oldOctave;
        }
        
    }
    
    /**
     * Describes the types of musical Instruments that can be played by a Noteblock.
     * The Instrument being played can be overridden with {@link NoteblockEvent.Play#setInstrument(Instrument)}
     *
     */
    public static enum Instrument
    {
        
        PIANO,
        BASSDRUM,
        SNARE,
        CLICKS,
        BASSGUITAR;
        
        // cache to avoid creating a new array every time
        private static final Instrument[] values = values();
        
        static Instrument fromId(int id)
        {
            return id < 0 || id > 4 ? PIANO : values[id];
        }
        
    }
    
    /**
     * Information about the pitch of a Noteblock note.
     * For altered notes such as G-Sharp / A-Flat the Sharp variant is used here. 
     *
     */
    public static enum Note
    {
        F_SHARP,
        G,
        G_SHARP,
        A,
        A_SHARP,
        B,
        C,
        C_SHARP,
        D,
        D_SHARP,
        E,
        F;
        
        private static final Note[] values = values();
        
        static Note fromId(int id)
        {
            return values[id % 12];
        }
        
    }
    
    /**
     * Describes the Octave of a Note being played by a Noteblock.
     * Together with {@link Note} it fully describes the note.
     *
     */
    public static enum Octave
    {
        LOW,
        MID,
        HIGH; // only valid for F_SHARP
        
        static Octave fromId(int id)
        {
            return id < 12 ? LOW : id == 24 ? HIGH : MID;
        }
    }

}
