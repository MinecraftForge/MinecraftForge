package net.minecraftforge.debug.world;

import net.minecraft.data.worldgen.ProcessorLists;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.structures.StructurePoolElement;
import net.minecraft.world.level.levelgen.feature.structures.StructureTemplatePool;
import net.minecraftforge.event.world.JigsawPieceEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Optional;

@Mod("jigsaw_event_test")
@Mod.EventBusSubscriber(modid = "jigsaw_event_test")
public class JigsawPieceEventTest
{
   @SubscribeEvent
   public static void jigsawEvent(JigsawPieceEvent event)
   {
      // Replace iron golem with giant in all villages
      if (event.patternLocation.getPath().equals("village/common/iron_golem"))
      {
         event.pieces.clear();
         event.pieces.add(StructurePoolElement.single("jigsaw_event_test:giant", ProcessorLists.EMPTY).apply(StructureTemplatePool.Projection.RIGID));
      }

      // Replace at most one house in every plains village with cube of diamond blocks
      // Force diamond house to always generate, even if this will overwrite existing piece
      boolean isGenerated = (boolean) event.jigsawPlacerSettings.getOrDefault(new ResourceLocation("jigsaw_event_test:generated"), false);
      if (event.patternLocation.getPath().equals("village/plains/houses") && !isGenerated)
      {
         event.pieces.clear();
         event.pieces.add(StructurePoolElement.single("jigsaw_event_test:diamond_house", ProcessorLists.EMPTY).apply(StructureTemplatePool.Projection.RIGID));

         event.jigsawPlacerSettings.put(new ResourceLocation("jigsaw_event_test:generated"), true);
         event.jigsawPlacerSettings.put(new ResourceLocation("forge:force_piece_placement"), true);
      } else {
         event.jigsawPlacerSettings.put(new ResourceLocation("forge:force_piece_placement"), false);
      }

      // After 5 villagers have been generated in taiga village, replace all future villagers in that village with zombie villagers
      int numberOfVillagers = (int) event.jigsawPlacerSettings.getOrDefault(new ResourceLocation("jigsaw_event_test:villagers"), 0);
      if (event.patternLocation.getPath().equals("village/taiga/villagers") && numberOfVillagers <= 5)
      {
         event.pieces.clear();
         event.pieces.add(StructurePoolElement.single("village/plains/zombie/villagers/baby", ProcessorLists.EMPTY).apply(StructureTemplatePool.Projection.RIGID));
         event.pieces.add(StructurePoolElement.single("village/plains/zombie/villagers/nitwit", ProcessorLists.EMPTY).apply(StructureTemplatePool.Projection.RIGID));
         event.pieces.add(StructurePoolElement.single("village/plains/zombie/villagers/nitwit", ProcessorLists.EMPTY).apply(StructureTemplatePool.Projection.RIGID));
         event.jigsawPlacerSettings.put(new ResourceLocation("jigsaw_event_test:villagers"), numberOfVillagers + 1);
      }

      // Replace savanna houses on y>=70 with desert ones
      if (event.patternLocation.getPath().equals("village/savanna/houses") && event.jigsawBlockPosition.getY() >= 70)
      {
         Optional<StructureTemplatePool> jigsawpattern = event.patternRegistry.getOptional(new ResourceLocation("village/desert/houses"));
         event.pieces.clear();
         event.pieces.addAll(jigsawpattern.get().getShuffledTemplates(event.random));
      }
   }
}
