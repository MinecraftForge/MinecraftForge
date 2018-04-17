package net.minecraftforge.event.advancements;

import java.util.Map;

import net.minecraft.advancements.Advancement;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * This Event is fired when Forge starts doing its thing with Advancements, like on server start and on reload.
 * The provided Map is a direct reference to the Advancement Builder map, so you can directly modify it to inject custom Advancement Builders.
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 */
public class AdvancementLoadEvent extends Event
{

    public final Map<ResourceLocation, Advancement.Builder> advancementMap;

    public AdvancementLoadEvent(Map<ResourceLocation, Advancement.Builder> advancementMap)
    {
        this.advancementMap = advancementMap;
    }

    /**
     * This Event is fired before Forge loads mods' advancements but after Vanilla loaded its advancements
     */
    public static class Pre extends AdvancementLoadEvent
    {

        public Pre(Map<ResourceLocation, Advancement.Builder> advancementMap)
        {
            super(advancementMap);
        }
    }

    /**
     * This Event is fired after Forge has completed loading mods' advancements
     */
    public static class Post extends AdvancementLoadEvent
    {

        public Post(Map<ResourceLocation, Advancement.Builder> advancementMap)
        {
            super(advancementMap);
        }
    }
}
