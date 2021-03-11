package net.minecraftforge.client.gui;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class OverlayRegistry
{
    public static synchronized IIngameOverlay registerOverlay(int priority, String displayName, IIngameOverlay overlay)
    {
        OverlayEntry entry = overlays.get(overlay);

        if (entry != null)
        {
            if (entry.priority == priority)
                return overlay;

            overlaysOrdered.remove(entry);
        }

        int insertAt;
        for(insertAt=0; insertAt< overlaysOrdered.size(); insertAt++)
        {
            OverlayEntry ov = overlaysOrdered.get(insertAt);
            if (ov.getPriority() > priority)
            {
                break;
            }
        }

        entry = new OverlayEntry(priority, overlay, displayName);

        overlaysOrdered.add(insertAt, entry);
        overlays.put(overlay, entry);

        return overlay;
    }

    public static synchronized void enableOverlay(IIngameOverlay overlay, boolean enable)
    {
        OverlayEntry entry = overlays.get(overlay);
        if (entry != null)
        {
            entry.setEnabled(enable);
        }
    }

    public static synchronized void removeOverlay(IIngameOverlay overlay)
    {
        OverlayEntry entry = overlays.get(overlay);
        if (entry != null)
        {
            overlays.remove(overlay);
            overlaysOrdered.remove(entry);
        }
    }

    private static final Map<IIngameOverlay, OverlayEntry> overlays = Maps.newHashMap();
    private static final List<OverlayEntry> overlaysOrdered = Lists.newArrayList();

    public static Stream<IIngameOverlay> getOrdered()
    {
        return overlaysOrdered.stream().map(o -> o.overlay);
    }

    public static List<OverlayEntry> orderedEntries()
    {
        return Collections.unmodifiableList(overlaysOrdered);
    }

    public static class OverlayEntry {

        private final int priority;
        private final IIngameOverlay overlay;
        private final String displayName;
        private boolean enabled = true;

        public OverlayEntry(int priority, IIngameOverlay overlay, String displayName)
        {
            this.priority = priority;
            this.overlay = overlay;
            this.displayName = displayName;
        }

        public int getPriority()
        {
            return priority;
        }

        public IIngameOverlay getOverlay()
        {
            return overlay;
        }

        public String getDisplayName()
        {
            return displayName;
        }

        public boolean isEnabled()
        {
            return enabled;
        }

        // Call via OverlayRegistry.enableOverlay
        private void setEnabled(boolean enabled)
        {
            this.enabled = enabled;
        }
    }

}
