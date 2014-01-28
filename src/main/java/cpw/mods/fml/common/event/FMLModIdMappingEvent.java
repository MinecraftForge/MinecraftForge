package cpw.mods.fml.common.event;

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
        public ModRemapping(int oldId, int newId, String tag)
        {
            this.oldId = oldId;
            this.newId = newId;
            this.tag = tag.substring(1);
            this.remapTarget = tag.charAt(0) == '\u0001' ? RemapTarget.BLOCK : RemapTarget.ITEM;
        }

    }
    public final ImmutableList<ModRemapping> remappedIds;

    public FMLModIdMappingEvent(Map<String, Integer[]> mappings)
    {
        List<ModRemapping> remappings = Lists.newArrayList();
        for (Entry<String, Integer[]> mapping : mappings.entrySet())
        {
            remappings.add(new ModRemapping(mapping.getValue()[0], mapping.getValue()[1], mapping.getKey()));
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