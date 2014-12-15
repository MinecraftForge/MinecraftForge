package net.minecraftforge.fml.common.event;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class FMLModIdMappingEvent extends FMLEvent {
    public static enum RemapTarget { BLOCK, ITEM }
    public class ModRemapping
    {
        public final int oldId;
        public final int newId;
        public final String tag;
        public final RemapTarget remapTarget;
        public ModRemapping(int oldId, int newId, String tag, RemapTarget type)
        {
            this.oldId = oldId;
            this.newId = newId;
            this.tag = tag;
            this.remapTarget = type;
        }

    }
    public final ImmutableList<ModRemapping> remappedIds;

    public FMLModIdMappingEvent(Map<String, Integer[]> blocks, Map<String, Integer[]> items)
    {
        List<ModRemapping> remappings = Lists.newArrayList();
        for (Entry<String, Integer[]> mapping : blocks.entrySet())
        {
            remappings.add(new ModRemapping(mapping.getValue()[0], mapping.getValue()[1], mapping.getKey(), RemapTarget.BLOCK));
        }
        for (Entry<String, Integer[]> mapping : items.entrySet())
        {
            remappings.add(new ModRemapping(mapping.getValue()[0], mapping.getValue()[1], mapping.getKey(), RemapTarget.ITEM));
        }

        Collections.sort(remappings, new Comparator<ModRemapping>() {
            @Override
            public int compare(ModRemapping o1, ModRemapping o2)
            {
                return (o1.newId < o2.newId) ? -1 : ((o1.newId == o2.newId) ? 0 : 1);
            }
        });
        remappedIds = ImmutableList.copyOf(remappings);
    }
}