/*
 * Minecraft Forge
 * Copyright (c) 2016.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.event.world;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

import com.google.common.base.Preconditions;

/**
 * Base class for Noteblock Events
 *
 */
public class NoteBlockEvent extends BlockEvent
{
    private int noteId;

    protected NoteBlockEvent(World world, BlockPos pos, IBlockState state, int note)
    {
        super(world, pos, state);
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
     * Fired when a Noteblock plays it's note. You can override the note and instrument
     * Canceling this event will stop the note from playing.
     */
    @Cancelable
    public static class Play extends NoteBlockEvent
    {
        private Instrument instrument;

        public Play(World world, BlockPos pos, IBlockState state, int note, int instrument)
        {
            super(world, pos, state, note);
            this.setInstrument(Instrument.fromId(instrument));
        }

        public Instrument getInstrument()
        {
            return instrument;
        }

        public void setInstrument(Instrument instrument)
        {
            this.instrument = instrument;
        }
    }

    /**
     * Fired when a Noteblock is changed. You can adjust the note it will change to via {@link #setNote(Note, Octave)}.
     * Canceling this event will not change the note and also stop the Noteblock from playing it's note.
     */
    @Cancelable
    public static class Change extends NoteBlockEvent
    {
        private final Note oldNote;
        private final Octave oldOctave;

        public Change(World world, BlockPos pos, IBlockState state, int oldNote, int newNote)
        {
            super(world, pos, state, newNote);
            this.oldNote = Note.fromId(oldNote);
            this.oldOctave = Octave.fromId(oldNote);
        }

        public Note getOldNote()
        {
            return oldNote;
        }

        public Octave getOldOctave()
        {
            return oldOctave;
        }
    }

    /**
     * Describes the types of musical Instruments that can be played by a Noteblock.
     * The Instrument being played can be overridden with {@link NoteBlockEvent.Play#setInstrument(Instrument)}
     */
    public static enum Instrument
    {
        PIANO,
        BASSDRUM,
        SNARE,
        CLICKS,
        BASSGUITAR,
        FLUTE,
        BELL,
        GUITAR,
        CHIME,
        XYLOPHONE;

        // cache to avoid creating a new array every time
        private static final Instrument[] values = values();

        static Instrument fromId(int id)
        {
            return id < 0 || id >= values.length ? PIANO : values[id];
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