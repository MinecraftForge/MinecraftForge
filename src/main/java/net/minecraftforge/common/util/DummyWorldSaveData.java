package net.minecraftforge.common.util;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.storage.WorldSavedData;

public class DummyWorldSaveData extends WorldSavedData {
    public static final DummyWorldSaveData DUMMY = new DummyWorldSaveData();
    private DummyWorldSaveData() {
        super("DUMMYDUMMY \uD83D\uDC4C\uD83D\uDC4C\uD83D\uDC4C");
    }

    @Override
    public void read(final NBTTagCompound nbt) {
        // NOOP
    }

    @Override
    public NBTTagCompound write(final NBTTagCompound compound) {
        // NOOP
        return null;
    }
}
