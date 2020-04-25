/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

package net.minecraftforge.common.util;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.storage.WorldSavedData;

public class DummyWorldSaveData extends WorldSavedData {
    public static final DummyWorldSaveData DUMMY = new DummyWorldSaveData();
    private DummyWorldSaveData() {
        super("DUMMYDUMMY \uD83D\uDC4C\uD83D\uDC4C\uD83D\uDC4C");
    }

    @Override
    public void read(final CompoundNBT nbt) {
        // NOOP
    }

    @Override
    public CompoundNBT write(final CompoundNBT compound) {
        // NOOP
        return null;
    }
}
