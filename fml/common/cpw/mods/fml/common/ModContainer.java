/*
 * The FML Forge Mod Loader suite.
 * Copyright (C) 2012 cpw
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */
package cpw.mods.fml.common;

import java.io.File;
import java.util.List;
import java.util.Map;

import cpw.mods.fml.common.ModContainer.SourceType;

/**
 * The container that wraps around mods in the system.
 * <p>The philosophy is that individual mod implementation technologies should not impact the actual loading and management
 * of mod code. This interface provides a mechanism by which we can wrap actual mod code so that the loader and other
 * facilities can treat mods at arms length.</p> 
 *
 * @author cpw
 *
 */

public interface ModContainer
{
    public enum ModState {
        UNLOADED("Unloaded"), LOADED("Loaded"), PREINITIALIZED("Pre-initialized"), INITIALIZED("Initialized"), POSTINITIALIZED("Post-initialized"), AVAILABLE("Available");
        private String label;

        private ModState(String label) {
            this.label=label;
        }
        public String toString() {
            return this.label;
        }
    }
    
    public enum SourceType {
        JAR, CLASSPATH, DIR;
    }
    /**
     * The enclosed mod wants to be called during pre-initialization.
     * @return
     */
    boolean wantsPreInit();
    /**
     * The enclosed mod wants to be called during post-initialization.
     * @return
     */
    boolean wantsPostInit();
    /**
     * Called when pre-initialization occurs.
     */
    void preInit();
    /**
     * Called when main initialization occurs.
     */
    void init();
    /**
     * Called when post-initialization occurs.
     */
    void postInit();
    /**
     * The name of the mod
     * @return
     */
    String getName();
    /**
     * The state of the mod
     * @return
     */
    ModState getModState();
    /**
     * Move to the next mod state
     */
    void nextState();
    /**
     * Does this mod match the supplied mod?
     * @param mod
     * @return
     */
    boolean matches(Object mod);
    /**
     * The source of this mod: the file on the file system
     * @return
     */
    File getSource();
    /**
     * Returns the sorting rules as a string for printing
     * @return
     */
    String getSortingRules();
    /**
     * The actual mod object itself
     * @return
     */
    Object getMod();
    /**
     * Lookup the fuel value for the supplied item/damage with this mod.
     * @param itemId
     * @param itemDamage
     * @return
     */
    int lookupFuelValue(int itemId, int itemDamage);
    /**
     * This mod wants to be notified when an object is picked up.
     * @return
     */
    boolean wantsPickupNotification();
    /**
     * The pickup notifier for this mod.
     * @return
     */
    IPickupNotifier getPickupNotifier();
    /**
     * This mod wants to have special dispenser handling.
     * @return
     */
    boolean wantsToDispense();
    /**
     * The dispensing handler.
     * @return
     */
    IDispenseHandler getDispenseHandler();
    /**
     * This mod wants notification of crafting and/or smelting events.
     * @return
     */
    boolean wantsCraftingNotification();
    /**
     * The crafting and smelting handler for this mod.
     * @return
     */
    ICraftingHandler getCraftingHandler();
    /**
     * The strong dependencies of this mod. If the named mods in this list are not present, the game will abort.
     * @return
     */
    List<String> getDependencies();
    /**
     * Get a list of mods to load before this one. The special value "*" indicates to load <i>after</i> all other mods (except other "*" mods).
     * @return
     */
    List<String> getPreDepends();
    /**
     * Get a list of mods to load after this one. The special value "*" indicates to load <i>before</i> all other mods (except other "*" mods).
     * @return
     */
    List<String> getPostDepends();
    /**
     * This mod wants packets from the client, and wants them handled by FML.
     * @return
     */
    boolean wantsNetworkPackets();
    /**
     * The network handler for this mod.
     * @return
     */
    INetworkHandler getNetworkHandler();
    /**
     * Does this mod own this channel?
     * @param channel
     * @return
     */
    boolean ownsNetworkChannel(String channel);
    
    /**
     * Does this mod want commands from the console?
     * @return
     */
    boolean wantsConsoleCommands();
    
    IConsoleHandler getConsoleHandler();
    
    boolean wantsPlayerTracking();
    
    IPlayerTracker getPlayerTracker();
    
    List<IKeyHandler> getKeys();
    
    SourceType getSourceType();

    void setSourceType(SourceType type);
    
    ModMetadata getMetadata();
    void setMetadata(ModMetadata meta);
    /**
     * 
     */
    void gatherRenderers(Map renderers);
    /**
     * 
     */
    void requestAnimations();
    /**
     * @return
     */
    String getVersion();
    /**
     * @return
     */
    ProxyInjector findSidedProxy();
    
    void keyBindEvent(Object keyBinding);
}
