package net.minecraftforge.debug.village;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.jigsaw.*;
import net.minecraft.world.gen.feature.template.ProcessorLists;
import net.minecraftforge.event.world.JigsawPieceEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Optional;

@Mod("jigsaw_event_test")
@Mod.EventBusSubscriber(modid = "jigsaw_event_test")
public class JigsawEventTest
{
   @SubscribeEvent
   public static void jigsawEvent(JigsawPieceEvent event)
   {
      // Replace iron golem with giant in all villages
      if (event.patternLocation.getPath().equals("village/common/iron_golem"))
      {
         event.pieces.clear();
         event.pieces.add(JigsawPiece.single("jigsaw_event_test:giant", ProcessorLists.EMPTY).apply(JigsawPattern.PlacementBehaviour.RIGID));
      }

      // Replace at most one house in every plains village with cube of diamond blocks
      // Force diamond house to always generate, even if this will overwrite existing piece
      boolean isGenerated = (boolean) event.jigsawAssemblerSettings.getOrDefault(new ResourceLocation("jigsaw_event_test:generated"), false);
      if (event.patternLocation.getPath().equals("village/plains/houses") && !isGenerated)
      {
         event.pieces.clear();
         event.pieces.add(JigsawPiece.single("jigsaw_event_test:diamond_house", ProcessorLists.EMPTY).apply(JigsawPattern.PlacementBehaviour.RIGID));

         event.jigsawAssemblerSettings.put(new ResourceLocation("jigsaw_event_test:generated"), true);
         event.jigsawAssemblerSettings.put(new ResourceLocation("forge:force_piece_placement"), true);
      } else {
         event.jigsawAssemblerSettings.put(new ResourceLocation("forge:force_piece_placement"), false);
      }

      // After 5 villagers have been generated in taiga village, replace all future villagers in that village with zombie villagers
      int numberOfVillagers = (int) event.jigsawAssemblerSettings.getOrDefault(new ResourceLocation("jigsaw_event_test:villagers"), 0);
      if (event.patternLocation.getPath().equals("village/taiga/villagers") && numberOfVillagers <= 5)
      {
         event.pieces.clear();
         event.pieces.add(JigsawPiece.single("village/plains/zombie/villagers/baby", ProcessorLists.EMPTY).apply(JigsawPattern.PlacementBehaviour.RIGID));
         event.pieces.add(JigsawPiece.single("village/plains/zombie/villagers/nitwit", ProcessorLists.EMPTY).apply(JigsawPattern.PlacementBehaviour.RIGID));
         event.pieces.add(JigsawPiece.single("village/plains/zombie/villagers/nitwit", ProcessorLists.EMPTY).apply(JigsawPattern.PlacementBehaviour.RIGID));
         event.jigsawAssemblerSettings.put(new ResourceLocation("jigsaw_event_test:villagers"), numberOfVillagers + 1);
      }

      // Replace savanna houses on y>=70 with desert ones
      if (event.patternLocation.getPath().equals("village/savanna/houses") && event.jigsawBlockPosition.getY() >= 70)
      {
         Optional<JigsawPattern> jigsawpattern = event.patternRegistry.getOptional(new ResourceLocation("village/desert/houses"));
         event.pieces.clear();
         event.pieces.addAll(jigsawpattern.get().getShuffledTemplates(event.random));
      }
   }
}
