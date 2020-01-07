package net.minecraftforge.event.world;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.jigsaw.JigsawManager;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.structure.Structures;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;

/**
 * This event should be fired whenever a Jigsaw bases structure initializes its pools.
 * Use this event to register custom JigsawPattern before the corresponding structure is constructed.
 */
public abstract class StructureJigsawPoolInitEvent extends Event
{

    public void register(JigsawPattern jigsawPattern)
    {
        JigsawManager.field_214891_a.register(jigsawPattern);
    }

    public abstract ResourceLocation getStructureRegistryName();

    public static class Village extends StructureJigsawPoolInitEvent{
        private static boolean fired = false;

        public static void fire()
        {
            if(!fired)
            {
                MinecraftForge.EVENT_BUS.post(new Village());
                fired = true;
            }
        }

        @Override
        public ResourceLocation getStructureRegistryName()
        {
            return Structures.VILLAGE.getRegistryName();
        }
    }

    public static class PillageOutpost extends StructureJigsawPoolInitEvent
    {
        @Override
        public ResourceLocation getStructureRegistryName()
        {
            return Structures.PILLAGER_OUTPOST.getRegistryName();
        }
    }
}
