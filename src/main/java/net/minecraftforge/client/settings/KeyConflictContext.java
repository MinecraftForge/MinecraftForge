/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

package net.minecraftforge.client.settings;

import net.minecraft.client.Minecraft;

public enum KeyConflictContext implements IKeyConflictContext
{
    /**
     * Universal key bindings are used in every context and will conflict with any other context.
     * Key Bindings are universal by default.
     */
    UNIVERSAL {
        @Override
        public boolean isActive()
        {
            return true;
        }

        @Override
        public boolean conflicts(IKeyConflictContext other)
        {
            return true;
        }
    },

    /**
     * Gui key bindings are only used when a {@link GuiScreen} is open.
     */
    GUI {
        @Override
        public boolean isActive()
        {
            return Minecraft.getInstance().screen != null;
        }

        @Override
        public boolean conflicts(IKeyConflictContext other)
        {
            return this == other;
        }
    },

    /**
     * In-game key bindings are only used when a {@link GuiScreen} is not open.
     */
    IN_GAME {
        @Override
        public boolean isActive()
        {
            return !GUI.isActive();
        }

        @Override
        public boolean conflicts(IKeyConflictContext other)
        {
            return this == other;
        }
    }
}
