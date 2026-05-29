/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.event.level;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import com.google.common.base.Preconditions;
import net.minecraftsingularity.eventbus.api.bus.CancellableEventBus;
import net.minecraftsingularity.eventbus.api.bus.EventBus;
import net.minecraftsingularity.eventbus.api.event.InheritableEvent;
import net.minecraftsingularity.eventbus.api.event.characteristic.Cancellable;

/**
 * Base class for Noteblock Events
 */
public sealed abstract class NoteBlockEvent implements InheritableEvent, BlockEvent {
    public static final EventBus<NoteBlockEvent> BUS = EventBus.create(NoteBlockEvent.class);

    private final Level world;
    private final BlockPos pos;
    private final BlockState state;

    private int noteId;

    protected NoteBlockEvent(Level world, BlockPos pos, BlockState state, int note) {
        this.world = world;
        this.pos = pos;
        this.state = state;
        this.noteId = note;
    }

    @Override
    public LevelAccessor getLevel() {
        return world;
    }

    @Override
    public BlockPos getPos() {
        return pos;
    }

    @Override
    public BlockState getState() {
        return state;
    }

    /**
     * Get the Note the Noteblock is tuned to
     * @return the Note
     */
    public Note getNote() {
        return Note.fromId(noteId);
    }

    /**
     * Get the Octave of the note this Noteblock is tuned to
     * @return the Octave
     */
    public Octave getOctave() {
        return Octave.fromId(noteId);
    }

    /**
     * get the vanilla note-id, which contains information about both Note and Octave. Most modders should not need this.
     * @return an ID for the note
     */
    public int getVanillaNoteId() {
        return noteId;
    }

    /**
     * Set Note and Octave for this event.<br>
     * If octave is Octave.HIGH, note may only be Note.F_SHARP
     * @param note the Note
     * @param octave the Octave
     */
    public void setNote(Note note, Octave octave) {
        Preconditions.checkArgument(octave != Octave.HIGH || note == Note.F_SHARP, "Octave.HIGH is only valid for Note.F_SHARP!");
        this.noteId = note.ordinal() + octave.ordinal() * 12;
    }

    /**
     * Fired when a Noteblock plays it's note. You can override the note and instrument
     * Canceling this event will stop the note from playing.
     */
    public static final class Play extends NoteBlockEvent implements Cancellable {
        public static final CancellableEventBus<Play> BUS = CancellableEventBus.create(Play.class);

        private NoteBlockInstrument instrument;

        public Play(Level world, BlockPos pos, BlockState state, int note, NoteBlockInstrument instrument) {
            super(world, pos, state, note);
            this.instrument = instrument;
        }

        public NoteBlockInstrument getInstrument() {
            return instrument;
        }

        public void setInstrument(NoteBlockInstrument instrument) {
            this.instrument = instrument;
        }
    }

    /**
     * Fired when a Noteblock is changed. You can adjust the note it will change to via {@link #setNote(Note, Octave)}.
     * Canceling this event will not change the note and also stop the Noteblock from playing it's note.
     */
    public static final class Change extends NoteBlockEvent implements Cancellable {
        public static final CancellableEventBus<Change> BUS = CancellableEventBus.create(Change.class);

        private final Note oldNote;
        private final Octave oldOctave;

        public Change(Level world, BlockPos pos, BlockState state, int oldNote, int newNote) {
            super(world, pos, state, newNote);
            this.oldNote = Note.fromId(oldNote);
            this.oldOctave = Octave.fromId(oldNote);
        }

        public Note getOldNote() {
            return oldNote;
        }

        public Octave getOldOctave() {
            return oldOctave;
        }
    }

    /**
     * Information about the pitch of a Noteblock note.
     * For altered notes such as G-Sharp / A-Flat the Sharp variant is used here.
     *
     */
    public enum Note {
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

        static Note fromId(int id) {
            return values[id % 12];
        }
    }

    /**
     * Describes the Octave of a Note being played by a Noteblock.
     * Together with {@link Note} it fully describes the note.
     *
     */
    public enum Octave {
        LOW,
        MID,
        HIGH; // only valid for F_SHARP

        static Octave fromId(int id) {
            return id < 12 ? LOW : id == 24 ? HIGH : MID;
        }
    }
}
