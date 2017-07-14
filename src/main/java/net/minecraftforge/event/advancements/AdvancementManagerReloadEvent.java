package net.minecraftforge.event.advancements;

import java.util.Map;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * This is fired in {@link AdvancementManager#reload()} after JSON advancements have been (re)loaded.
 * Add dynamically generated advancements to the map returned by {@link #getAdvMap()}.<br>
 * <br>
 * Ex.
 * <code>
 * <pre>
 * &#064;Mod.EventBusSubscriber(modid = "randommodid")
 * public static class LoremIpsum {
 *   &#064;SubscribeEvent
 *   public static void onAdvReload(AdvancementManagerReloadEvent event) {
 *     event.getAdvMap().add(YOUR_ADVANCEMENT_ID, YOUR_ADVANCEMENT_BUILDER); // An advancement builder for an advancement can be obtained using {@link Advancement#copy()}.
 *   }
 * }
 * </pre>
 * </code>
 * 
 * @author Landmaster
 */
public class AdvancementManagerReloadEvent extends Event
{
    private final AdvancementManager instance;
    private final Map<ResourceLocation, Advancement.Builder> advMap;

    public AdvancementManagerReloadEvent(AdvancementManager instance, Map<ResourceLocation, Advancement.Builder> advMap)
    {
        this.instance = instance;
        this.advMap = advMap;
    }

    /**
     * <strong>NOTE: Do not use {@code event.getAdvManager().getAdvancement(rl)}; use {@code event.getAdvMap().get(rl).build(rl)} instead</strong>
     * @return the advancement manager
     */
    public AdvancementManager getAdvManager()
    {
        return this.instance;
    }

    /**
     * Add your advancements, each as an {@link Advancement.Builder} (obtainable via {@link Advancement#copy()}), to this map.
     * @return a mutable {@link Map} of advancement ids to advancement builders
     */
    public Map<ResourceLocation, Advancement.Builder> getAdvMap()
    {
        return this.advMap;
    }
}
