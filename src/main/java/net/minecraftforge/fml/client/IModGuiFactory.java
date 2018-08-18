/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

package net.minecraftforge.fml.client;

import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

/**
 * This is the interface you need to implement if you want to provide a customized config screen.
 * {@link DefaultGuiFactory} provides a default implementation of this interface and will be used
 * if the mod does not specify anything else.
 */
public interface IModGuiFactory {
    /**
     * Called when instantiated to initialize with the active minecraft instance.
     *
     * @param minecraftInstance the instance
     */
    void initialize(Minecraft minecraftInstance);
    
    /**
     * If this method returns false, the config button in the mod list will be disabled
     * @return true if this object provides a config gui screen, false otherwise
     */
    boolean hasConfigGui();
    
    /**
     * Return an initialized {@link GuiScreen}. This screen will be displayed
     * when the "config" button is pressed in the mod list. It will
     * have a single argument constructor - the "parent" screen, the same as all
     * Minecraft GUIs. The expected behaviour is that this screen will replace the
     * "mod list" screen completely, and will return to the mod list screen through
     * the parent link, once the appropriate action is taken from the config screen.
     *
     * This config GUI is anticipated to provide configuration to the mod in a friendly
     * visual way. It should not be abused to set internals such as IDs (they're gonna
     * keep disappearing anyway), but rather, interesting behaviours. This config GUI
     * is never run when a server game is running, and should be used to configure
     * desired behaviours that affect server state. Costs, mod game modes, stuff like that
     * can be changed here.
     *
     * @param parentScreen The screen to which must be returned when closing the
     * returned screen.
     * @return A class that will be instantiated on clicks on the config button
     *  or null if no GUI is desired.
     */
    GuiScreen createConfigGui(GuiScreen parentScreen);

    /**
     * Return a list of the "runtime" categories this mod wishes to populate with
     * GUI elements.
     *
     * Runtime categories are created on demand and organized in a 'lite' tree format.
     * The parent represents the parent node in the tree. There is one special parent
     * 'Help' that will always list first, and is generally meant to provide Help type
     * content for mods. The remaining parents will sort alphabetically, though
     * this may change if there is a lot of alphabetic abuse. "AAA" is probably never a valid
     * category parent.
     *
     * Runtime configuration itself falls into two flavours: in-game help, which is
     * generally non interactive except for the text it wishes to show, and client-only
     * affecting behaviours. This would include things like toggling minimaps, or cheat modes
     * or anything NOT affecting the behaviour of the server. Please don't abuse this to
     * change the state of the server in any way, this is intended to behave identically
     * when the server is local or remote.
     *
     * @return the set of options this mod wishes to have available, or empty if none
     */
    Set<RuntimeOptionCategoryElement> runtimeGuiCategories();

    /**
     * Represents an option category and entry in the runtime gui options list.
     *
     * @author cpw
     *
     */
    public static class RuntimeOptionCategoryElement {
        public final String parent;
        public final String child;

        public RuntimeOptionCategoryElement(String parent, String child)
        {
            this.parent = parent;
            this.child = child;
        }
    }
}
