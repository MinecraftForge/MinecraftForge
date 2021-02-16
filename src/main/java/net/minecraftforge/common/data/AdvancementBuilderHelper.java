package net.minecraftforge.common.data;

import com.google.common.collect.Maps;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.resources.ResourcePackType;

public class AdvancementBuilderHelper {

    public static boolean build(ExistingFileHelper fileHelper, Advancement.Builder builder){
        return builder.resolveParent((advancementId) -> {
            if(fileHelper.exists(advancementId, ResourcePackType.SERVER_DATA, ".json", "advancements")) {
                return new Advancement(advancementId, null, null, AdvancementRewards.EMPTY, Maps.newHashMap(), null);
            }
            return null;
        });
    }
}
