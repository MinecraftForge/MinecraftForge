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

package net.minecraftforge.client.gui;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class OverlayRegistry
{
    /**
     * Adds a new overlay entry to the registry, placed at the beginning of the list.
     * Call from {@link net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent}. No need for enqueueWork.
     * @param displayName A string for debug purposes, used primarily in exception traces coming from the overlays.
     * @param overlay An instance, lambda or method reference for the logic used in rendering the overlay.
     * @return The same object passed into the {@code overlay} parameter.
     */
    public static synchronized IIngameOverlay registerOverlayBottom(@Nonnull String displayName, @Nonnull IIngameOverlay overlay)
    {
        return registerOverlay(-1, null, displayName, overlay);
    }

    /**
     * Adds a new overlay entry to the registry, placed before the {@code other} parameter in the list.
     * Call from {@link net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent}. No need for enqueueWork.
     * @param other The overlay to insert before. The overlay must be registered.
     * @param displayName A string for debug purposes, used primarily in exception traces coming from the overlays.
     * @param overlay An instance, lambda or method reference for the logic used in rendering the overlay.
     * @return The same object passed into the {@code overlay} parameter.
     */
    public static synchronized IIngameOverlay registerOverlayBelow(@Nonnull IIngameOverlay other, @Nonnull String displayName, @Nonnull IIngameOverlay overlay)
    {
        return registerOverlay(-1, other, displayName, overlay);
    }

    /**
     * Adds a new overlay entry to the registry, placed after the {@code other} parameter in the list.
     * Call from {@link net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent}. No need for enqueueWork.
     * @param other The overlay to insert after. The overlay must be registered.
     * @param displayName A string for debug purposes, used primarily in exception traces coming from the overlays.
     * @param overlay An instance, lambda or method reference for the logic used in rendering the overlay.
     * @return The same object passed into the {@code overlay} parameter.
     */
    public static synchronized IIngameOverlay registerOverlayAbove(@Nonnull IIngameOverlay other, @Nonnull String displayName, @Nonnull IIngameOverlay overlay)
    {
        return registerOverlay(1, other, displayName, overlay);
    }

    /**
     * Adds a new overlay entry to the registry, placed at the end of the list.
     * Call from {@link net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent}. No need for enqueueWork.
     * @param displayName A string for debug purposes, used primarily in exception traces coming from the overlays.
     * @param overlay An instance, lambda or method reference for the logic used in rendering the overlay.
     * @return The same object passed into the {@code overlay} parameter.
     */
    public static synchronized IIngameOverlay registerOverlayTop(@Nonnull String displayName, @Nonnull IIngameOverlay overlay)
    {
        return registerOverlay(1, null, displayName, overlay);
    }

    private static IIngameOverlay registerOverlay(int sort, @Nullable IIngameOverlay other, @Nonnull String displayName, @Nonnull IIngameOverlay overlay)
    {
        OverlayEntry entry = overlays.get(overlay);

        if (entry != null)
        {
            overlaysOrdered.remove(entry);
        }

        int insertAt = overlays.size();
        if (other == null)
        {
            if (sort < 0)
                insertAt = 0;
        }
        else
        {
            for (int i = 0; i < overlaysOrdered.size(); i++)
            {
                OverlayEntry ov = overlaysOrdered.get(i);
                if (ov.getOverlay() == other)
                {
                    if (sort < 0) insertAt = i;
                    else insertAt = i+1;
                    break;
                }
            }
        }

        entry = new OverlayEntry(overlay, displayName);

        overlaysOrdered.add(insertAt, entry);
        overlays.put(overlay, entry);

        return overlay;
    }

    /**
     * Enables or disables an overlay. This is preferred over removing overlays.
     * @param overlay The overlay object to enable or disable
     * @param enable The new state
     */
    public static synchronized void enableOverlay(@Nonnull IIngameOverlay overlay, boolean enable)
    {
        OverlayEntry entry = overlays.get(overlay);
        if (entry != null)
        {
            entry.setEnabled(enable);
        }
    }

    /**
     * Returns the information on a registered overlay.
     * @param overlay The overlay to obtain the information for.
     * @return The registration entry for this overlay.
     */
    @Nullable
    public static synchronized OverlayEntry getEntry(@Nonnull IIngameOverlay overlay)
    {
        return overlays.get(overlay);
    }

    /**
     * @return Returns an unmodifiable view of the ordered list of entries.
     */
    public static List<OverlayEntry> orderedEntries()
    {
        return Collections.unmodifiableList(overlaysOrdered);
    }

    private static final Map<IIngameOverlay, OverlayEntry> overlays = Maps.newHashMap();
    private static final List<OverlayEntry> overlaysOrdered = Lists.newArrayList();

    public static class OverlayEntry {
        private final IIngameOverlay overlay;
        private final String displayName;
        private boolean enabled = true;

        public OverlayEntry(IIngameOverlay overlay, String displayName)
        {
            this.overlay = overlay;
            this.displayName = displayName;
        }

        /**
         * @return Returns the overlay renderer.
         */
        public IIngameOverlay getOverlay()
        {
            return overlay;
        }

        /**
         * @return Returns the debug name for this entry.
         */
        public String getDisplayName()
        {
            return displayName;
        }

        /**
         * @return Returns true if the entry will be rendered.
         */
        public boolean isEnabled()
        {
            return enabled;
        }

        /**
         * For internal use. Call via {@link #enableOverlay(IIngameOverlay, boolean)}.
         */
        private void setEnabled(boolean enabled)
        {
            this.enabled = enabled;
        }
    }

}
